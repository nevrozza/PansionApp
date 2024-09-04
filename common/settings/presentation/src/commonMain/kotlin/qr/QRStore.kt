package qr

import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import qr.QRStore.Intent
import qr.QRStore.Label
import qr.QRStore.State

interface QRStore : Store<Intent, State, Label> {
    data class State(
        val code: String = "",
        val isRegistration: Boolean,
        val deviceName: String = "",
        val deviceType: String = "",
        val formName: String = "",


        val isDateDialogShowing: Boolean = false,
        val currentYear: Int = Clock.System.now()
            .toLocalDateTime(TimeZone.of("UTC+3")).year,
        val currentMillis: Long = Clock.System.now().toEpochMilliseconds(),
        val cLogin: String = "",
        val cName: String = "",
        val cSurname: String = "",
        val cPraname: String? = "",
        val cBirthday: String = "",
        val cParentFirstFIO: String = "",
        val cParentSecondFIO: String = "",
        val cAvatarId: Int = 0
    )

    sealed interface Intent {
        data class ChangeCode(val code: String) : Intent
        data object SendToServer: Intent
        data object SendToServerAtAll: Intent
        data object GoToNone: Intent

        data class ChangeDateDialogShowing(val isShowing: Boolean) : Intent

        data class ChangeCName(val name: String) : Intent
        data class ChangeCSurname(val surname: String) : Intent
        data class ChangeCPraname(val praname: String) : Intent
        data class ChangeCBirthday(val birthday: String) : Intent
        data class ChangeCParentFirstFIO(val fio: String) : Intent
        data class ChangeCParentSecondFIO(val fio: String) : Intent

    }

    sealed interface Message {
        data class CodeChanged(val code: String) : Message
        data class AuthReceived(val deviceName: String, val deviceType: String) : Message
        data class FormReceived(val formName: String) : Message

        data class DateDialogShowingChanged(val isShowing: Boolean) : Message

        data class CNameChanged(val name: String) : Message
        data class CSurnameChanged(val surname: String) : Message
        data class CPranameChanged(val praname: String) : Message
        data class CBirthdayChanged(val birthday: String) : Message
        data class CParentFirstFIOChanged(val fio: String) : Message
        data class CParentSecondFIOChanged(val fio: String) : Message
        data class LoginChanged(val login: String) : Message
    }

    sealed interface Label

}
