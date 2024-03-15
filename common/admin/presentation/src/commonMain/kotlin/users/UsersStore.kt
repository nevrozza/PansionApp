package users

import admin.users.User
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import users.UsersStore.Intent
import users.UsersStore.Label
import users.UsersStore.State

interface UsersStore : Store<Intent, State, Label> {
    data class State(
        val users: List<User>? = null,
        val isDateDialogShowing: Boolean = false,

        val currentYear: Int = Clock.System.now()
            .toLocalDateTime(TimeZone.of("Europe/Moscow")).year,
        val currentMillis: Long = Clock.System.now().toEpochMilliseconds(),

        val cLogin: String = "",
        val cName: String = "",
        val cSurname: String = "",
        val cPraname: String? = "",
        val cBirthday: String = "",
        val cRole: String = "",
        val cIsModerator: Boolean = false,
        val cIsMentor: Boolean = false,
        val cIsParent: Boolean = false,

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

        )

    sealed interface Intent {
        data object FetchUsers : Intent
        data object FetchUsersInit : Intent
        data class ChangeDateDialogShowing(val isShowing: Boolean) : Intent

        data class ChangeCName(val name: String) : Intent
        data class ChangeCSurname(val surname: String) : Intent
        data class ChangeCPraname(val praname: String) : Intent
        data class ChangeCBirthday(val birthday: String) : Intent
        data class ChangeCRole(val role: String) : Intent
        data class ChangeCIsModerator(val isModerator: Boolean) : Intent
        data class ChangeCIsMentor(val isMentor: Boolean) : Intent
        data class ChangeCIsParent(val isParent: Boolean) : Intent

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

        data class OpenEditingSheet(val user: User) : Intent
        data object ClearPassword: Intent
        data object EditUser: Intent
    }

    sealed interface Message {
        data class UsersChanged(val users: List<User>?) : Message

        data class DateDialogShowingChanged(val isShowing: Boolean) : Message

        data class CNameChanged(val name: String) : Message
        data class CSurnameChanged(val surname: String) : Message
        data class CPranameChanged(val praname: String) : Message
        data class CBirthdayChanged(val birthday: String) : Message
        data class CRoleChanged(val role: String) : Message
        data class CIsModeratorChanged(val isModerator: Boolean) : Message
        data class CIsMentorChanged(val isMentor: Boolean) : Message
        data class CIsParentChanged(val isParent: Boolean) : Message

        data class UserCreated(val login: String) : Message
        data object ClearUser : Message

        data class ENameChanged(val name: String) : Message
        data class ESurnameChanged(val surname: String) : Message
        data class EPranameChanged(val praname: String) : Message
        data class EBirthdayChanged(val birthday: String) : Message
        data class ERoleChanged(val role: String) : Message
        data class EIsModeratorChanged(val isModerator: Boolean) : Message
        data class EIsMentorChanged(val isMentor: Boolean) : Message
        data class EIsParentChanged(val isParent: Boolean) : Message

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
            val isParent: Boolean
        ) : Message
    }

    sealed interface Label

}
