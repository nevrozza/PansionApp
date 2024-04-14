package home

import AuthRepository
import MainRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import components.networkInterface.NetworkInterface
import di.Inject
import home.HomeStore.Intent
import home.HomeStore.Label
import home.HomeStore.State
import home.HomeStore.Message
import journal.JournalStore
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomeExecutor(
    private val authRepository: AuthRepository,
    private val mainRepository: MainRepository,
    private val quickTabNInterface: NetworkInterface,
    private val teacherNInterface: NetworkInterface,
    private val gradesNInterface: NetworkInterface,
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            is Intent.Init -> {
//                val a: AuthRepository = Inject.instance()
//                dispatch(Message.Inited(
//                    avatarId = a.fetchAvatarId(),
//                    login = a.fetchLogin(),
//                    name = a.fetchName(),
//                    surname = a.fetchSurname(),
//                    praname = a.fetchSurname()
//                ))
                scope.launch {
                    async { fetchQuickTab(period = state().period)  }
                    async { fetchGrades() }
                    async { fetchTeacherGroups() }
                }


            }
        }
    }

    private fun fetchGrades() {
        scope.launch {
            try {
                gradesNInterface.nStartLoading()
                val grades = mainRepository.fetchRecentGrades(state().login).grades
                dispatch(Message.GradesUpdated(grades))
                gradesNInterface.nSuccess()
            } catch (e: Throwable) {
                println(e)
                gradesNInterface.nError("Не удалось загрузить список оценок") {
                    fetchGrades()
                }
//                groupListComponent.onEvent(ListDialogStore.Intent.CallError("Не удалось загрузить список групп =/") { fetchTeacherGroups() })
            }
        }
    }

    private fun fetchTeacherGroups() {
        scope.launch {
            try {
                teacherNInterface.nStartLoading()
                val groups = mainRepository.fetchTeacherGroups().groups
                dispatch(Message.TeacherGroupUpdated(groups))
                teacherNInterface.nSuccess()
            } catch (e: Throwable) {
                println(e)
                teacherNInterface.nError("Не удалось загрузить список групп") {
                    fetchTeacherGroups()
                }
//                groupListComponent.onEvent(ListDialogStore.Intent.CallError("Не удалось загрузить список групп =/") { fetchTeacherGroups() })
            }
        }
    }

    fun fetchQuickTab(period: HomeStore.Period) {
        scope.launch {
            quickTabNInterface.nStartLoading()
            try {
                val avg = mainRepository.fetchMainAvg(state().login, reason = period.ordinal.toString())
                val avgMap = state().averageGradePoint.toMutableMap()
                val stupsMap = state().ladderOfSuccess.toMutableMap()
                avgMap[period] = avg.avg
                stupsMap[period] = avg.stups
                dispatch(Message.QuickTabUpdated(avg = avgMap.toMap(HashMap()), stups = stupsMap.toMap(HashMap())))
            } catch (_: Throwable) {
                quickTabNInterface.nError("Ошибка") {
                    fetchQuickTab(period)
                }
            }
        }
    }
}
