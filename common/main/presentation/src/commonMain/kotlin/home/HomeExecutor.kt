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
import main.Period
import main.RDeleteMainNotificationsReceive
import main.RFetchMainHomeTasksCountReceive
import main.RFetchMainNotificationsReceive
import schedule.PersonScheduleItem
import server.Roles

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
            Intent.ChangePeriod -> changePeriod(
                period = when (state().period) {
                    Period.WEEK -> Period.MODULE
                    Period.MODULE -> Period.HALF_YEAR
                    Period.HALF_YEAR -> Period.YEAR
                    Period.YEAR -> Period.WEEK
                }
            )

            is Intent.UpdateHomeWorkEmoji -> {
                dispatch(
                    Message.UpdateHomeWorkEmoji(
                        emoji = getEmoji(
                            count = intent.count
                        )
                    )
                )
            }

            is Intent.CheckNotification -> scope.launch(CDispatcher) {
                try {
                    mainRepository.deleteMainNotification(
                        RDeleteMainNotificationsReceive(
                            key = intent.key
                        )
                    )
                    val newNotifications = state().notifications.toMutableList()
                    newNotifications.removeAll { it.key == intent.key }
                    scope.launch {
                        dispatch(
                            Message.NotificationsUpdated(newNotifications)
                        )
                    }
                } catch (_: Throwable) {

                }
            }
        }
    }

    private fun fetchChildrenNotifications() {
        scope.launch(CDispatcher) {
            try {
                quickTabNInterface.nStartLoading()
                val r =
                    mainRepository.fetchChildrenMainNotifications()

                scope.launch {
                    dispatch(Message.ChildrenNotificationsInited(
                        notChildren = r.students,
                        childrenNotifications = r.notifications
                    ))
                    quickTabNInterface.nSuccess()
                }
            } catch (e: Throwable) {
                quickTabNInterface.nError("Не удалось загрузить уведомления") {
                    fetchChildren()
                }
            }
        }
    }

    private fun changePeriod(period: Period) {
        dispatch(Message.PeriodChanged(period))
        fetchQuickTab(period, false)
    }

    private fun init() {
        scope.launch(CDispatcher) {
            if (state().role == Roles.student) {
                fetchQuickTab(period = state().period, isFirst = true)
                fetchGrades()
                fetchHomeTasksCount()
                fetchNotifications()
            }
            else if (state().role == Roles.teacher) {
                fetchTeacherGroups()
                fetchSchedule(
                    dayOfWeek = state().currentDate.first.toString(),
                    date = state().currentDate.second
                )
            }
            if (state().isParent) {
                fetchChildren()
            }
            if(state().isParent || state().isMentor) {
                fetchChildrenNotifications()
            }
        }
        journalComponent?.onEvent(JournalStore.Intent.Init)
    }

    private fun fetchChildren() {
        scope.launch(CDispatcher) {
            try {
                gradesNInterface.nStartLoading()
                val r =
                    mainRepository.fetchChildren()

                scope.launch {
                    dispatch(Message.ChildrenUpdated(r.children))
                    gradesNInterface.nSuccess()
                }
            } catch (e: Throwable) {
                gradesNInterface.nError("Не удалось загрузить список детей") {
                    fetchChildren()
                }
            }
        }
    }

    private fun fetchHomeTasksCount() {
        scope.launch(CDispatcher) {
            try {
                val count = mainRepository.fetchMainHomeTasksCount(
                    RFetchMainHomeTasksCountReceive(
                        studentLogin = state().login
                    )
                ).count
                println("COUNT: ${count}")
                scope.launch {
                    dispatch(
                        Message.UpdateHomeWorkEmoji(
                            getEmoji(count)
                        )
                    )
                }
            } catch (e: Throwable) {

            }
        }
    }

    private fun fetchNotifications() {
        scope.launch(CDispatcher) {
            try {
                val notifications = mainRepository.fetchMainNotifications(
                    RFetchMainNotificationsReceive(studentLogin = state().login)
                ).notifications
                scope.launch {
                    dispatch(
                        Message.NotificationsUpdated(
                            notifications = notifications
                        )
                    )
                    println("NOTS: ${notifications}")
                }
            } catch (e: Throwable) {

            }
        }
    }

    private fun fetchSchedule(dayOfWeek: String, date: String) {
        scope.launch(CDispatcher) {
//            if ((state().items[date] ?: listOf()).isEmpty()) {
            try {
                scheduleNInterface.nStartLoading()
                val response =
                    mainRepository.fetchPersonSchedule(
                        dayOfWeek = dayOfWeek,
                        date = date,
                        login = state().login
                    )
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

    private fun fetchQuickTab(period: Period, isFirst: Boolean) {
        scope.launch(CDispatcher) {
            quickTabNInterface.nStartLoading()
            try {
                val avg =
                    mainRepository.fetchMainAvg(state().login, reason = period.ordinal.toString(), isFirst = isFirst)
                val avgMap = state().averageGradePoint.toMutableMap()
                val stupsMap = state().ladderOfSuccess.toMutableMap()
                avgMap[period] = avg.avg
                stupsMap[period] = avg.stups
                scope.launch {
                    dispatch(
                        Message.QuickTabUpdated(
                            avg = avgMap.toMap(HashMap()),
                            stups = stupsMap.toMap(HashMap()),
                            achievements = avg.achievementsStups
                        )
                    )

                    quickTabNInterface.nSuccess()
                }
            } catch (_: Throwable) {
                quickTabNInterface.nError("Ошибка") {
                    fetchQuickTab(period, isFirst)
                }
            }
        }
    }
}
