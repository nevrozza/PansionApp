package rating

import com.arkivanov.mvikotlin.core.store.Reducer
import rating.RatingStore.State
import rating.RatingStore.Message

object RatingReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.OnSubjectClicked -> copy(currentSubject = msg.subjectId)
            is Message.RatingUpdated -> copy(me = msg.me, items = msg.items, lastEditTime = msg.lastEditTime)
            is Message.SubjectsUpdated -> copy(subjects = msg.subjects, period = period ?: msg.currentPeriod)
            is Message.OnFormClicked -> copy(forms = msg.formNum)
            is Message.OnPeriodClicked -> copy(period = msg.period)
            is Message.IsDetailedChanged -> copy(isDetailed = !isDetailed)
        }
    }
}