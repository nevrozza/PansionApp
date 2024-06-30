package calendar

import com.arkivanov.mvikotlin.core.store.Reducer
import calendar.CalendarStore.State
import calendar.CalendarStore.Message

object CalendarReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            Message.CalendarClosed -> copy(isCalendarShowing = false)
            is Message.CalendarOpened -> copy(isCalendarShowing = true, selectedModuleNum = msg.selectedModuleNum, creatingHalfNum = msg.creatingHalfNum)
            is Message.ModulesUpdated -> copy(modules = msg.modules.sortedBy { it.num })
            is Message.IsAnimationSaved -> copy(isSavedAnimation = msg.isSaved)
        }
    }
}