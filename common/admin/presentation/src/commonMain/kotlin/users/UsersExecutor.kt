package users

import AdminRepository
import admin.User
import admin.UserForRegistration
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.launch
import server.IsParentStatus
import server.Moderation
import users.UsersStore.Intent
import users.UsersStore.Label
import users.UsersStore.State
import users.UsersStore.Message

class UsersExecutor(private val adminRepository: AdminRepository) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent, getState: () -> State) {
        when (intent) {
            Intent.FetchUsersInit -> fetchUsers(true)
            Intent.FetchUsers -> fetchUsers()
            is Intent.ChangeCreatingSheetShowing -> dispatch(Message.CreatingSheetShowingChanged(intent.isShowing))
            is Intent.ChangeEditingSheetShowing -> dispatch(Message.EditingSheetShowingChanged(intent.isShowing))
            is Intent.ChangeDateDialogShowing -> dispatch(Message.DateDialogShowingChanged(intent.isShowing))

            is Intent.ChangeCName -> dispatch(Message.CNameChanged(intent.name))
            is Intent.ChangeCSurname -> dispatch(Message.CSurnameChanged(intent.surname))
            is Intent.ChangeCPraname -> dispatch(Message.CPranameChanged(intent.praname))
            is Intent.ChangeCBirthday ->dispatch(Message.CBirthdayChanged(intent.birthday))
            is Intent.ChangeCRole -> dispatch(Message.CRoleChanged(intent.role))
            is Intent.ChangeCIsModerator -> dispatch(Message.CIsModeratorChanged(intent.isModerator))
            is Intent.ChangeCIsMentor -> dispatch(Message.CIsMentorChanged(intent.isMentor))
            is Intent.ChangeCIsParent -> dispatch(Message.CIsParentChanged(intent.isParent))

            Intent.CreateUser -> createUser(getState())
            Intent.ClearUser -> dispatch(Message.ClearUser)
            Intent.TryCreateAgain -> dispatch(Message.TryCreateAgain)

            is Intent.ChangeEName -> dispatch(Message.ENameChanged(intent.name))
            is Intent.ChangeESurname -> dispatch(Message.ESurnameChanged(intent.surname))
            is Intent.ChangeEPraname -> dispatch(Message.EPranameChanged(intent.praname))
            is Intent.ChangeEBirthday ->dispatch(Message.EBirthdayChanged(intent.birthday))
            is Intent.ChangeERole -> dispatch(Message.ERoleChanged(intent.role))
            is Intent.ChangeEIsModerator -> dispatch(Message.EIsModeratorChanged(intent.isModerator))
            is Intent.ChangeEIsMentor -> dispatch(Message.EIsMentorChanged(intent.isMentor))
            is Intent.ChangeEIsParent -> dispatch(Message.EIsParentChanged(intent.isParent))

            is Intent.OpenEditingSheet -> openEditingSheet(intent.user)
            is Intent.ClearPassword -> clearPassword(getState())
            Intent.EditUser -> editUser(getState())
            Intent.TryEditUserAgain -> dispatch(Message.TryEditAgain)
        }
    }

    private fun clearPassword(state: State) {
        scope.launch {
            dispatch(Message.EditingProcessStarted)
            try {
                val isGood = adminRepository.clearUserPassword(state.eLogin)
                dispatch(Message.EditingSheetShowingChanged(false))
                dispatch(Message.TryEditAgain)
                fetchUsers()
            }
            catch (_: Throwable) {
                dispatch(Message.EditingError)
            }
        }
    }

    private fun editUser(state: State) {
        scope.launch {
            dispatch(Message.EditingProcessStarted)
            try {
                val isGood = adminRepository.editUser(login = state.eLogin, UserForRegistration(
                    name = state.eName,
                    surname = state.eSurname,
                    praname = state.ePraname,
                    birthday = state.eBirthday,
                    role = state.eRole,
                    moderation = if(state.eIsModerator && state.eIsMentor) Moderation.both
                    else if (state.eIsMentor) Moderation.mentor
                    else if (state.eIsModerator) Moderation.moderator
                    else Moderation.nothing,
                    isParent = state.eIsParent
                )).isGood
                dispatch(Message.EditingSheetShowingChanged(false))
                dispatch(Message.TryEditAgain)
                fetchUsers()
            }
            catch (_: Throwable) {
                dispatch(Message.EditingError)
            }
        }
    }

    private fun openEditingSheet(user: User) {
        dispatch(Message.InitEditingUser(
            name = user.name,
            surname = user.surname,
            praname = user.praname,
            login = user.login,
            isPassword = user.password != null,
            birthday = user.birthday ?: "",
            role = user.role,
            isMentor = user.moderation in listOf(Moderation.mentor, Moderation.both, Moderation.superBoth),
            isModerator = user.moderation in listOf(Moderation.moderator, Moderation.both, Moderation.superModerator, Moderation.superBoth),
            isParent = user.isParent
        ))
    }

    private fun createUser(state: State) {
        scope.launch {
            dispatch(Message.CreatingProcessStarted)
            try {
                val user = UserForRegistration(
                    name = state.cName,
                    surname = state.cSurname,
                    praname = state.cPraname,
                    birthday = state.cBirthday,
                    role = state.cRole,
                    moderation = if(state.cIsModerator && state.cIsMentor) Moderation.both
                    else if (state.cIsMentor) Moderation.mentor
                    else if (state.cIsModerator) Moderation.moderator
                    else Moderation.nothing,
                    isParent = state.cIsParent
                )
                val login = adminRepository.registerUser(user).login
                dispatch(Message.UserCreated(login))
                fetchUsers()
            }
            catch (_: Throwable) {
                dispatch(Message.CreationError)
            }
        }
    }

    private fun fetchUsers(isInit: Boolean = false) {
        scope.launch {
            dispatch(Message.ProcessStarted)
            try {
                val users = adminRepository.fetchAllUsers().users
                dispatch(Message.UsersChanged(users))
            }
            catch (e: Throwable) {
                if (isInit) {
                    if (e.message!!.contains("403 Forbidden")) {
                        dispatch(Message.AccessDenied)
                    }
                    dispatch(Message.UsersChanged(null))
                }
            }
        }
    }

}
