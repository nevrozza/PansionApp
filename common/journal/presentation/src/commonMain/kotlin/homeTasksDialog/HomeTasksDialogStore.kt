package homeTasksDialog

import com.arkivanov.mvikotlin.core.store.Store
import homeTasks.HomeTasksStore
import homeTasksDialog.HomeTasksDialogStore.Intent
import homeTasksDialog.HomeTasksDialogStore.Label
import homeTasksDialog.HomeTasksDialogStore.State
import homework.ClientReportHomeworkItem
import homework.CreateReportHomeworkItem

interface HomeTasksDialogStore : Store<Intent, State, Label> {
    data class State(
        val groupId: Int,
        val homeTasks: List<ClientReportHomeworkItem> = emptyList(),
    )

    sealed interface Intent {
        data object Init: Intent
    }

    sealed interface Message {
        data class HomeTasksUpdated(val homeTasks: List<ClientReportHomeworkItem>) : HomeTasksDialogStore.Message
    }

    sealed interface Label

}
