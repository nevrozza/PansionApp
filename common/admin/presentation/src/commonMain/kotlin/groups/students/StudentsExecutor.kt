package groups.students

import AdminRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.networkInterface.NetworkInterface
import components.listDialog.ListComponent
import groups.students.StudentsStore.Intent
import groups.students.StudentsStore.Label
import groups.students.StudentsStore.State
import groups.students.StudentsStore.Message
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StudentsExecutor(
    private val adminRepository: AdminRepository,
    private val formsListComponent: ListComponent,
    private val nStudentsInterface: NetworkInterface,
    private val nStudentGroupsInterface: NetworkInterface
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent, getState: () -> State) {
        when (intent) {
            is Intent.BindStudentToForm -> bindStudentToForm(getState(), intent.formId)
            is Intent.ClickOnFormTab -> changeCurrentFormTab(intent.formId)
            is Intent.ClickOnStudent -> changeStudent(intent.studentLogin)
            is Intent.ClickOnStudentPlus -> dispatch(Message.OnStudentPlusClicked(intent.studentLogin))
        }
    }

    private fun changeStudent(login: String) {

        scope.launch {
            dispatch(Message.OnStudentClicked(login))
            updateStudentsGroups(login)
        }

    }

    private fun changeCurrentFormTab(formId: Int) {
        scope.launch {
            dispatch(Message.ChosenFormChanged(formId))
            updateStudentsInForm(formId)
        }
    }

    private fun bindStudentToForm(state: State, formId: Int) {
        scope.launch {
//            formListComponent.onEvent(ListDialogStore.Intent.StartProcess)
            formsListComponent.nInterface.nStartLoading()
            try {
                adminRepository.bindStudentToForm(
                    login = state.chosenStudentPlusLogin,
                    formId = formId
                )

//                dispatch(GroupsStore.Message.UserFormCreated(students))

                with(formsListComponent) {
                    onEvent(components.listDialog.ListDialogStore.Intent.HideDialog)
                    delay(200)
                    nInterface.nSuccess()
                }
            } catch (_: Throwable) {
                formsListComponent.nInterface.nError("Что-то пошло не так =/", onFixErrorClick = {
                    bindStudentToForm(state, formId)
                })
            }
            updateStudentsInForm(state.chosenFormTabId)
        }
    }

    private suspend fun updateStudentsGroups(studentLogin: String) {
        try {
            nStudentGroupsInterface.nStartLoading()
            val groups = adminRepository.fetchStudentGroups(studentLogin).groups
            dispatch(Message.StudentGroupsUpdated(groups))
            nStudentGroupsInterface.nSuccess()
        } catch (e: Throwable) {
            nStudentGroupsInterface.nError("Что-то пошло не так", onFixErrorClick = {
                scope.launch {
                    updateStudentsGroups(studentLogin)
                }
            })
        }
    }

    private suspend fun updateStudentsInForm(formId: Int) {
        try {
            nStudentsInterface.nStartLoading()
            val students = adminRepository.fetchStudentsInForm(formId).students
            dispatch(Message.StudentsUpdated(students))
            nStudentsInterface.nSuccess()
        } catch (e: Throwable) {
            nStudentsInterface.nError("Что-то пошло не так", onFixErrorClick = {
                scope.launch {
                    updateStudentsInForm(formId)
                }
            })
        }
    }
}
