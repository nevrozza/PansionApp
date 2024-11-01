package ministry

import com.arkivanov.mvikotlin.core.store.Reducer
import ministry.MinistryStore.Message
import ministry.MinistryStore.State

object MinistryReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.MinistryHeaderInited -> copy(isMultiMinistry = msg.isMultiMinistry, pickedMinistry = msg.pickedMinistry)
            is Message.MinistryChanged -> copy(pickedMinistry = msg.ministryId)
            is Message.DateChanged -> copy(currentDate = msg.date)
            is Message.ListUpdated -> copy(ministryList = msg.list)
            is Message.MVDEditOpened -> copy(mvdLogin = msg.login, mvdReportId = msg.reportId)
            is Message.MVDDS3Opened -> copy(mvdCustom = msg.custom, mvdStups = msg.stups)
            is Message.Ds3StepperChanged -> copy(mvdStups = msg.stups)
            is Message.Ds3CustomChanged -> copy(mvdCustom = msg.custom)
        }
    }
}