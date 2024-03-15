package groups.forms

import AdminRepository
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
    private val creatingFormBottomSheet: CBottomSheetComponent
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(i: Intent, getState: () -> State) {
        when (i) {
            is Intent.ClickOnForm -> chooseForm(i.formId)

            is Intent.ChangeCFormTitle -> dispatch(Message.CFormTitleChanged(i.title))
            is Intent.ChangeCFormClassNum -> dispatch(Message.CFormClassNumChanged(i.classNum))
            is Intent.ChangeCFormShortTitle -> dispatch(Message.CFormShortTitleChanged(i.shortTitle))
            Intent.CreateForm -> createForm(getState())

            Intent.OpenFormGroupCreationMenu -> dispatch(Message.FormGroupCreatingMenuOpened)
            Intent.CloseFormGroupCreationMenu -> dispatch(Message.FormGroupCreationMenuClosed)

            is Intent.ChangeCFormGroupGroupId -> dispatch(Message.CFormGroupGroupIdChanged(i.groupId))
            is Intent.ChangeCFormGroupSubjectId -> dispatch(Message.CFormGroupSubjectIdChanged(i.subjectId))
            is Intent.ChangeCFormMentorLogin -> dispatch(Message.CFormMentorLoginChanged(i.mentorLogin))
            Intent.CreateFormGroup -> createFormGroup(getState())
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
