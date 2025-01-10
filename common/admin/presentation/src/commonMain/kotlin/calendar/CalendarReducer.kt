package calendar

import com.arkivanov.mvikotlin.core.store.Reducer
import calendar.CalendarStore.State
import calendar.CalendarStore.Message

object CalendarReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            Message.CalendarClosed -> copy(
                isCalendarShowing = false,
//                selectedHolidayId = null,
//                selectedModuleNum = null,
//                creatingHalfNum = null
            )

            is Message.CalendarOpened -> copy(
                isCalendarShowing = true,
                selectedModuleNum = msg.selectedModuleNum,
                creatingHalfNum = msg.creatingHalfNum,
                selectedHolidayId = null
            )

            is Message.DateRangePickerOpened -> copy(
                isCalendarShowing = true,
                selectedModuleNum = null,
                creatingHalfNum = null,
                selectedHolidayId = msg.selectedHolidayId
            )
            is Message.HolidaysUpdated -> copy(holidays = msg.holidays.sortedBy { it.id })

            is Message.ModulesUpdated -> copy(modules = msg.modules.sortedBy { it.num })
            is Message.IsAnimationSaved -> copy(isSavedAnimation = msg.isSaved)
        }
    }
}