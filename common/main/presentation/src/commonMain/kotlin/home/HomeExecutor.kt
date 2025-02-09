package home

import JournalRepository
import MainRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.networkInterface.NetworkInterface
import deviceSupport.launchIO
import deviceSupport.withMain
import home.HomeStore.Intent
import home.HomeStore.Label
import home.HomeStore.Message
import home.HomeStore.State
import journal.JournalComponent
import journal.JournalStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import main.Period
import main.RChangeToUv
import main.RDeleteMainNotificationsReceive
import main.RFetchMainHomeTasksCountReceive
import main.RFetchMainNotificationsReceive
import report.RMarkLessonReceive
import schedule.PersonScheduleItemWithNum
import server.Roles
import server.toMinutes

class HomeExecutor(
    private val mainRepository: MainRepository,
    private val journalRepository: JournalRepository,
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
            is Intent.ChangeDate -> {
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
                    Message.UpdateHomeWorkEmoji(intent.count)
                )
            }

            is Intent.CheckNotification -> scope.launchIO {
                try {
                    mainRepository.deleteMainNotification(
                        RDeleteMainNotificationsReceive(
                            key = intent.key
                        )
                    )
                    if(intent.login != null) {

                        val newChildNotifications = state().childrenNotifications.toMutableMap()
                        val newNotifications = newChildNotifications[intent.login]?.toMutableList() ?: mutableListOf()
                        newNotifications.removeAll { it.key == intent.key }
                        newChildNotifications[intent.login] = newNotifications
                        withMain {
                            dispatch(Message.ChildrenNotificationsInited(
                                notChildren = state().notChildren,
                                childrenNotifications = newChildNotifications
                            ))
                        }
                    } else {
                        val newNotifications = state().notifications.toMutableList()
                        newNotifications.removeAll { it.key == intent.key }
                        withMain {
                            dispatch(
                                Message.NotificationsUpdated(newNotifications)
                            )
                        }
                    }


                } catch (_: Throwable) {

                }
            }

            is Intent.ChangeToUv -> changeToUv(intent.reportId, intent.login, intent.isDeep)

            is Intent.MarkLesson -> markLesson(intent.lessonId)
        }
    }


    private fun markLesson(lessonId: Int) {
        scope.launchIO {
            try {

                journalRepository.toMarkLesson(
                    RMarkLessonReceive(
                        date = state().currentDate.second,
                        lessonId = lessonId
                    )
                )
                val newSchedule = state().items.toMutableMap()
                newSchedule[state().currentDate.second] = newSchedule[state().currentDate.second]?.map {
                    if (it.lessonIndex == lessonId) {
                        it.copy(isMarked = true)
                    } else it
                } ?: listOf()
                withMain {
                    dispatch(
                        Message.ItemsUpdated(newSchedule.toMap(HashMap()), lastUpdate = state().lastUpdate)
                    )
                }
            } catch (e: Throwable) {
                println(e)
            }
        }
    }

    private fun changeToUv(reportId: Int, login: String, isDeep: Boolean) {
        scope.launchIO {
            try {

                mainRepository.changeToUv(
                    RChangeToUv(
                        login = login,
                        reportId = reportId
                    )
                )
                if (isDeep) {
                    fetchChildrenNotifications()
                } else {
                    val newHots = state().childrenNotifications.toMutableMap()
                    newHots[login] = state().childrenNotifications[login]?.map {
                        val data = it.reason.split(".")
                        val type = data[0]
                        if (it.reportId == reportId && type == "N" && data[1] == "1") {
                            it.copy(
                                reason = "N.2"
                            )
                        } else {
                            it
                        }
                    } ?: listOf()
                    withMain {
                        dispatch(
                            Message.ChildrenNotificationsInited(
                                notChildren = state().notChildren,
                                childrenNotifications = newHots
                            )
                        )
                        quickTabNInterface.nSuccess()
                    }
                }
            } catch (e: Throwable) {
                println(e)
            }
        }
    }

    private fun fetchChildrenNotifications() {
        scope.launchIO {
            try {
                quickTabNInterface.nStartLoading()
                val r =
                    mainRepository.fetchChildrenMainNotifications()
                withMain {
                    dispatch(
                        Message.ChildrenNotificationsInited(
                            notChildren = r.students,
                            childrenNotifications = r.notifications
                        )
                    )
                    quickTabNInterface.nSuccess()
                }
            } catch (e: Throwable) {
                quickTabNInterface.nError("Не удалось загрузить уведомления", e) {
                    fetchChildrenNotifications()
                }
            }
        }
    }

    private fun changePeriod(period: Period) {
        dispatch(Message.PeriodChanged(period))
        fetchQuickTab(period, false)
    }

    private fun init() {
        scope.launchIO {
            if (state().role == Roles.STUDENT) {
                fetchQuickTab(period = state().period, isFirst = true)
                fetchGrades()
                fetchHomeTasksCount()
                fetchNotifications()
                fetchSchedule(
                    dayOfWeek = state().currentDate.first.toString(),
                    date = state().currentDate.second
                )
            } else if (state().role == Roles.TEACHER) {
                fetchTeacherGroups()
                fetchSchedule(
                    dayOfWeek = state().currentDate.first.toString(),
                    date = state().currentDate.second
                )
            }
            if (state().isParent) {
                fetchChildren()
            }
            if (state().isParent || state().isMentor) {
                fetchChildrenNotifications()
            }
            if ((state().isMentor || state().isModer) && state().role != Roles.TEACHER) {
                fetchTeacherGroups()
            }
        }
        journalComponent?.onEvent(JournalStore.Intent.Init)
    }

    private fun fetchChildren() {
        scope.launchIO {
            try {
                gradesNInterface.nStartLoading()
                val r =
                    mainRepository.fetchChildren()

                withMain {
                    dispatch(Message.ChildrenUpdated(r.children))
                    gradesNInterface.nSuccess()
                }
            } catch (e: Throwable) {
                gradesNInterface.nError("Не удалось загрузить список детей", e) {
                    fetchChildren()
                }
            }
        }
    }

    private fun fetchHomeTasksCount() {
        scope.launchIO {
            teacherNInterface.nStartLoading()
            try {
                val count = mainRepository.fetchMainHomeTasksCount(
                    RFetchMainHomeTasksCountReceive(
                        studentLogin = state().login
                    )
                ).count
                withMain {
                    dispatch(
                        Message.UpdateHomeWorkEmoji(
                            count
                        )
                    )
                    teacherNInterface.nSuccess()
                }
            } catch (e: Throwable) {
                teacherNInterface.nSuccess()
            }
        }
    }

    private fun fetchNotifications() {
        scope.launchIO {
            try {
                val notifications = mainRepository.fetchMainNotifications(
                    RFetchMainNotificationsReceive(studentLogin = state().login)
                ).notifications
                withMain {
                    dispatch(
                        Message.NotificationsUpdated(
                            notifications = notifications
                        )
                    )
                }
            } catch (_: Throwable) {

            }
        }
    }

    private fun fetchSchedule(dayOfWeek: String, date: String) {
        scope.launchIO {
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
                    var num = 0
                    val items = it.value.sortedBy { it.start.toMinutes() }.map { item ->
                        if (item.groupId !in listOf(-11)) num +=1
                        PersonScheduleItemWithNum(
                            groupId = item.groupId,
                            teacherFio = item.teacherFio,
                            cabinet = item.cabinet,
                            start = item.start,
                            end = item.end,
                            subjectName = item.subjectName,
                            groupName = item.groupName,
                            marks = item.marks,
                            stupsSum = item.stupsSum,
                            isSwapped = item.isSwapped,
                            num = num,
                            lessonIndex = item.lessonIndex,
                            isMarked = item.isMarked
                        )
                    }
                    newList[it.key] = items
                }
                withContext(Dispatchers.Main) {

                    println("CHECK: ${this.coroutineContext}")
                    dispatch(Message.ItemsUpdated(newList.toMap(HashMap()), lastUpdate = response.lastUpdate))
                    scheduleNInterface.nSuccess()
                }
            } catch (e: Throwable) {
                println(e)
                scheduleNInterface.nError("Не удалось загрузить расписание", e) {
                    fetchSchedule(dayOfWeek = dayOfWeek, date = date)
                }
//                groupListComponent.onEvent(ListDialogStore.Intent.CallError("Не удалось загрузить список групп =/") { fetchTeacherGroups() })
            }
//            }
        }
    }

    private fun fetchGrades() {
        scope.launchIO {
            try {
                gradesNInterface.nStartLoading()
                val r = mainRepository.fetchRecentGrades(state().login)
                val grades = r.grades
                withMain {
                    dispatch(Message.GradesUpdated(grades, r.isAnyDepts))
                    gradesNInterface.nSuccess()
                }
            } catch (e: Throwable) {
                println(e)
                gradesNInterface.nError("Не удалось загрузить список оценок", e) {
                    fetchGrades()
                }
//                groupListComponent.onEvent(ListDialogStore.Intent.CallError("Не удалось загрузить список групп =/") { fetchTeacherGroups() })
            }
        }
    }

    private fun fetchTeacherGroups() {
        scope.launchIO {
            try {
                teacherNInterface.nStartLoading()
                val groups = mainRepository.fetchTeacherGroups().groups.sortedBy { it.subjectId }
                    .sortedBy { it.teacherLogin != state().login }
                withMain {
                    dispatch(Message.TeacherGroupUpdated(groups))

                    teacherNInterface.nSuccess()
                }
            } catch (e: Throwable) {
                println(e)
                teacherNInterface.nError("Не удалось загрузить список групп", e) {
                    fetchTeacherGroups()
                }
//                groupListComponent.onEvent(ListDialogStore.Intent.CallError("Не удалось загрузить список групп =/") { fetchTeacherGroups() })
            }
        }
    }

    private fun fetchQuickTab(period: Period, isFirst: Boolean) {
        scope.launchIO {
            quickTabNInterface.nStartLoading()
            try {
                val avg =
                    mainRepository.fetchMainAvg(
                        state().login,
                        reason = period.ordinal.toString(),
                        isFirst = isFirst
                    )
                val avgMap = state().averageGradePoint.toMutableMap()
                val stupsMap = state().ladderOfSuccess.toMutableMap()
                avgMap[period] = avg.avg
                stupsMap[period] = avg.stups
                withMain {
                    dispatch(
                        Message.QuickTabUpdated(
                            avg = avgMap.toMap(HashMap()),
                            stups = stupsMap.toMap(HashMap()),
                            achievements = avg.achievementsStups
                        )
                    )

                    quickTabNInterface.nSuccess()
                }
            } catch (e: Throwable) {
                quickTabNInterface.nError("Ошибка", e) {
                    fetchQuickTab(period, isFirst)
                }
            }
        }
    }
}
