package users

import com.arkivanov.mvikotlin.core.store.Reducer
import users.UsersStore.State
import users.UsersStore.Message

object UsersReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.UsersChanged -> copy(users = msg.users)


            is Message.DateDialogShowingChanged -> copy(isDateDialogShowing = msg.isShowing)

            is Message.CNameChanged -> copy(cName = msg.name)
            is Message.CSurnameChanged -> copy(cSurname = msg.surname)
            is Message.CPranameChanged -> copy(cPraname = msg.praname)
            is Message.CBirthdayChanged -> copy(cBirthday = msg.birthday)
            is Message.CRoleChanged -> copy(cRole = msg.role)
            is Message.CIsModeratorChanged -> copy(cIsModerator = msg.isModerator)
            is Message.CIsMentorChanged -> copy(cIsMentor = msg.isMentor)
            is Message.CIsParentChanged -> copy(cIsParent = msg.isParent)
            is Message.ClearUser -> copy(cLogin = "", cIsParent = false, cBirthday = "", cIsMentor = false, cIsModerator = false, cName = "", cPraname = "", cRole = "", cSurname = "")

            is Message.UserCreated -> copy(cLogin = msg.login)

            is Message.ENameChanged -> copy(eName = msg.name)
            is Message.ESurnameChanged -> copy(eSurname = msg.surname)
            is Message.EPranameChanged -> copy(ePraname = msg.praname)
            is Message.EBirthdayChanged -> copy(eBirthday = msg.birthday)
            is Message.ERoleChanged -> copy(eRole = msg.role)
            is Message.EIsModeratorChanged -> copy(eIsModerator = msg.isModerator)
            is Message.EIsMentorChanged -> copy(eIsMentor = msg.isMentor)
            is Message.EIsParentChanged -> copy(eIsParent = msg.isParent)
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
                eIsParent = msg.isParent
            )
        }
    }
}