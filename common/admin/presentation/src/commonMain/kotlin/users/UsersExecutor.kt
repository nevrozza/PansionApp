package users

import AdminRepository
import FIO
import admin.users.ToBeCreatedStudent
import admin.users.User
import admin.users.UserInit
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.cAlertDialog.CAlertDialogComponent
import components.cAlertDialog.CAlertDialogStore
import components.cBottomSheet.CBottomSheetComponent
import components.cBottomSheet.CBottomSheetStore
import components.networkInterface.NetworkInterface
import kotlinx.coroutines.launch
import server.Moderation
import users.UsersStore.Intent
import users.UsersStore.Label
import users.UsersStore.Message
import users.UsersStore.State

class UsersExecutor(
    private val adminRepository: AdminRepository,
    private val nUsersInterface: NetworkInterface,
    private val eUserBottomSheet: CBottomSheetComponent,
    private val cUserBottomSheet: CBottomSheetComponent,
    private val eDeleteDialog: CAlertDialogComponent,
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.FetchUsersInit -> fetchUsers(true)
            Intent.FetchUsers -> fetchUsers()
            is Intent.ChangeDateDialogShowing -> dispatch(Message.DateDialogShowingChanged(intent.isShowing))

            is Intent.ChangeCName -> dispatch(Message.CNameChanged(intent.name))
            is Intent.ChangeCSurname -> dispatch(Message.CSurnameChanged(intent.surname))
            is Intent.ChangeCPraname -> dispatch(Message.CPranameChanged(intent.praname))
            is Intent.ChangeCBirthday -> dispatch(Message.CBirthdayChanged(intent.birthday))
            is Intent.ChangeCRole -> dispatch(Message.CRoleChanged(intent.role))
            is Intent.ChangeCIsModerator -> dispatch(Message.CIsModeratorChanged(intent.isModerator))
            is Intent.ChangeCIsMentor -> dispatch(Message.CIsMentorChanged(intent.isMentor))
            is Intent.ChangeCIsParent -> dispatch(Message.CIsParentChanged(intent.isParent))

            Intent.CreateUser -> createUser(state())
            Intent.ClearUser -> {
                cUserBottomSheet.fullySuccess()
                dispatch(Message.ClearUser)
            }

            is Intent.ChangeEName -> dispatch(Message.ENameChanged(intent.name))
            is Intent.ChangeESurname -> dispatch(Message.ESurnameChanged(intent.surname))
            is Intent.ChangeEPraname -> dispatch(Message.EPranameChanged(intent.praname))
            is Intent.ChangeEBirthday -> dispatch(Message.EBirthdayChanged(intent.birthday))
            is Intent.ChangeERole -> dispatch(Message.ERoleChanged(intent.role))
            is Intent.ChangeEIsModerator -> dispatch(Message.EIsModeratorChanged(intent.isModerator))
            is Intent.ChangeEIsMentor -> dispatch(Message.EIsMentorChanged(intent.isMentor))
            is Intent.ChangeEIsParent -> dispatch(Message.EIsParentChanged(intent.isParent))

            is Intent.OpenEditingSheet -> openEditingSheet(intent.user)
            is Intent.ClearPassword -> clearPassword(state())
            Intent.EditUser -> editUser(state())
            is Intent.ChangeCParentFirstFIO -> dispatch(Message.CParentFirstFIOChanged(intent.fio))
            is Intent.ChangeCParentSecondFIO -> dispatch(Message.CParentSecondFIOChanged(intent.fio))
            is Intent.DeleteAccount -> deleteAccount()
            is Intent.DeleteAccountInit -> scope.launch {
                eDeleteDialog.onEvent(if (intent.login != null) CAlertDialogStore.Intent.ShowDialog else CAlertDialogStore.Intent.HideDialog)
                dispatch(Message.DeletingAccountInit(intent.login))
            }

            is Intent.ChangeCFormId -> dispatch(Message.CFormIdChanged(intent.formId))
            is Intent.FNoAdmin -> dispatch(Message.FNoAdmin(intent.isOn))
            is Intent.FOther -> dispatch(Message.FOther(intent.isOn))
            is Intent.FStudents -> dispatch(Message.FStudents(intent.isOn))
            is Intent.FTeachers -> dispatch(Message.FTeachers(intent.isOn))
            is Intent.FInActive -> dispatch(Message.FInactive(intent.isOn))
            is Intent.FParents -> dispatch(Message.FParents(intent.isOn))
            is Intent.ChangeCSubjectId -> dispatch(Message.CSubjectIdChanged(intent.subjectId))
            is Intent.CreateUsers -> createUsersFromExcel(intent.users)
            is Intent.UpdateUserFind -> dispatch(Message.UserFindUpdate(intent.data))
            is Intent.ChangeESubjectId -> dispatch(Message.ESubjectIdChange(intent.subjectId))
        }
    }

    private fun createUsersFromExcel(students: List<ToBeCreatedStudent>) {
        scope.launch {
            cUserBottomSheet.nInterface.nStartLoading()
            try {
                val r = adminRepository.registerExcelStudents(students)
                cUserBottomSheet.nInterface.nSuccess()
            } catch (_: Throwable) {
                with(cUserBottomSheet.nInterface) {
                    nError("Что-то пошло не так =/", onFixErrorClick = {
                        goToNone()
                    })
                }
            }
        }.invokeOnCompletion {
            fetchUsers()
        }
    }

    private fun deleteAccount() {
        scope.launch {
            eDeleteDialog.nInterface.nStartLoading()
            try {
                adminRepository.deleteUser(
                    login = state().eLogin, UserInit(
                        fio = FIO(
                            name = state().eName,
                            surname = state().eSurname,
                            praname = state().ePraname
                        ),
                        birthday = state().eBirthday,
                        role = state().eRole,
                        moderation = if (state().eIsModerator && state().eIsMentor) Moderation.both
                        else if (state().eIsMentor) Moderation.mentor
                        else if (state().eIsModerator) Moderation.moderator
                        else Moderation.nothing,
                        isParent = state().eIsParent
                    )
                )
                eDeleteDialog.fullySuccess()
                eUserBottomSheet.fullySuccess()
            } catch (_: Throwable) {
                with(eDeleteDialog.nInterface) {
                    nError("Что-то пошло не так =/", onFixErrorClick = {
                        goToNone()
                    })
                }
            }
        }.invokeOnCompletion {
            fetchUsers()
        }
    }

    private fun clearPassword(state: State) {
        scope.launch {
            eUserBottomSheet.nInterface.nStartLoading()
            try {
                adminRepository.clearUserPassword(state.eLogin)
                eUserBottomSheet.fullySuccess()
            } catch (_: Throwable) {
                with(eUserBottomSheet.nInterface) {
                    nError("Что-то пошло не так =/", onFixErrorClick = {
                        goToNone()
                    })
                }
            }
        }.invokeOnCompletion {
            fetchUsers()
        }

    }

    private fun editUser(state: State) {
        scope.launch {
            eUserBottomSheet.nInterface.nStartLoading()
            try {
                adminRepository.editUser(
                    login = state.eLogin, UserInit(
                        fio = FIO(
                            name = state.eName,
                            surname = state.eSurname,
                            praname = state.ePraname
                        ),
                        birthday = state.eBirthday,
                        role = state.eRole,
                        moderation = if (state.eIsModerator && state.eIsMentor) Moderation.both
                        else if (state.eIsMentor) Moderation.mentor
                        else if (state.eIsModerator) Moderation.moderator
                        else Moderation.nothing,
                        isParent = state.eIsParent
                    ),
                    subjectId = state.eSubjectId
                )
                eUserBottomSheet.fullySuccess()
            } catch (_: Throwable) {
                with(eUserBottomSheet.nInterface) {
                    nError("Что-то пошло не так =/", onFixErrorClick = {
                        goToNone()
                    })
                }
            }
        }.invokeOnCompletion {
            fetchUsers()
        }
    }

    private fun openEditingSheet(user: User) {
        dispatch(
            Message.InitEditingUser(
                name = user.user.fio.name,
                surname = user.user.fio.surname,
                praname = user.user.fio.praname,
                login = user.login,
                isPassword = user.isProtected,
                birthday = user.user.birthday,
                role = user.user.role,
                isMentor = user.user.moderation in listOf(
                    Moderation.mentor,
                    Moderation.both
                ),
                isModerator = user.user.moderation in listOf(
                    Moderation.moderator,
                    Moderation.both
                ),
                isParent = user.user.isParent,
                subjectId = user.subjectId
            )
        )
        eUserBottomSheet.onEvent(CBottomSheetStore.Intent.ShowSheet)
    }

    private fun createUser(state: State) {
        scope.launch {
            cUserBottomSheet.nInterface.nStartLoading()
            try {
                val user = UserInit(
                    fio = FIO(
                        name = state.cName,
                        surname = state.cSurname,
                        praname = state.cPraname
                    ),
                    birthday = state.cBirthday,
                    role = state.cRole,
                    moderation = if (state.cIsModerator && state.cIsMentor) Moderation.both
                    else if (state.cIsMentor) Moderation.mentor
                    else if (state.cIsModerator) Moderation.moderator
                    else Moderation.nothing,
                    isParent = state.cIsParent
                )
                val parents = listOf(
                    state().cParentFirstFIO,
                    state.cParentSecondFIO
                ).filter { it.isNotBlank() }
                val r = adminRepository.registerUser(
                    user,
                    parents = parents.ifEmpty { null },
                    formId = state.cFormId,
                    subjectId = state.cSubjectId
                    )
                dispatch(Message.UserCreated(r.login, r.parents))
            } catch (_: Throwable) {
                with(cUserBottomSheet.nInterface) {
                    nError("Что-то пошло не так =/", onFixErrorClick = {
                        goToNone()
                    })
                }
            }
        }.invokeOnCompletion {
            fetchUsers()
        }
    }

    private fun fetchUsers(isInit: Boolean = false) {
        scope.launch {
            nUsersInterface.nStartLoading()
            try {
                val r = adminRepository.fetchAllUsers()
                dispatch(Message.UsersChanged(r.users, r.forms, r.subjects))
                nUsersInterface.nSuccess()
            } catch (e: Throwable) {
                if (isInit) {

                    nUsersInterface.nError(
                        if (e.message!!.contains("403 Forbidden")) "Доступ запрещён" else "Что-то пошло не так",
                        onFixErrorClick = if (e.message!!.contains("403 Forbidden")) {
                            {}
                        } else {
                            { fetchUsers(true) }
                        }
                    )

                    dispatch(Message.UsersChanged(null, emptyList(), emptyMap()))
                }
            }
        }
    }

}
