package users

import admin.groups.forms.CutedForm
import admin.users.ToBeCreatedStudent
import admin.users.User
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import users.UsersStore.Intent
import users.UsersStore.Label
import users.UsersStore.State

interface UsersStore : Store<Intent, State, Label>, InstanceKeeper.Instance {
    @Serializable
    data class State(
        val users: List<User>? = null,
        val forms: List<CutedForm> = emptyList(),
        val subjects: Map<Int, String> = emptyMap(),
        val isDateDialogShowing: Boolean = false,
        val currentYear: Int = Clock.System.now()
            .toLocalDateTime(TimeZone.of("UTC+3")).year,
        val currentMillis: Long = Clock.System.now().toEpochMilliseconds(),
        val cLogin: String = "",
        val cParentLogins: List<String>? = null,
        val cName: String = "",
        val cSurname: String = "",
        val cPraname: String? = "",
        val cBirthday: String = "",
        val cRole: String = "",
        val cFormId: Int = 0,
        val cIsModerator: Boolean = false,
        val cIsMentor: Boolean = false,
        val cIsParent: Boolean = false,
        val cParentFirstFIO: String = "",
        val cParentSecondFIO: String = "",
        val cSubjectId: Int? = null,


        val eLogin: String = "",
        val eIsPassword: Boolean = false,
        val eName: String = "",
        val eSurname: String = "",
        val ePraname: String? = "",
        val eBirthday: String = "",
        val eRole: String = "",
        val eIsModerator: Boolean = false,
        val eIsMentor: Boolean = false,
        val eIsParent: Boolean = false,
        val eSubjectId: Int? = null,

        val eDeletingLogin: String? = null,


        val fTeachers: Boolean = true,
        val fStudents: Boolean = true,
        val fOther: Boolean = true,
        val fParents: Boolean = true,
        val fNoAdmin: Boolean = true,
        val fInActive: Boolean = true,

        val userFindField: String = ""
        )

    sealed interface Intent {
        data class UpdateUserFind(val data: String) : Intent


        data class FTeachers(val isOn: Boolean) : Intent
        data class FStudents(val isOn: Boolean) : Intent
        data class FOther(val isOn: Boolean) : Intent
        data class FParents(val isOn: Boolean) : Intent
        data class FInActive(val isOn: Boolean) : Intent
        data class FNoAdmin(val isOn: Boolean) : Intent




        data class DeleteAccountInit(val login: String?) : Intent

        data object DeleteAccount : Intent


        data object FetchUsers : Intent
        data object FetchUsersInit : Intent
        data class ChangeDateDialogShowing(val isShowing: Boolean) : Intent

        data class ChangeCName(val name: String) : Intent
        data class ChangeCSurname(val surname: String) : Intent
        data class ChangeCPraname(val praname: String) : Intent
        data class ChangeCBirthday(val birthday: String) : Intent
        data class ChangeCRole(val role: String) : Intent
        data class ChangeCFormId(val formId: Int) : Intent
        data class ChangeCSubjectId(val subjectId: Int) : Intent
        data class ChangeCIsModerator(val isModerator: Boolean) : Intent
        data class ChangeCIsMentor(val isMentor: Boolean) : Intent
        data class ChangeCIsParent(val isParent: Boolean) : Intent
        data class ChangeCParentFirstFIO(val fio: String) : Intent
        data class ChangeCParentSecondFIO(val fio: String) : Intent

        data object CreateUser : Intent
        data object ClearUser : Intent

        data class ChangeEName(val name: String) : Intent
        data class ChangeESurname(val surname: String) : Intent
        data class ChangeEPraname(val praname: String) : Intent
        data class ChangeEBirthday(val birthday: String) : Intent
        data class ChangeERole(val role: String) : Intent
        data class ChangeEIsModerator(val isModerator: Boolean) : Intent
        data class ChangeEIsMentor(val isMentor: Boolean) : Intent
        data class ChangeEIsParent(val isParent: Boolean) : Intent
        data class ChangeESubjectId(val subjectId: Int) : Intent

        data class OpenEditingSheet(val user: User) : Intent
        data object ClearPassword: Intent
        data object EditUser: Intent

        data class CreateUsers(val users: List<ToBeCreatedStudent>) : Intent
    }

    sealed interface Message {

        data class UserFindUpdate(val data: String) : Message

        data class FTeachers(val isOn: Boolean) : Message
        data class FStudents(val isOn: Boolean) : Message
        data class FOther(val isOn: Boolean) : Message
        data class FParents(val isOn: Boolean) : Message
        data class FInactive(val isOn: Boolean) : Message
        data class FNoAdmin(val isOn: Boolean) : Message


        data class DeletingAccountInit(val login: String?) : Message

        data class UsersChanged(val users: List<User>?, val forms: List<CutedForm>, val subjects: Map<Int, String>) : Message

        data class DateDialogShowingChanged(val isShowing: Boolean) : Message

        data class CNameChanged(val name: String) : Message
        data class CSurnameChanged(val surname: String) : Message
        data class CPranameChanged(val praname: String) : Message
        data class CBirthdayChanged(val birthday: String) : Message
        data class CRoleChanged(val role: String) : Message
        data class CFormIdChanged(val formId: Int) : Message
        data class CSubjectIdChanged(val subjectId: Int) : Message
        data class CIsModeratorChanged(val isModerator: Boolean) : Message
        data class CIsMentorChanged(val isMentor: Boolean) : Message
        data class CIsParentChanged(val isParent: Boolean) : Message
        data class CParentFirstFIOChanged(val fio: String) : Message
        data class CParentSecondFIOChanged(val fio: String) : Message

        data class UserCreated(val login: String, val parents: List<String>?) : Message
        data object ClearUser : Message

        data class ENameChanged(val name: String) : Message
        data class ESurnameChanged(val surname: String) : Message
        data class EPranameChanged(val praname: String) : Message
        data class EBirthdayChanged(val birthday: String) : Message
        data class ERoleChanged(val role: String) : Message
        data class EIsModeratorChanged(val isModerator: Boolean) : Message
        data class EIsMentorChanged(val isMentor: Boolean) : Message
        data class EIsParentChanged(val isParent: Boolean) : Message
        data class ESubjectIdChange(val subjectId: Int) : Message

        data class InitEditingUser(
            val name: String,
            val surname: String,
            val praname: String?,
            val login: String,
            val isPassword: Boolean,
            val birthday: String,
            val role: String,
            val isMentor: Boolean,
            val isModerator: Boolean,
            val isParent: Boolean,
            val subjectId: Int?
        ) : Message
    }

    sealed interface Label

}
