package homeTasks

import CDispatcher
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import homeTasks.HomeTasksStore.Intent
import homeTasks.HomeTasksStore.Label
import homeTasks.HomeTasksStore.State
import homeTasks.HomeTasksStore.Message
import kotlinx.coroutines.launch

class HomeTasksExecutor : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            is Intent.CheckTask -> checkTask(taskId = intent.taskId, isCheck = intent.isCheck)
        }
    }
    private fun checkTask(taskId: Int, isCheck: Boolean) {
        scope.launch(CDispatcher) {
//            val newTasks = state().homeTasks.toMutableList()
//            val task = newTasks.first { it.id == taskId }
            val newTasks = state().homeTasks.map { if (it.id == taskId) it.copy(done = isCheck) else it }
            scope.launch {
                dispatch(Message.TasksUpdated(newTasks))
            }
        }
    }
}
