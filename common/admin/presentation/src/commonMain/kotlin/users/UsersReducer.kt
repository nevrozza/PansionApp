package users

import com.arkivanov.mvikotlin.core.store.Reducer
import users.UsersStore.State
import users.UsersStore.Message

object UsersReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.UsersChanged -> copy(users = msg.users, isInProcess = false)
            is Message.ProcessStarted -> copy(isInProcess = true)
            is Message.CreatingSheetShowingChanged -> copy(isCreatingSheetShowing = msg.isShowing)
            is Message.EditingSheetShowingChanged -> copy(isEditingSheetShowing = msg.isShowing)
            is Message.DateDialogShowingChanged -> copy(isDateDialogShowing = msg.isShowing)
            is Message.AccessDenied -> copy(isAccessDenied = true)

            is Message.CNameChanged -> copy(cName = msg.name)
            is Message.CSurnameChanged -> copy(cSurname = msg.surname)
            is Message.CPranameChanged -> copy(cPraname = msg.praname)
            is Message.CBirthdayChanged -> copy(cBirthday = msg.birthday)
            is Message.CRoleChanged -> copy(cRole = msg.role)
            is Message.CIsModeratorChanged -> copy(cIsModerator = msg.isModerator)
            is Message.CIsMentorChanged -> copy(cIsMentor = msg.isMentor)
            is Message.CIsParentChanged -> copy(cIsParent = msg.isParent)
            is Message.ClearUser -> copy(isCreatingInProcess = false, cError = "", cLogin = "", cIsParent = false, cBirthday = "", cIsMentor = false, cIsModerator = false, cName = "", cPraname = "", cRole = "", cSurname = "")
            is Message.CreatingProcessStarted -> copy(isCreatingInProcess = true, cError = "")
            is Message.UserCreated -> copy(cLogin = msg.login)
            is Message.CreationError -> copy(cError = "Что-то пошло не так =/")
            is Message.TryCreateAgain -> copy(cError = "", isCreatingInProcess = false)

            is Message.ENameChanged -> copy(eName = msg.name)
            is Message.ESurnameChanged -> copy(eSurname = msg.surname)
            is Message.EPranameChanged -> copy(ePraname = msg.praname)
            is Message.EBirthdayChanged -> copy(eBirthday = msg.birthday)
            is Message.ERoleChanged -> copy(eRole = msg.role)
            is Message.EIsModeratorChanged -> copy(eIsModerator = msg.isModerator)
            is Message.EIsMentorChanged -> copy(eIsMentor = msg.isMentor)
            is Message.EIsParentChanged -> copy(eIsParent = msg.isParent)
            is Message.EditingError -> copy(eError = "Что-то пошло не так =/")
            is Message.InitEditingUser -> copy(
                eLogin = msg.login,
                eIsPassword = msg.isPassword,
                eName = msg.name,
                eSurname = msg.surname,
                ePraname = msg.praname,
                eBirthday = msg.birthday,
                eRole = msg.role,
                eIsModerator = msg.isModerator,
                eIsMentor = msg.isMentor,
                eIsParent = msg.isParent,
                isEditingSheetShowing = true
            )

           is Message.TryEditAgain -> copy(eError = "", isEditingInProcess = false)
           is Message.EditingProcessStarted -> copy(isEditingInProcess = true)
        }
    }
}