package groups.forms

import AdminRepository
import CDispatcher
import admin.groups.forms.FormInit
import admin.groups.forms.outside.REditFormReceive
import admin.groups.subjects.REditGroupReceive
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.networkInterface.NetworkInterface
import components.cBottomSheet.CBottomSheetComponent
import groups.forms.FormsStore.Intent
import groups.forms.FormsStore.Label
import groups.forms.FormsStore.State
import groups.forms.FormsStore.Message
import kotlinx.coroutines.launch

class FormsExecutor(
    private val adminRepository: AdminRepository,
    private val nFormGroupsInterface: NetworkInterface,
    private val updateForms: () -> Unit,
    private val creatingFormBottomSheet: CBottomSheetComponent,
    private val editFormBottomSheet: CBottomSheetComponent,
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(i: Intent) {
        when (i) {
            is Intent.ClickOnForm -> chooseForm(i.formId)

            is Intent.ChangeCFormTitle -> dispatch(Message.CFormTitleChanged(i.title))
            is Intent.ChangeCFormClassNum -> dispatch(Message.CFormClassNumChanged(i.classNum))
            is Intent.ChangeCFormShortTitle -> dispatch(Message.CFormShortTitleChanged(i.shortTitle))
            Intent.CreateForm -> createForm(state())

            Intent.OpenFormGroupCreationMenu -> dispatch(Message.FormGroupCreatingMenuOpened)
            Intent.CloseFormGroupCreationMenu -> dispatch(Message.FormGroupCreationMenuClosed)

            is Intent.ChangeCFormGroupGroupId -> dispatch(Message.CFormGroupGroupIdChanged(i.groupId))
            is Intent.ChangeCFormGroupSubjectId -> changeCFormGroupSubjectId(i.subjectId)
            is Intent.ChangeCFormMentorLogin -> dispatch(Message.CFormMentorLoginChanged(i.mentorLogin))
            Intent.CreateFormGroup -> createFormGroup(state())
            Intent.UpdateMentors -> updateMentors()
            is Intent.DeleteFormGroup -> deleteFormGroup(
                subjectId = i.subjectId,
                groupId = i.groupId
            )

            is Intent.ChangeEFormClassNum -> dispatch(Message.ChangeEFormClassNum(i.classNum))
            is Intent.ChangeEFormMentorLogin -> dispatch(Message.ChangeEFormMentorLogin(i.mentorLogin))
            is Intent.ChangeEFormShortTitle -> dispatch(Message.ChangeEFormShortTitle(i.shortTitle))
            is Intent.ChangeEFormTitle -> dispatch(Message.ChangeEFormTitle(i.title))
            Intent.EditForm -> editForm()
            is Intent.EditFormInit -> dispatch(Message.EditFormInit(i.formId))
        }
    }

    private fun editForm() {
        scope.launch(CDispatcher) {
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
                scope.launch {
                    updateForms()
                    editFormBottomSheet.fullySuccess()
                }
            } catch (e: Throwable) {
                println("EDITFORMERROR: ${e}")
                editFormBottomSheet.nInterface.nError("Не удалось изменить этот класс") {
                    editFormBottomSheet.nInterface.goToNone()
                }
            }
        }
    }

    private fun changeCFormGroupSubjectId(subjectId: Int) {
        dispatch(Message.CFormGroupSubjectIdChanged(subjectId))
        scope.launch {
            try {
                val cutedGroups =
                    adminRepository.fetchCutedGroups(subjectId).groups.filter { it.groupId !in state().formGroups.map { it.groupId } }

                dispatch(Message.CFormGroupSubjectIdChangedAtAll(subjectId, cutedGroups))
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
        scope.launch {
            try {
                val mentors = adminRepository.fetchAllMentors().mentors
                dispatch(Message.MentorsUpdated(mentors))
                creatingFormBottomSheet.nInterface.nSuccess()
            } catch (_: Throwable) {
                creatingFormBottomSheet.nInterface.nError("Не удалось загрузить список") {
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

//                    dispatch(Message.FormsProcessStarted(formId))
                    dispatch(Message.ChosenFormIdChanged(formId))
                    nFormGroupsInterface.nStartLoading()
                    updateFormGroups(formId)
                } catch (e: Throwable) {
                    println(e)
//                    dispatch(GroupsStore.Message.CurrentFormIdChanged(0, listOf()))
                    nFormGroupsInterface.nError("Что-то пошло не так =/", onFixErrorClick = {
                        this.launch {
                            updateFormGroups(formId)
                        }
                    })
                }
            }
        }
    }

    private fun createForm(state: State) {
        scope.launch {
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


                dispatch(Message.CFormTitleChanged(""))
                dispatch(Message.CFormClassNumChanged(""))
                dispatch(Message.CFormShortTitleChanged(""))
                dispatch(Message.CFormMentorLoginChanged(""))

//                dispatch(GroupsStore.Message.FormCreated(forms))
                creatingFormBottomSheet.fullySuccess()
                //nInterfaceOfSheet
            } catch (_: Throwable) {
//                dispatch(GroupsStore.Message.CreationFormError)
                with(creatingFormBottomSheet.nInterface) {
                    nError("Что-то пошло не так =/", onFixErrorClick = {
                        goToNone()
                    })
                }
                //nInterfaceOfSheetError
            }
            updateForms()
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

            } catch (_: Throwable) {
                with(nFormGroupsInterface) {
                    nError("Что-то пошло не так =/", onFixErrorClick = {
                        goToNone()
                    })
                }
            }
            try {
                updateFormGroups(state().chosenFormId)
            } catch (_: Throwable) {
                nFormGroupsInterface.nError("Что-то пошло не так =/", onFixErrorClick = {
                    this.launch {
                        updateFormGroups(state().chosenFormId)
                    }
                })
            }

        }
    }

    private fun createFormGroup(state: State) {
        scope.launch {
//            dispatch(Message.CreatingProcessStarted)
            nFormGroupsInterface.nStartLoading()
            try {
                adminRepository.createFormGroup(
                    formId = state.chosenFormId,
                    subjectId = state.cFormGroupSubjectId,
                    groupId = state.cFormGroupGroupId
                )
                dispatch(Message.FormGroupCreated)

            } catch (_: Throwable) {
                with(nFormGroupsInterface) {
                    nError("Что-то пошло не так =/", onFixErrorClick = {
                        goToNone()
                    })
                }
            }
            try {
                updateFormGroups(state.chosenFormId)
            } catch (_: Throwable) {
                nFormGroupsInterface.nError("Что-то пошло не так =/", onFixErrorClick = {
                    this.launch {
                        updateFormGroups(state.chosenFormId)
                    }
                })
            }

        }
    }

    private suspend fun updateFormGroups(formId: Int) {
        val groups = adminRepository.fetchFormGroups(formId).groups
        dispatch(Message.FormGroupsUpdated(groups))
        nFormGroupsInterface.nSuccess()
    }
}
