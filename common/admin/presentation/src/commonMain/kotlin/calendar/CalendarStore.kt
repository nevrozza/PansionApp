package calendar

import admin.calendar.CalendarModuleItem
import com.arkivanov.mvikotlin.core.store.Store
import calendar.CalendarStore.Intent
import calendar.CalendarStore.Label
import calendar.CalendarStore.State

interface CalendarStore : Store<Intent, State, Label> {
    data class State(
        val modules: List<CalendarModuleItem> = emptyList(),
        val isCalendarShowing: Boolean = false,
        val selectedModuleNum: Int? = null,
        val creatingHalfNum: Int? = null,
        val isSavedAnimation: Boolean = false

//        val halfs: List<CalendarHalfItem> = emptyList(),
//        val weekends: List<String> = emptyList()
    )

    sealed interface Intent {
        data object Init: Intent

        data object SendItToServer: Intent

        data class OpenCalendar(val creatingHalfNum: Int, val selectedModuleNum: Int) : Intent

        data object CloseCalendar: Intent

        data class CreateModule(val date: String) : Intent

        data object DeleteModule : Intent

        data class IsSavedAnimation(val isSaved: Boolean) : Intent
    }

    sealed interface Message {

        data class ModulesUpdated(val modules: List<CalendarModuleItem>) : Message

        data class CalendarOpened(val creatingHalfNum: Int, val selectedModuleNum: Int) : Message

        data object CalendarClosed : Message

        data class IsAnimationSaved(val isSaved: Boolean) : Message
    }

    sealed interface Label

}

