package lessonReport

import AuthRepository
import CDispatcher
import JournalRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.cAlertDialog.CAlertDialogComponent
import components.cAlertDialog.CAlertDialogStore
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.networkInterface.NetworkInterface
import homework.CreateReportHomeworkItem
import homework.RFetchReportHomeTasksReceive
import homework.RSaveReportHomeTasksReceive
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lessonReport.LessonReportStore.Intent
import report.*
import server.*

class LessonReportExecutor(
    private val setMarkMenuComponent: ListComponent,
    private val setDzMarkMenuComponent: ListComponent,
    private val deleteMarkMenuComponent: ListComponent,
    private val setLateTimeMenuComponent: ListComponent,
    private val nInterface: NetworkInterface,
    private val nHomeTasksInterface: NetworkInterface,
    private val journalRepository: JournalRepository,
    private val authRepository: AuthRepository,
    private val marksDialogComponent: CAlertDialogComponent,
    private val header: ReportHeader
) :
    CoroutineExecutor<Intent, Unit, LessonReportStore.State, LessonReportStore.Message, LessonReportStore.Label>() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun executeIntent(intent: Intent) {


        when (intent) {
            is Intent.CreateColumn -> {
                val newList = state().columnNames.toMutableList()
                newList.add(
                    ReportColumn(
                        title = intent.columnName,
                        type = intent.reasonId
                    )
                )

                newList.sortBy { customOrder[it.type] }

                dispatch(LessonReportStore.Message.ColumnsUpdated(newList.toList()))
            }

            is Intent.DeleteColumnInit -> {
                dispatch(
                    LessonReportStore.Message.DeleteColumnInited(
                        intent.reportColumn
                    )
                )
            }

            is Intent.DeleteColumn -> {
                val newStudentList = state().students.toMutableList()
                val finalStudentList = mutableListOf<StudentLine>()
                newStudentList.forEach { line ->
                    val newLine = line.marksOfCurrentLesson.toMutableList()
                    newLine.removeAll {
                        it.reason == (state().deletingReportColumn?.type ?: "")
                    }
                    finalStudentList.add(line.copy(marksOfCurrentLesson = newLine))
                }

                val newList = state().columnNames.toMutableList()
                newList.remove(state().deletingReportColumn)

                dispatch(LessonReportStore.Message.ColumnsUpdated(newList.toList()))
                dispatch(LessonReportStore.Message.StudentsUpdated(finalStudentList))
            }

            Intent.ClearSelection -> dispatch(LessonReportStore.Message.SelectionCleared)
            is Intent.OpenSetMarksMenu -> {
                dispatch(
                    LessonReportStore.Message.MarksMenuOpened(
                        reasonId = intent.reasonId,
                        studentLogin = intent.studentLogin,
                        markValue = "",
                        selectedDeploy = ""
                    )
                )

                if (intent.reasonId.st == "!dz") {
                    setDzMarkMenuComponent.onEvent(
                        ListDialogStore.Intent.ShowDialog
                    )
                } else {
                    setMarkMenuComponent.onEvent(
                        ListDialogStore.Intent.ShowDialog
                    )
                }
            }

            is Intent.OpenDeleteMarkMenu -> {
                dispatch(
                    LessonReportStore.Message.MarksMenuOpened(
                        reasonId = intent.reasonId,
                        studentLogin = intent.studentLogin,
                        markValue = intent.markValue.toString(),
                        selectedDeploy = intent.selectedDeploy
                    )
                )
                deleteMarkMenuComponent.onEvent(
                    ListDialogStore.Intent.ShowDialog
                )
            }

            is Intent.SetMark -> {
                val newList = state().students.toMutableList()
                val line = newList.first { it.login == state().selectedLogin }
                newList.remove(line)
                val newMarksList = line.marksOfCurrentLesson.toMutableList()
                newMarksList.add(
                    Mark(
                        intent.mark,
                        state().selectedMarkReason,
                        true,
                        id = state().ids + 1,
                        date = state().date,
                        deployTime = getSixTime(),
                        deployLogin = authRepository.fetchLogin(),
                        deployDate = getDate()
                    )
                )
                newList.add(
                    line.copy(
                        marksOfCurrentLesson = newMarksList
                    )
                )
                dispatch(LessonReportStore.Message.StudentsUpdated(newList))
            }

            Intent.DeleteMark -> {
                val newStudentList = state().students.toMutableList()


                val studentLine = state().students.first { it.login == state().selectedLogin }
                val marks =
                    studentLine.marksOfCurrentLesson.filter { it.reason == state().selectedMarkReason }
                        .toMutableList()
                marks.removeAt(state().selectedMarkValue.toInt())

                val finalMarks = studentLine.marksOfCurrentLesson.toMutableList()
                finalMarks.removeAll { it.reason == state().selectedMarkReason }
                finalMarks.addAll(marks)

                newStudentList.remove(studentLine)
                newStudentList.add(studentLine.copy(marksOfCurrentLesson = finalMarks))

                dispatch(LessonReportStore.Message.StudentsUpdated(newStudentList))

            }

            is Intent.ChangeStups -> {

                val newList = state().students.toMutableList()
                val line = newList.first { it.login == intent.login }

                newList.remove(line)
                val newStupsList = line.stupsOfCurrentLesson.toMutableList()
                if (line.stupsOfCurrentLesson.count { it.reason == intent.columnReason } != 0) {
                    newStupsList.removeAll { it.reason == intent.columnReason }
                }

                newStupsList.add(
                    Stup(
                        value = intent.value,
                        reason = intent.columnReason,
                        id = state().ids,
                        deployTime = getSixTime(),
                        deployLogin = authRepository.fetchLogin(),
                        deployDate = getDate(),
                        custom = null
                    )
                )

                newList.add(
                    line.copy(
                        stupsOfCurrentLesson = newStupsList
                    )
                )

                dispatch(LessonReportStore.Message.StudentsUpdated(newList))

            }

            is Intent.ChangeSettingsTab -> dispatch(
                LessonReportStore.Message.SettingsTabChanged(
                    intent.settingsTab
                )
            )

            is Intent.ChangeTopic -> dispatch(
                LessonReportStore.Message.TopicChanged(
                    intent.topic
                )
            )

            is Intent.ChangeDescription -> dispatch(
                LessonReportStore.Message.DescriptionChanged(
                    intent.description
                )
            )

            is Intent.DislikeStudent -> {
                val newDislikedList = state().dislikedList.toMutableList()
                val newLikedList = state().likedList.toMutableList()
                newLikedList.remove(intent.studentLogin)
                if (intent.studentLogin in newDislikedList) {
                    newDislikedList.remove(intent.studentLogin)
                } else {
                    newDislikedList.add(intent.studentLogin)
                }
                dispatch(
                    LessonReportStore.Message.RepUpdated(
                        likedList = newLikedList,
                        dislikedList = newDislikedList
                    )
                )
            }

            is Intent.LikeStudent -> {
                val newLikedList = state().likedList.toMutableList()
                val newDislikedList = state().dislikedList.toMutableList()
                newDislikedList.remove(intent.studentLogin)
                if (intent.studentLogin in newLikedList) {
                    newLikedList.remove(intent.studentLogin)
                } else {
                    newLikedList.add(intent.studentLogin)
                }

                dispatch(
                    LessonReportStore.Message.RepUpdated(
                        dislikedList = newDislikedList,
                        likedList = newLikedList
                    )
                )
            }


            is Intent.ChangeAttendance -> {

                val newStudentList = state().students.toMutableList()
                val student = state().students.first { it.login == intent.studentLogin }

                val previousAttendance = student.attended?.attendedType
                val previousLateTime = student.lateTime
                newStudentList.remove(student)
                newStudentList.add(
                    student.copy(
                        attended = if (student.attended != null) student.attended.copy(
                            attendedType = intent.attendedType
                        ) else Attended(attendedType = intent.attendedType, null),
                        lateTime = "0",
                        stupsOfCurrentLesson = student.stupsOfCurrentLesson.map {

                            if (it.reason.st == "!ds") {
                                val toAdd = (fetchStupsForAttendance(
                                    reason = it.reason,
                                    attendedType = intent.attendedType
                                ) ?: 0) + (fetchStupsForLateTime(
                                    reason = it.reason,
                                    lateTime = "0"
                                ))
                                val toMinus = (fetchStupsForAttendance(
                                    reason = it.reason,
                                    attendedType = previousAttendance
                                ) ?: 0) + (fetchStupsForLateTime(
                                    reason = it.reason,
                                    lateTime = previousLateTime
                                ))
                                Stup(
                                    value = it.value + toAdd - toMinus,
                                    reason = it.reason,
                                    id = it.id,
                                    deployTime = it.deployTime,
                                    deployDate = it.deployDate,
                                    deployLogin = it.deployLogin,
                                    custom = it.custom
                                )

                            } else {
                                it
                            }
                        }
                    )
                )

                dispatch(LessonReportStore.Message.StudentsUpdated(newStudentList))
            }

            is Intent.SetLateTime -> {
                val result = if (intent.chosenTime == "auto") {
                    val start = state().time.split(":")
                    val startMinutes = start[0].toInt() * 60 + start[1].toInt()
                    val current = getSixTime().split(":")
                    val currentMinutes = current[0].toInt() * 60 + current[1].toInt()
                    val lateMinutes = currentMinutes - startMinutes
                    lateMinutes.toSixTime().removePrefix("00:")
                    //                    val currentTime = Clock.System.now()
                    //                        .toLocalDateTime(TimeZone.of("UTC+3")).toString()
                    //                        .cut(16)
                } else {
                    intent.chosenTime
                }

                val newStudentList = state().students.toMutableList()

                val student = state().students.first { it.login == intent.studentLogin }


                val previousLateTime = student.lateTime

                newStudentList.remove(student)
                newStudentList.add(student.copy(
                    lateTime = "$result${if (intent.chosenTime != "0") " мин" else ""}",
                    stupsOfCurrentLesson = student.stupsOfCurrentLesson.map {
                            if (it.reason == "!ds3") {
                                val toAdd = (fetchStupsForLateTime(
                                    reason = it.reason,
                                    lateTime = result
                                ))
                                val toMinus = (fetchStupsForLateTime(
                                    reason = it.reason,
                                    lateTime = previousLateTime
                                ))

                                println("DS3LATE: ${previousLateTime} ${toMinus}")
                                Stup(
                                    value = it.value + toAdd - toMinus,
                                    reason = it.reason,
                                    id = it.id,
                                    deployTime = it.deployTime,
                                    deployDate = it.deployDate,
                                    deployLogin = it.deployLogin,
                                    custom = it.custom
                                )

                            } else {
                                it
                            }
                        }
                ))

                dispatch(LessonReportStore.Message.StudentsUpdated(newStudentList))
            }


            is Intent.OpenSetLateTimeMenu -> {


                dispatch(
                    LessonReportStore.Message.LateTimeMenuOpened(
                        studentLogin = intent.studentLogin
                    )
                )
                setLateTimeMenuComponent.onEvent(
                    ListDialogStore.Intent.ShowDialog
                )
            }

            Intent.ChangeInfoShowing -> dispatch(LessonReportStore.Message.InfoShowingChanged)
            Intent.UpdateWholeReport -> updateWholeReport()

            Intent.ChangeIsMentorWas -> dispatch(LessonReportStore.Message.IsMentorWasChanged)
            is Intent.ChangeStatus -> dispatch(
                LessonReportStore.Message.StatusChanged(
                    intent.status
                )
            )

            is Intent.Init -> {
                init()
                fetchHomeTasks()
            }

            is Intent.OpenDetailedMarks -> openDetailedMarks(intent.studentLogin)
            is Intent.IsSavedAnimation -> dispatch(
                LessonReportStore.Message.IsSavedAnimation(
                    intent.isSaved
                )
            )

            is Intent.IsErrorAnimation -> dispatch(
                LessonReportStore.Message.IsErrorAnimation(
                    intent.isError
                )
            )

            is Intent.AddEmptyHomeTask -> dispatch(
                LessonReportStore.Message.HomeTasksUpdated(
                    state().hometasks + CreateReportHomeworkItem(
                        id = ((state().hometasks.lastOrNull()?.id) ?: (-1)) + 1,
                        type = "",
                        text = "",
                        stups = 0,
                        fileIds = null,
                        studentLogins = intent.studentLogins,
                        isNew = true,
                        isNec = true
                    )
                )
            )

            is Intent.ChangeHomeTaskType -> scope.launch {
                updateTasksToEditIds(id = intent.id, isNew = intent.isNew)
                val newHomeTasks = state().hometasks.toMutableList()
                val item = state().hometasks.first { it.id == intent.id }
                val index = state().hometasks.indexOf(item)
                newHomeTasks[index] = item.copy(
                    type = intent.type,
                    stups = if (intent.type.contains("!st")) item.stups else 0
                )
                dispatch(
                    LessonReportStore.Message.HomeTasksUpdated(
                        newHomeTasks
                    )
                )
            }

            is Intent.ChangeHomeTaskIsNec -> scope.launch {
                updateTasksToEditIds(id = intent.id, isNew = intent.isNew)
                val newHomeTasks = state().hometasks.toMutableList()
                val item = state().hometasks.first { it.id == intent.id }
                val index = state().hometasks.indexOf(item)
                newHomeTasks[index] = item.copy(
                    isNec = intent.isNec
                )
                dispatch(
                    LessonReportStore.Message.HomeTasksUpdated(
                        newHomeTasks
                    )
                )
            }

            is Intent.ChangeHomeTaskAward -> scope.launch {
                updateTasksToEditIds(id = intent.id, isNew = intent.isNew)
                val newHomeTasks = state().hometasks.toMutableList()
                val item = state().hometasks.first { it.id == intent.id }
                val index = state().hometasks.indexOf(item)
                newHomeTasks[index] = item.copy(stups = intent.award)
                dispatch(
                    LessonReportStore.Message.HomeTasksUpdated(
                        newHomeTasks
//                        state().hometasks.map {
//                            if (it.id == intent.id) it.copy(stups = intent.award)
//                            else it
//                        }
                    )
                )
            }

            is Intent.ChangeHomeTaskText -> { //scope.launch
                updateTasksToEditIds(id = intent.id, isNew = intent.isNew)
                val homeTasks = state().hometasks
                val index = homeTasks.indexOfFirst { it.id == intent.id }
                val newHomeTasks = homeTasks.toMutableList().apply {
                    this[index] = this[index].copy(text = intent.text)
                }
                dispatch(
                    LessonReportStore.Message.HomeTasksUpdated(
                        newHomeTasks
                    )
                )
            }

            Intent.SaveHomeTasks -> saveHomeTasks()
            is Intent.IsHomeTasksSavedAnimation -> dispatch(
                LessonReportStore.Message.IsHomeTasksSavedAnimation(
                    intent.isSaved
                )
            )

            is Intent.IsHomeTasksErrorAnimation -> dispatch(
                LessonReportStore.Message.IsHomeTasksErrorAnimation(
                    intent.isError
                )
            )

            is Intent.UpdateTabLoginsId -> dispatch(
                LessonReportStore.Message.TabLoginsIdUpdated(
                    intent.tabLogins
                )
            )

            is Intent.AddLoginToNewTab -> scope.launch {
                val newNewTabLogins = state().newTabLogins.toMutableList()
                newNewTabLogins.add(intent.login)
                dispatch(LessonReportStore.Message.NewTabsLoginsUpdated(newNewTabLogins))
            }

            is Intent.DeleteLoginFromNewTab -> scope.launch {
                val newNewTabLogins = state().newTabLogins.toMutableList()
                newNewTabLogins.remove(intent.login)
                dispatch(LessonReportStore.Message.NewTabsLoginsUpdated(newNewTabLogins))
            }

            Intent.OnTasksTabAcceptClick -> scope.launch {
                if (state().tabLogins == null) {
                    val newTabs = state().homeTasksNewTabs.toMutableList()
                    newTabs.add(state().newTabLogins)
                    dispatch(LessonReportStore.Message.SaveTabLoginsUpdated(newTabs))
                } else {
                    if (state().tabLogins in state().homeTasksNewTabs) {
                        val a = state().homeTasksNewTabs.map {
                            if (it == state().tabLogins) state().newTabLogins
                            else it
                        }
                        dispatch(LessonReportStore.Message.SaveTabLoginsUpdated(a))
                    }
                    val newTasks = state().hometasks.map {
                        if (it.studentLogins == state().tabLogins) {
                            updateTasksToEditIds(id = it.id, isNew = it.isNew)
                            it.copy(studentLogins = state().newTabLogins)
                        } else it
                    }
                    dispatch(LessonReportStore.Message.HomeTasksUpdated(newTasks))
                }
            }
        }
    }

    private fun fetchStupsForAttendance(
        reason: String,
        attendedType: String?
    ): Int? {
        return if (reason.st == "!ds") {
            val reasonNum = reason.last().toString().toInt()
            when (attendedType) {
                "2" -> {
                    when (reasonNum) {
                        1 -> 0
                        2 -> 0
                        else -> 0
                    }
                }

                "1" -> {
                    when (reasonNum) {
                        1 -> 0
                        2 -> 0
                        else -> -10
                    }
                }

                else -> {
                    when (reasonNum) {
                        1 -> 1
                        2 -> 1
                        else -> 0
                    }
                }
            }
        } else null
    }

    private fun fetchStupsForLateTime(
        reason: String,
        lateTime: String
    ): Int {
        return if (reason == "!ds3") {
            val late = (lateTime.replace("мин", "").replace(">", "").trim()).toIntOrNull() ?: 0
            when {
                late >= 10 -> -10
                late >= 5 -> -5
                late >= 1 -> -1
                else -> 0
            }
        } else 0
    }

    private fun updateTasksToEditIds(id: Int, isNew: Boolean) {
        if (!isNew) {
            val ids = state().homeTasksToEditIds
            dispatch(LessonReportStore.Message.HomeTasksToEditIdsUpdated(ids + id))
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun saveHomeTasks() {
        GlobalScope.launch(CDispatcher) {
            nHomeTasksInterface.nStartLoading()
            try {
                val r = journalRepository.saveReportHomeTasks(
                    RSaveReportHomeTasksReceive(
                        subjectId = state().subjectId,
                        groupId = state().groupId,
                        reportId = state().lessonReportId,
                        tasks = state().hometasks.filter { (it.isNew && it.type.isNotBlank() && it.text.isNotBlank()) || (!it.isNew && it.id in state().homeTasksToEditIds) }
                    )
                )
                scope.launch {
                    dispatch(LessonReportStore.Message.HomeTasksToEditIdsUpdated(emptySet()))
                    dispatch(LessonReportStore.Message.HomeTasksUpdated(r.tasks))
                    dispatch(LessonReportStore.Message.IsHomeTasksSavedAnimation(true))
                    nHomeTasksInterface.nSuccess()
                }
            } catch (e: Throwable) {
                scope.launch {
                    dispatch(LessonReportStore.Message.IsHomeTasksErrorAnimation(true))
//                        dispatch(LessonReportStore.Message.isFABShowing(true))
                    nHomeTasksInterface.nError("Не удалось загрузить задания на сервер", e) {
                        //TODO
                        nHomeTasksInterface.goToNone()
                    }
                }
            }
        }
    }

    private fun fetchHomeTasks() {
        scope.launch(CDispatcher) {
            nHomeTasksInterface.nStartLoading()
            try {
                val tasks = journalRepository.fetchReportHomeTasks(
                    RFetchReportHomeTasksReceive(
                        reportId = state().lessonReportId
                    )
                )
                scope.launch {
                    nHomeTasksInterface.nSuccess()
                    dispatch(LessonReportStore.Message.HomeTasksUpdated(tasks.tasks))
                }
            } catch (e: Throwable) {
                scope.launch {
                    nHomeTasksInterface.nError("Не удалось загрузить данные с сервера", e) {
                        fetchHomeTasks()
                    }
                }
            }

        }
    }

    private fun init() {
        scope.launch {
            nInterface.nStartLoading()
            try {
                val studentsData = journalRepository.fetchReportStudents(
                    RFetchReportStudentsReceive(
                        reportId = state().lessonReportId,
                        module = header.module.toIntOrNull() ?: 1,
                        date = header.date,
                        minutes = header.time.toMinutes()
                    )
                )

                val students = mutableListOf<StudentLine>()
                val likedList = mutableListOf<String>()
                val dislikedList = mutableListOf<String>()


                studentsData.students.forEach { student ->
                    student.serverStudentLine
                    students.add(
                        StudentLine(
                            shortFio = student.shortFio,
                            login = student.serverStudentLine.login,
                            attended = student.serverStudentLine.attended,
                            lateTime = student.serverStudentLine.lateTime,
                            avgMark = AvgMark(
                                previousSum = student.prevSum,
                                countOfMarks = student.prevCount,
//                                        value = student.prevSum / (student.prevCount.toFloat())
                            ),
                            marksOfCurrentLesson = studentsData.marks.filter { it.login == student.serverStudentLine.login }
                                .map {
                                    Mark(
                                        value = it.content,
                                        reason = it.reason,
                                        isGoToAvg = it.isGoToAvg,
                                        id = it.id,
                                        date = "sad",
                                        deployTime = it.deployTime,
                                        deployDate = it.deployDate,
                                        deployLogin = it.deployLogin
                                    )
                                }.sortedWith(
                                    compareBy(
                                        { getLocalDate(it.deployDate).toEpochDays() },
                                        { it.deployTime.toMinutes() })
                                ),
                            stupsOfCurrentLesson = getInitedStups(
                                studentsData = studentsData,
                                student = student,
                                authRepository = authRepository
                            )
                        )
                    )

                    when (student.serverStudentLine.isLiked) {
                        "t" -> likedList.add(student.serverStudentLine.login)
                        "f" -> dislikedList.add(student.serverStudentLine.login)
                    }
                }

                dispatch(
                    LessonReportStore.Message.Inited(
                        students = students,
                        likedList = likedList,
                        dislikedList = dislikedList,
                        newStatus = studentsData.newStatus,
                        newTopic = studentsData.newTopic
                    )
                )
                nInterface.nSuccess()
            } catch (e: Throwable) {
                nInterface.nError("Что-то пошло не так", e) {
                    init()
                }
            }
        }

        scope.launch(CDispatcher) {
            while (true) {
                delay(1000 * 60 * 3)
                if (state().isUpdateNeeded && state().isEditable) {
                    updateWholeReport()
                }
            }
        }
    }

    private fun getInitedStups(
        studentsData: RFetchReportStudentsResponse,
        student: AddStudentLine,
        authRepository: AuthRepository
    ): List<Stup> {

        val init = studentsData.stups.filter { it.login == student.serverStudentLine.login }
            .map {
                Stup(
                    value = it.content.toInt(),
                    reason = it.reason,
                    id = it.id,
                    deployTime = it.deployTime,
                    deployDate = it.deployDate,
                    deployLogin = it.deployLogin,
                    custom = it.custom
                )
            }
        val toAdd = mutableListOf<Stup>()
        listOf("!ds3", "!ds2", "!ds1").forEach { reason ->
            if (init.none { it.reason == reason }) {
                val value = (fetchStupsForAttendance(
                    reason = reason,
                    attendedType = student.serverStudentLine.attended?.attendedType
                ) ?: 0) + fetchStupsForLateTime(
                    reason = reason,
                    lateTime = student.serverStudentLine.lateTime
                )
                toAdd.add(
                    Stup(
                        value = value,
                        reason = reason,
                        id = state().ids,
                        deployTime = getSixTime(),
                        deployLogin = authRepository.fetchLogin(),
                        deployDate = getDate(),
                        custom = null
                    )
                )
                dispatch(LessonReportStore.Message.InvisibleStupAdd)
            }
        }

        return (init + toAdd)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun updateWholeReport() {
        GlobalScope.launch(CDispatcher) {
            nInterface.nStartLoading()
            try {
//                        dispatch(LessonReportStore.Message.isFABShowing(false))
                val marks = mutableListOf<ServerRatingUnit>()
                val stups = mutableListOf<ServerRatingUnit>()
                state().students.forEach { s ->
                    s.marksOfCurrentLesson.forEach { m ->
                        marks.add(
                            ServerRatingUnit(
                                login = s.login,
                                id = m.id,
                                content = m.value.toString(),
                                reason = m.reason,
                                isGoToAvg = m.isGoToAvg,
                                deployTime = m.deployTime,
                                deployDate = m.deployDate,
                                deployLogin = m.deployLogin,
                                custom = null
                            )
                        )
                    }

                    s.stupsOfCurrentLesson.forEach { m ->
                        stups.add(
                            ServerRatingUnit(
                                login = s.login,
                                id = m.id,
                                content = m.value.toString(),
                                reason = m.reason,
                                isGoToAvg = true,
                                deployTime = m.deployTime,
                                deployLogin = m.deployLogin,
                                deployDate = m.deployDate,
                                custom = m.custom
                            )
                        )
                    }
                }
                val editTime = "${getDate()} (${getSixTime()})"

                val r = RUpdateReportReceive(
                    lessonReportId = state().lessonReportId,
//                            groupId = state().groupId,
//                            date = state().date,
//                            time = state().time,
                    topic = state().topic,
                    description = state().description,
                    students = state().students.map {
                        ServerStudentLine(
                            login = it.login,
                            lateTime = it.lateTime,
                            isLiked = if (it.login in state().likedList) "t" else if (it.login in state().dislikedList) "f" else "0",
                            attended = it.attended
                        )
                    },
                    columnNames = state().columnNames.filter { it.type != ColumnTypes.prisut && it.type != ColumnTypes.opozdanie && it.type != ColumnTypes.srBall }
                        .map { it.type },
                    status = state().status,
                    ids = state().ids,
                    editTime = editTime,
                    isMentorWas = state().isMentorWas,
                    marks = marks,
                    stups = stups,
                    edYear = state().edYear
                )

                journalRepository.updateWholeReport(r)
                scope.launch {
                    dispatch(LessonReportStore.Message.EditTimeChanged(editTime))
                    dispatch(LessonReportStore.Message.IsSavedAnimation(true))
                    nInterface.nSuccess()
                }
            } catch (e: Throwable) {
                scope.launch {
                    dispatch(LessonReportStore.Message.IsErrorAnimation(true))
//                        dispatch(LessonReportStore.Message.isFABShowing(true))
                    nInterface.nError("Что-то пошло не так", e) {
                        nInterface.goToNone()
                    }
                }
            }
        }
    }

    private fun openDetailedMarks(login: String) {
        scope.launch {
            try {
                dispatch(LessonReportStore.Message.DetailedMarksOpened(login))
                marksDialogComponent.nInterface.nStartLoading()
                marksDialogComponent.onEvent(CAlertDialogStore.Intent.ShowDialog)
                val marks = journalRepository.fetchSubjectQuarterMarks(
                    RFetchSubjectQuarterMarksReceive(
                        login = login,
                        subjectId = state().subjectId,
                        quartersNum = state().module.toString(),
                        edYear = state().edYear
                    )
                ).marks.sortedBy { getLocalDate(it.date).toEpochDays() }.reversed()
                if (login == state().detailedMarksLogin) {
                    dispatch(LessonReportStore.Message.DetailedMarksFetched(marks))
                    marksDialogComponent.nInterface.nSuccess()
                }
            } catch (e: Throwable) {
                marksDialogComponent.nInterface.nError("Что-то пошло не так", e) {
                    //TODO
                    openDetailedMarks(login)
                }
            }
        }
    }

}


val customOrder = mapOf(
    ColumnTypes.prisut to 0,
    "!dz1" to 1,
    "!dz2" to 2,
    "!dz3" to 3,
    "!dz4" to 4,
    "!cl1" to 5,
    "!cl2" to 6,
    "!cl3" to 7,
    "!cl4" to 8,
    "!cl5" to 9,
    ColumnTypes.srBall to 10,
    "!st1" to 11,
    "!st2" to 12,
    "!st3" to 13,
    "!st4" to 14,
    "!st5" to 15,
    ColumnTypes.opozdanie to 16,
    "!ds1" to 17,
    "!ds2" to 18,
    "!ds3" to 19
)




