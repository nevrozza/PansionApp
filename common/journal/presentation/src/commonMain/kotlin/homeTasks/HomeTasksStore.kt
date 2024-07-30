package homeTasks

import com.arkivanov.mvikotlin.core.store.Store
import homeTasks.HomeTasksStore.Intent
import homeTasks.HomeTasksStore.Label
import homeTasks.HomeTasksStore.State
import homework.ClientHomeworkItem
import homework.CutedDateTimeGroup

interface HomeTasksStore : Store<Intent, State, Label> {
    data class State(
        val login: String,
        val name: String,
        val avatarId: Int,
        val loadingDate: String? = null,
        val dates: List<String> = emptyList(),
        val groups: List<CutedDateTimeGroup> = emptyList(),
        val subjects: Map<Int, String> = emptyMap(),
        val homeTasks: List<ClientHomeworkItem> = emptyList()
    )

    sealed interface Intent {
//        data class
        data class OpenDateItem(val date: String) : Intent
        data class CheckTask(val taskId: Int, val isCheck: Boolean, val doneId: Int?) : Intent
        data object Init : Intent
    }

    sealed interface Message {
        data class LoadingDateChanged(val date: String?) : Message
        data class DatesGroupsSubjectsInited(val dates: List<String>, val groups: List<CutedDateTimeGroup>, val subjects: Map<Int, String>) : Message
        data class TasksUpdated(val tasks: List<ClientHomeworkItem>) : Message
    }

    sealed interface Label

}
