package homeTasks

import JournalRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.networkInterface.NetworkInterface
import deviceSupport.launchIO
import deviceSupport.withMain
import homeTasks.HomeTasksStore.Intent
import homeTasks.HomeTasksStore.Label
import homeTasks.HomeTasksStore.Message
import homeTasks.HomeTasksStore.State
import homework.RCheckHomeTaskReceive
import homework.RFetchHomeTasksReceive
import homework.RFetchTasksInitReceive

class HomeTasksExecutor(
    private val journalRepository: JournalRepository,
    private val nInitInterface: NetworkInterface,
    private val nInterface: NetworkInterface,
    val updateHTCount: (Int) -> Unit
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            is Intent.CheckTask -> checkTask(taskId = intent.taskId, isCheck = intent.isCheck, doneId = intent.doneId)
            Intent.Init -> init()
            is Intent.OpenDateItem -> {
                dispatch(Message.LoadingDateChanged(intent.date))
                fetchTasks(date = intent.date)
            }
        }
    }

    private fun init() {
        scope.launchIO {
            nInitInterface.nStartLoading()
            fetchInitFirst()
            fetchTasks(null)
        }
    }

    private suspend fun fetchInitFirst() {
        try {
            val r = journalRepository.fetchHomeTasksInit(RFetchTasksInitReceive(state().login))
            withMain {
                dispatch(Message.DatesGroupsSubjectsInited(
                    dates = r.dates.toSet().toList(),
                    groups = r.groups,
                    subjects = r.subjects
                ))
                if(state().homeTasks.isNotEmpty()) {
                    nInitInterface.nSuccess()
                }
            }
        } catch (e: Throwable) {
            print("ht: ${e}")
            nInitInterface.nError(text = "Не удалось загрузить данные\nоб уроках",e) {
                init()
            }
        }

    }

    private fun fetchTasks(date: String?) {
        scope.launchIO {
            try {
                nInterface.nStartLoading()
                val r = journalRepository.fetchHomeTasks(
                    RFetchHomeTasksReceive(
                        login = state().login,
                        date = date
                    )
                )
                val tasks = if(date == null) {
                    r.tasks
                } else {
                    state().homeTasks.mapNotNull {
                        if(it.date == date) null
                        else it
                    } + r.tasks
                }
                withMain {
                    dispatch(Message.TasksUpdated(tasks))
                    if(state().groups.isNotEmpty() || state().dates.isNotEmpty()) {
                        nInitInterface.nSuccess()
                    }
                    nInterface.nSuccess()
                    dispatch(Message.LoadingDateChanged(null))
                }
            } catch (e: Throwable) {
                if (date == null) {
                    nInitInterface.nError(text = "Не удалось загрузить задания",e) {
                        init()
                    }
                } else {
                    nInterface.nError(text = "Не удалось загрузить задания этого дня",e) {
                        fetchTasks(date)
                    }
                }
            }
        }
    }

    private fun checkTask(taskId: Int, isCheck: Boolean, doneId: Int?) {
        scope.launchIO {
            val newTasks = state().homeTasks.map { if (it.id == taskId) it.copy(done = isCheck) else it }
            withMain {
                dispatch(Message.TasksUpdated(newTasks))
            }
            try {
                journalRepository.checkHomeTask(
                    RCheckHomeTaskReceive(
                        login = state().login,
                        homeWorkId = taskId,
                        isCheck = isCheck,
                        id = doneId
                    )
                )
                withMain {
                    updateHTCount(newTasks.filter { it.isNec }.count { !it.done })
                }
            } catch (e: Throwable) {
                println(e)
            }
        }
    }
}
