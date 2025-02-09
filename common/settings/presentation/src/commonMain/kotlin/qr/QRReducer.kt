package qr

import applicationTimeZone
import com.arkivanov.mvikotlin.core.store.Reducer
import kotlinx.datetime.Clock
import kotlinx.datetime.toLocalDateTime
import qr.QRStore.Message
import qr.QRStore.State

object QRReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.CodeChanged -> copy(code = msg.code)
            is Message.AuthReceived -> copy(
                deviceType = msg.deviceType,
                deviceName = msg.deviceName
            )

            is Message.FormReceived -> copy(
                formName = msg.formName,
                cName = "",
                cPraname = null,
                cSurname = "",
                cBirthday = "",
                cLogin = "",
                currentYear = Clock.System.now()
                    .toLocalDateTime(applicationTimeZone).year,
                currentMillis = Clock.System.now().toEpochMilliseconds(),
                isDateDialogShowing = false,
                cParentSecondFIO = "",
                cParentFirstFIO = ""
            )

            is Message.DateDialogShowingChanged -> copy(isDateDialogShowing = msg.isShowing)

            is Message.CNameChanged -> copy(cName = msg.name)
            is Message.CSurnameChanged -> copy(cSurname = msg.surname)
            is Message.CPranameChanged -> copy(cPraname = msg.praname)
            is Message.CBirthdayChanged -> copy(cBirthday = msg.birthday)
            is Message.CParentFirstFIOChanged -> copy(cParentFirstFIO = msg.fio)
            is Message.CParentSecondFIOChanged -> copy(cParentSecondFIO = msg.fio)
            is Message.LoginChanged -> copy(cLogin = msg.login)
        }
    }
}