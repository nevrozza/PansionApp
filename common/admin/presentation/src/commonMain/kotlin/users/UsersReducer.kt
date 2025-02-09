package users

import com.arkivanov.mvikotlin.core.store.Reducer
import server.Roles
import users.UsersStore.Message
import users.UsersStore.State

object UsersReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.UsersChanged -> copy(users = msg.users, forms = msg.forms, subjects = msg.subjects)


            is Message.DateDialogShowingChanged -> copy(isDateDialogShowing = msg.isShowing)

            is Message.CNameChanged -> copy(cName = msg.name)
            is Message.CSurnameChanged -> copy(cSurname = msg.surname)
            is Message.CPranameChanged -> copy(cPraname = msg.praname)
            is Message.CBirthdayChanged -> copy(cBirthday = msg.birthday)
            is Message.CRoleChanged -> copy(
                cRole = msg.role,
                cIsModerator = if (msg.role == Roles.STUDENT) false else cIsModerator,
                cIsMentor = if (msg.role == Roles.STUDENT) false else cIsMentor,
                cIsParent = if (msg.role == Roles.STUDENT) false else cIsParent
            )
            is Message.CIsModeratorChanged -> copy(cIsModerator = msg.isModerator)
            is Message.CIsMentorChanged -> copy(cIsMentor = msg.isMentor)
            is Message.CIsParentChanged -> copy(cIsParent = msg.isParent)
            is Message.ClearUser -> copy(
                cLogin = "",
                cParentLogins = null,
                cIsParent = false,
                cBirthday = "",
                cIsMentor = false,
                cIsModerator = false,
                cName = "",
                cPraname = "",
                cRole = "",
                cSurname = "",
                cParentFirstFIO = "",
                cParentSecondFIO = "",
                cFormId = 0,
                cSubjectId = null
            )

            is Message.UserCreated -> copy(cLogin = msg.login, cParentLogins = msg.parents)

            is Message.ENameChanged -> copy(eName = msg.name)
            is Message.ESurnameChanged -> copy(eSurname = msg.surname)
            is Message.EPranameChanged -> copy(ePraname = msg.praname)
            is Message.EBirthdayChanged -> copy(eBirthday = msg.birthday)
            is Message.ERoleChanged -> copy(
                eRole = msg.role,
                eIsModerator = if (msg.role == Roles.STUDENT) false else eIsModerator,
                eIsMentor = if (msg.role == Roles.STUDENT) false else eIsMentor,
                eIsParent = if (msg.role == Roles.STUDENT) false else eIsParent
            )
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
                eIsParent = msg.isParent,
                eSubjectId = msg.subjectId
            )

            is Message.CParentFirstFIOChanged -> copy(cParentFirstFIO = msg.fio)
            is Message.CParentSecondFIOChanged -> copy(cParentSecondFIO = msg.fio)
            is Message.DeletingAccountInit -> copy(eDeletingLogin = msg.login)
            is Message.CFormIdChanged -> copy(cFormId = msg.formId)
            is Message.FNoAdmin -> copy(fNoAdmin = msg.isOn)
            is Message.FOther -> copy(fOther = msg.isOn)
            is Message.FStudents -> copy(fStudents = msg.isOn)
            is Message.FTeachers -> copy(fTeachers = msg.isOn)
            is Message.FInactive -> copy(fInActive = msg.isOn)
            is Message.FParents -> copy(fParents = msg.isOn, fOther = if (msg.isOn) true else fOther)
            is Message.CSubjectIdChanged -> copy(
                cSubjectId = msg.subjectId
            )

            is Message.UserFindUpdate -> copy(
                userFindField = msg.data
            )

            is Message.ESubjectIdChange -> copy(
                eSubjectId = msg.subjectId
            )
        }
    }
}