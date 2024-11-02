package school

import com.arkivanov.mvikotlin.core.store.Reducer
import school.SchoolStore.Message
import school.SchoolStore.State

object SchoolReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.Inited -> copy(
                formId = msg.formId,
                formName = msg.formName,
                top = msg.top,
                formNum = msg.formNum,
                ministryId = msg.ministryId,
                mvdStupsCount = msg.mvdStupsCount,
                zdStupsCount = msg.zdStupsCount
            )

            is Message.MinistrySettingsOpened -> copy(
                ministryStudents = msg.ministryStudents
            )

            is Message.DutyFetched -> copy(
                dutyKids = msg.dutyKids,
                dutyPeopleCount = msg.dutyPeopleCount
            )
        }
    }
}