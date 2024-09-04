package mentoring

import com.arkivanov.mvikotlin.core.store.Reducer
import mentoring.MentoringStore.State
import mentoring.MentoringStore.Message

object MentoringReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.StudentsFetched -> copy(forms = msg.forms, students = msg.students, requests = msg.requests)
            is Message.StudentSelected -> copy(chosenLogin = msg.login)
            is Message.PreAttendanceUpdate -> copy(preAttendance = msg.preAttendance, schedule = msg.schedule,
                cStart = null, cEnd = null, cIsGood = null, cReason = null) //MinusEdit
            is Message.PreAttendanceLoginChanged -> copy(chosenAttendanceLogin = msg.login)
            is Message.DateChanged -> copy(currentDate = msg.date,
                cStart = null, cEnd = null, cIsGood = null, cReason = null) //MinusEdit
            is Message.EditPreAttendanceStarted -> copy(cStart = msg.start, cEnd = msg.end, cIsGood = msg.cIsGood, cReason = msg.reason)
            is Message.CEndChanged -> copy(cEnd = msg.end)
            is Message.CIsGoodChanged -> copy(cIsGood = msg.isGood)
            is Message.CReasonChanged -> copy(cReason = msg.reason)
            is Message.CStartChanged -> copy(cStart = msg.start)
            is Message.FormsUpdated -> copy(forms = msg.forms)
        }
    }
}