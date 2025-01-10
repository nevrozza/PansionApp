package calendar

import admin.calendar.CalendarModuleItem
import admin.calendar.Holiday
import com.arkivanov.mvikotlin.core.store.Store
import calendar.CalendarStore.Intent
import calendar.CalendarStore.Label
import calendar.CalendarStore.State
import server.getCurrentDate
import server.getCurrentEdYear

interface CalendarStore : Store<Intent, State, Label> {
    data class State(
        val modules: List<CalendarModuleItem> = emptyList(),
        val isCalendarShowing: Boolean = false,
        val selectedModuleNum: Int? = null,
        val creatingHalfNum: Int? = null,
        val isSavedAnimation: Boolean = false,
        val edYear: Int = getCurrentEdYear(),
        val today: String = getCurrentDate().second,

        val holidays: List<Holiday> = emptyList(),
        val selectedHolidayId: Int? = null

//        val halfs: List<CalendarHalfItem> = emptyList(),
//        val weekends: List<String> = emptyList()
    )

    sealed interface Intent {
        data object Init: Intent

        data object SendItToServer: Intent

        data class OpenCalendar(val creatingHalfNum: Int, val selectedModuleNum: Int) : Intent
        data class OpenRangePicker(val selectedHolidayId: Int) : Intent

        data object CloseCalendar: Intent

        data class CreateModule(val date: String) : Intent
        data class CreateHoliday(val start: String, val end: String, val isForAll: Boolean) : Intent

        data object DeleteModule : Intent
        data class DeleteHoliday(val id: Int) : Intent

        data class IsSavedAnimation(val isSaved: Boolean) : Intent
    }

    sealed interface Message {

        data class ModulesUpdated(val modules: List<CalendarModuleItem>) : Message
        data class HolidaysUpdated(val holidays: List<Holiday>) : Message

        data class CalendarOpened(val creatingHalfNum: Int, val selectedModuleNum: Int) : Message
        data class DateRangePickerOpened(val selectedHolidayId: Int) : Message

        data object CalendarClosed : Message

        data class IsAnimationSaved(val isSaved: Boolean) : Message
    }

    sealed interface Label

}

