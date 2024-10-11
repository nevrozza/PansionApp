package formRating

import com.arkivanov.mvikotlin.core.store.Reducer
import formRating.FormRatingStore.State
import formRating.FormRatingStore.Message

object FormRatingReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.AvailableFormsUpdated -> copy(
                availableForms = msg.availableForms
            )

            is Message.FormChanged -> copy(
                formId = msg.formId,
                formName = msg.formName,
                formNum = msg.formNum
            )

            is Message.PeriodChanged -> copy(period = msg.period)
            is Message.FormRatingPagesUpdated -> copy(
                formRatingPages = msg.pages,
                subjects = msg.subjects
            )

            is Message.StupsLoginSelected -> copy(
                stupsLogin = msg.login
            )
        }
    }
}