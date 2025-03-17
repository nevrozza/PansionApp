package groups.students

import AdminRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.listDialog.ListComponent
import components.networkInterface.NetworkInterface
import deviceSupport.launchIO
import deviceSupport.withIO
import deviceSupport.withMain
import groups.students.StudentsStore.Intent
import groups.students.StudentsStore.Label
import groups.students.StudentsStore.Message
import groups.students.StudentsStore.State
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StudentsExecutor(
    private val adminRepository: AdminRepository,
    private val formsListComponent: ListComponent,
    private val nStudentsInterface: NetworkInterface,
    private val nStudentGroupsInterface: NetworkInterface
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeAction(action: Unit) {
        changeCurrentFormTab(0)
    }

    override fun executeIntent(intent: Intent) {
        when (intent) {
            is Intent.BindStudentToForm -> bindStudentToForm(state(), intent.formId)
            is Intent.ClickOnFormTab -> changeCurrentFormTab(intent.formId)
            is Intent.ClickOnStudent -> changeStudent(intent.studentLogin)
            is Intent.ClickOnStudentPlus -> dispatch(Message.OnStudentPlusClicked(intent.studentLogin))

            Intent.OpenFormGroupCreationMenu -> dispatch(Message.FormGroupCreatingMenuOpened)
            Intent.CloseFormGroupCreationMenu -> dispatch(Message.FormGroupCreationMenuClosed)

            is Intent.ChangeCFormGroupGroupId -> dispatch(Message.CFormGroupGroupIdChanged(intent.groupId))
            is Intent.ChangeCFormGroupSubjectId -> changeCFormGroupSubjectId(intent.subjectId)
            Intent.CreateFormGroup -> createFormGroup(state())

            is Intent.DeleteStudentGroup -> deleteStudentGroup(
                login = intent.login,
                subjectId = intent.subjectId,
                groupId = intent.groupId,
                afterAll = intent.afterAll
            )
        }
    }


    private fun deleteStudentGroup(
        subjectId: Int,
        groupId: Int,
        login: String,
        afterAll: () -> Unit
    ) {
        scope.launchIO {
//            dispatch(Message.CreatingProcessStarted)
            nStudentGroupsInterface.nStartLoading()
            try {
                adminRepository.deleteStudentGroup(
                    studentLogin = login,
                    subjectId = subjectId,
                    groupId = groupId
                )
                withMain {
                    dispatch(Message.FormGroupCreated)
                    afterAll()
                }
            } catch (e: Throwable) {
                with(nStudentGroupsInterface) {
                    nError("Что-то пошло не так =/", e, onFixErrorClick = {
                        goToNone()
                    })
                }
            }
            try {
                updateStudentsGroups(state().chosenStudentLogin)
            } catch (e: Throwable) {
                nStudentGroupsInterface.nError("Что-то пошло не так =/", e, onFixErrorClick = {
                    this.launchIO {
                        updateStudentsGroups(state().chosenStudentLogin)
                    }
                })
            }

        }
    }

    private fun createFormGroup(state: State) {
        scope.launchIO {
            nStudentGroupsInterface.nStartLoading()
            try {
                adminRepository.createStudentGroup(
                    studentLogin = state.chosenStudentLogin,
                    subjectId = state.cFormGroupSubjectId,
                    groupId = state.cFormGroupGroupId
                )
                withMain {
                    dispatch(Message.FormGroupCreated)
                }
            } catch (e: Throwable) {
                with(nStudentGroupsInterface) {
                    nError("Что-то пошло не так =/", e, onFixErrorClick = {
                        goToNone()
                    })
                }
            }
            try {
                updateStudentsGroups(state().chosenStudentLogin)
            } catch (e: Throwable) {
                nStudentGroupsInterface.nError("Что-то пошло не так =/", e, onFixErrorClick = {
                    this.launchIO {
                        updateStudentsGroups(state().chosenStudentLogin)
                    }
                })
            }

        }
    }

    private fun changeCFormGroupSubjectId(subjectId: Int) {
        dispatch(Message.CFormGroupSubjectIdChanged(subjectId))
        scope.launchIO {
            try {
                val cutedGroups =
                    adminRepository.fetchCutedGroups(subjectId).groups.filter { it.groupId !in state().studentGroups.map { it.id } }

                withMain {
                    dispatch(Message.CFormGroupSubjectIdChangedAtAll(subjectId, cutedGroups))
                }
            } catch (_: Throwable) {
//                creatingFormBottomSheet.nInterface.nError("Не удалось загрузить список") {
//                    updateMentors()
//                }
            }
        }

    }

    private fun changeStudent(login: String) {

        scope.launch {
            if (login != state().chosenStudentLogin) {
                dispatch(Message.OnStudentClicked(login))
                withIO {
                    updateStudentsGroups(login)
                }
            } else {
                dispatch(Message.OnStudentClicked(""))
            }
        }

    }

    private fun changeCurrentFormTab(formId: Int) {
        dispatch(Message.ChosenFormChanged(formId))
        scope.launchIO {
            updateStudentsInForm(formId)
        }
    }

    private fun bindStudentToForm(state: State, formId: Int) {
        scope.launchIO {
//            formListComponent.onEvent(ListDialogStore.Intent.StartProcess)
            formsListComponent.nInterface.nStartLoading()
            try {
                adminRepository.bindStudentToForm(
                    login = state.chosenStudentPlusLogin,
                    formId = formId
                )

//                dispatch(GroupsStore.Message.UserFormCreated(students))

                with(formsListComponent) {
                    withMain {
                        onEvent(components.listDialog.ListDialogStore.Intent.HideDialog)
                    }
                    delay(200)
                    nInterface.nSuccess()
                }
            } catch (e: Throwable) {
                formsListComponent.nInterface.nError(
                    "Что-то пошло не так =/",
                    e,
                    onFixErrorClick = {
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
            withMain {
                if (studentLogin == state().chosenStudentLogin) {
                    dispatch(Message.StudentGroupsUpdated(groups))
                    nStudentGroupsInterface.nSuccess()
                }
            }
        } catch (e: Throwable) {
            nStudentGroupsInterface.nError("Что-то пошло не так", e, onFixErrorClick = {
                scope.launchIO {
                    updateStudentsGroups(studentLogin)
                }
            })
        }
    }

    private suspend fun updateStudentsInForm(formId: Int) {
        try {
            nStudentsInterface.nStartLoading()
            val students = adminRepository.fetchStudentsInForm(formId).students
            withMain {
                if (state().chosenFormTabId == formId) {
                    dispatch(Message.StudentsUpdated(students))
                    nStudentsInterface.nSuccess()
                }
            }
        } catch (e: Throwable) {
            nStudentsInterface.nError("Что-то пошло не так", e, onFixErrorClick = {
                scope.launchIO {
                    updateStudentsInForm(formId)
                }
            })
        }
    }
}
