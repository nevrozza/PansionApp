package home

import AuthRepository
import CDispatcher
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
import journal.JournalComponent
import journal.JournalStore
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import schedule.PersonScheduleItem

class HomeExecutor(
    private val authRepository: AuthRepository,
    private val mainRepository: MainRepository,
    private val quickTabNInterface: NetworkInterface,
    private val teacherNInterface: NetworkInterface,
    private val gradesNInterface: NetworkInterface,
    private val scheduleNInterface: NetworkInterface,
    private val journalComponent: JournalComponent?
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            is Intent.Init -> init()
            Intent.ChangeIsDatesShown -> dispatch(Message.IsDatesShownChanged)
            is Intent.ChangeDate -> scope.launch {
                dispatch(Message.DateChanged(intent.date))
                fetchSchedule(dayOfWeek = intent.date.first.toString(), date = intent.date.second)
            }

            is Intent.UpdateSomeHeaders -> dispatch(Message.SomeHeadersUpdated(intent.someHeaders))
            is Intent.UpdateAvatarId -> dispatch(Message.AvatarIdUpdated(intent.avatarId))
        }
    }

    private fun init() {
        scope.launch(CDispatcher) {
            fetchQuickTab(period = state().period)
            fetchGrades()
            fetchTeacherGroups()
            fetchSchedule(
                dayOfWeek = state().currentDate.first.toString(),
                date = state().currentDate.second
            )
        }
        journalComponent?.onEvent(JournalStore.Intent.Init)
    }

    private fun fetchSchedule(dayOfWeek: String, date: String) {
        scope.launch(CDispatcher) {
//            if ((state().items[date] ?: listOf()).isEmpty()) {
                try {
                    scheduleNInterface.nStartLoading()
                    val response =
                        mainRepository.fetchPersonSchedule(dayOfWeek = dayOfWeek, date = date)
                    val newList = state().items.toMutableMap()
                    response.list.forEach {
                        newList[it.key] = it.value
                    }
                    scope.launch {
                        dispatch(Message.ItemsUpdated(newList.toMap(HashMap())))
                        scheduleNInterface.nSuccess()
                    }
                } catch (e: Throwable) {
                    println(e)
                    scheduleNInterface.nError("Не удалось загрузить расписание") {
                        fetchSchedule(dayOfWeek = dayOfWeek, date = date)
                    }
//                groupListComponent.onEvent(ListDialogStore.Intent.CallError("Не удалось загрузить список групп =/") { fetchTeacherGroups() })
                }
//            }
        }
    }

    private fun fetchGrades() {
        scope.launch(CDispatcher) {
            try {
                gradesNInterface.nStartLoading()
                val grades = mainRepository.fetchRecentGrades(state().login).grades
                scope.launch {
                    dispatch(Message.GradesUpdated(grades))
                    gradesNInterface.nSuccess()
                }
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
        scope.launch(CDispatcher) {
            try {
                teacherNInterface.nStartLoading()
                val groups = mainRepository.fetchTeacherGroups().groups
                scope.launch {
                    dispatch(Message.TeacherGroupUpdated(groups))

                    teacherNInterface.nSuccess()
                }
            } catch (e: Throwable) {
                println(e)
                teacherNInterface.nError("Не удалось загрузить список групп") {
                    fetchTeacherGroups()
                }
//                groupListComponent.onEvent(ListDialogStore.Intent.CallError("Не удалось загрузить список групп =/") { fetchTeacherGroups() })
            }
        }
    }

    private fun fetchQuickTab(period: HomeStore.Period) {
        scope.launch(CDispatcher) {
            quickTabNInterface.nStartLoading()
            try {
                val avg =
                    mainRepository.fetchMainAvg(state().login, reason = period.ordinal.toString())
                val avgMap = state().averageGradePoint.toMutableMap()
                val stupsMap = state().ladderOfSuccess.toMutableMap()
                avgMap[period] = avg.avg
                stupsMap[period] = avg.stups
                scope.launch {
                    dispatch(
                        Message.QuickTabUpdated(
                            avg = avgMap.toMap(HashMap()),
                            stups = stupsMap.toMap(HashMap())
                        )
                    )

                    quickTabNInterface.nSuccess()
                }
            } catch (_: Throwable) {
                quickTabNInterface.nError("Ошибка") {
                    fetchQuickTab(period)
                }
            }
        }
    }
}
