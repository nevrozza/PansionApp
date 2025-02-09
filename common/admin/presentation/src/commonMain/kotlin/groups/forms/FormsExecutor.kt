package groups.forms

import AdminRepository
import admin.groups.forms.FormInit
import admin.groups.forms.outside.REditFormReceive
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.cBottomSheet.CBottomSheetComponent
import components.networkInterface.NetworkInterface
import deviceSupport.launchIO
import deviceSupport.withMain
import groups.forms.FormsStore.Intent
import groups.forms.FormsStore.Label
import groups.forms.FormsStore.Message
import groups.forms.FormsStore.State
import kotlinx.coroutines.launch

class FormsExecutor(
    private val adminRepository: AdminRepository,
    private val nFormGroupsInterface: NetworkInterface,
    private val updateForms: () -> Unit,
    private val creatingFormBottomSheet: CBottomSheetComponent,
    private val editFormBottomSheet: CBottomSheetComponent,
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            is Intent.ClickOnForm -> chooseForm(intent.formId)

            is Intent.ChangeCFormTitle -> dispatch(Message.CFormTitleChanged(intent.title))
            is Intent.ChangeCFormClassNum -> dispatch(Message.CFormClassNumChanged(intent.classNum))
            is Intent.ChangeCFormShortTitle -> dispatch(Message.CFormShortTitleChanged(intent.shortTitle))
            Intent.CreateForm -> createForm(state())

            Intent.OpenFormGroupCreationMenu -> dispatch(Message.FormGroupCreatingMenuOpened)
            Intent.CloseFormGroupCreationMenu -> dispatch(Message.FormGroupCreationMenuClosed)

            is Intent.ChangeCFormGroupGroupId -> dispatch(Message.CFormGroupGroupIdChanged(intent.groupId))
            is Intent.ChangeCFormGroupSubjectId -> changeCFormGroupSubjectId(intent.subjectId)
            is Intent.ChangeCFormMentorLogin -> dispatch(Message.CFormMentorLoginChanged(intent.mentorLogin))
            Intent.CreateFormGroup -> createFormGroup(state())
            Intent.UpdateMentors -> updateMentors()
            is Intent.DeleteFormGroup -> deleteFormGroup(
                subjectId = intent.subjectId,
                groupId = intent.groupId
            )

            is Intent.ChangeEFormClassNum -> dispatch(Message.ChangeEFormClassNum(intent.classNum))
            is Intent.ChangeEFormMentorLogin -> dispatch(Message.ChangeEFormMentorLogin(intent.mentorLogin))
            is Intent.ChangeEFormShortTitle -> dispatch(Message.ChangeEFormShortTitle(intent.shortTitle))
            is Intent.ChangeEFormTitle -> dispatch(Message.ChangeEFormTitle(intent.title))
            Intent.EditForm -> editForm()
            is Intent.EditFormInit -> dispatch(Message.EditFormInit(intent.formId))
        }
    }

    private fun editForm() {
        scope.launchIO {
            try {
                editFormBottomSheet.nInterface.nStartLoading()
                adminRepository.editForm(
                    REditFormReceive(
                        id = state().eFormId,
                        form = FormInit(
                            title = state().eFormTitle,
                            shortTitle = state().eFormShortTitle,
                            mentorLogin = state().eFormMentorLogin,
                            classNum = state().eFormClassNum.toIntOrNull() ?: 1
                        )
                    )
                )
                withMain {
                    updateForms()
                    editFormBottomSheet.fullySuccess()
                }
            } catch (e: Throwable) {
                println("EDITFORMERROR: ${e}")
                editFormBottomSheet.nInterface.nError("Не удалось изменить этот класс", e) {
                    editFormBottomSheet.nInterface.goToNone()
                }
            }
        }
    }

    private fun changeCFormGroupSubjectId(subjectId: Int) {
        dispatch(Message.CFormGroupSubjectIdChanged(subjectId))
        scope.launchIO {
            try {
                val cutedGroups =
                    adminRepository.fetchCutedGroups(subjectId).groups.filter { it.groupId !in state().formGroups.map { it.groupId } }
                withMain {
                    dispatch(Message.CFormGroupSubjectIdChangedAtAll(subjectId, cutedGroups))
                }
//                creatingFormBottomSheet.nInterface.nSuccess()
            } catch (_: Throwable) {
//                creatingFormBottomSheet.nInterface.nError("Не удалось загрузить список") {
//                    updateMentors()
//                }
            }
        }

    }

    private fun updateMentors() {
        creatingFormBottomSheet.nInterface.nStartLoading()
        scope.launchIO {
            try {
                val mentors = adminRepository.fetchAllMentors().mentors
                withMain {
                    dispatch(Message.MentorsUpdated(mentors))
                    creatingFormBottomSheet.nInterface.nSuccess()
                }
            } catch (e: Throwable) {
                creatingFormBottomSheet.nInterface.nError("Не удалось загрузить список", e) {
                    updateMentors()
                }
            }
        }
    }

    private fun chooseForm(formId: Int) {
        if (formId == 0) {
            dispatch(Message.ChosenFormIdChanged(0))
        } else {
            scope.launch {
                try {
                    dispatch(Message.ChosenFormIdChanged(formId))
                    nFormGroupsInterface.nStartLoading()
                    updateFormGroups(formId)
                } catch (e: Throwable) {
                    println(e)
//                    dispatch(GroupsStore.Message.CurrentFormIdChanged(0, listOf()))
                    nFormGroupsInterface.nError("Что-то пошло не так =/", e, onFixErrorClick = {
                        this.launch {
                            updateFormGroups(formId)
                        }
                    })
                }
            }
        }
    }

    private fun createForm(state: State) {
        scope.launchIO {
//            dispatch(GroupsStore.Message.CreatingFormProcessStarted)
            //nInterfaceOfSheetLoading
            creatingFormBottomSheet.nInterface.nStartLoading()
            try {
                adminRepository.createForm(
                    title = state.cFormTitle,
                    mentorLogin = state.cFormMentorLogin,
                    classNum = state.cFormClassNum.toInt(),
                    shortTitle = state.cFormShortTitle
                )

                withMain {
                    dispatch(Message.CFormTitleChanged(""))
                    dispatch(Message.CFormClassNumChanged(""))
                    dispatch(Message.CFormShortTitleChanged(""))
                    dispatch(Message.CFormMentorLoginChanged(""))

//                dispatch(GroupsStore.Message.FormCreated(forms))
                    creatingFormBottomSheet.fullySuccess()
                }
                //nInterfaceOfSheet
            } catch (e: Throwable) {
//                dispatch(GroupsStore.Message.CreationFormError)
                with(creatingFormBottomSheet.nInterface) {
                    nError("Что-то пошло не так =/", e, onFixErrorClick = {
                        goToNone()
                    })
                }
                //nInterfaceOfSheetError
            }
            withMain {
                updateForms()
            }
        }
    }


    private fun deleteFormGroup(subjectId: Int, groupId: Int) {
        scope.launch {
//            dispatch(Message.CreatingProcessStarted)
            nFormGroupsInterface.nStartLoading()
            try {
                adminRepository.deleteFormGroup(
                    formId = state().chosenFormId,
                    subjectId = subjectId,
                    groupId = groupId
                )
//                dispatch(Message.FormGroupCreated)

            } catch (e: Throwable) {
                with(nFormGroupsInterface) {
                    nError("Что-то пошло не так =/", e, onFixErrorClick = {
                        goToNone()
                    })
                }
            }
            try {
                updateFormGroups(state().chosenFormId)
            } catch (e: Throwable) {
                nFormGroupsInterface.nError("Что-то пошло не так =/", e, onFixErrorClick = {
                    this.launch {
                        updateFormGroups(state().chosenFormId)
                    }
                })
            }

        }
    }

    private fun createFormGroup(state: State) {
        scope.launchIO {
//            dispatch(Message.CreatingProcessStarted)
            nFormGroupsInterface.nStartLoading()
            try {
                adminRepository.createFormGroup(
                    formId = state.chosenFormId,
                    subjectId = state.cFormGroupSubjectId,
                    groupId = state.cFormGroupGroupId
                )
                withMain {
                    dispatch(Message.FormGroupCreated)
                }
            } catch (e: Throwable) {
                with(nFormGroupsInterface) {
                    nError("Что-то пошло не так =/", e, onFixErrorClick = {
                        goToNone()
                    })
                }
            }
            withMain {
                try {
                    updateFormGroups(state.chosenFormId)
                } catch (e: Throwable) {
                    nFormGroupsInterface.nError("Что-то пошло не так =/", e, onFixErrorClick = {
                        this.launch {
                            updateFormGroups(state.chosenFormId)
                        }
                    })
                }
            }

        }
    }

    private suspend fun updateFormGroups(formId: Int) {
        val groups = adminRepository.fetchFormGroups(formId).groups
        dispatch(Message.FormGroupsUpdated(groups))
        nFormGroupsInterface.nSuccess()
    }
}
