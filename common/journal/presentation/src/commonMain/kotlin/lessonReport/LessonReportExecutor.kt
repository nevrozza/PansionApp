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
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import report.RFetchReportStudentsReceive
import report.RUpdateReportReceive
import report.ReportHeader
import report.ServerRatingUnit
import report.ServerStudentLine
import server.getDate
import server.getLocalDate
import server.getSixTime
import server.toSixTime

class LessonReportExecutor(
    private val setMarkMenuComponent: ListComponent,
    private val deleteMarkMenuComponent: ListComponent,
    private val setLateTimeMenuComponent: ListComponent,
    private val nInterface: NetworkInterface,
    private val journalRepository: JournalRepository,
    private val authRepository: AuthRepository,
    private val marksDialogComponent: CAlertDialogComponent,
    private val header: ReportHeader,
) :
    CoroutineExecutor<LessonReportStore.Intent, Unit, LessonReportStore.State, LessonReportStore.Message, LessonReportStore.Label>() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun executeIntent(intent: LessonReportStore.Intent) {
        when (intent) {
            is LessonReportStore.Intent.CreateColumn -> {
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

            is LessonReportStore.Intent.DeleteColumnInit -> {
                dispatch(
                    LessonReportStore.Message.DeleteColumnInited(
                        intent.reportColumn
                    )
                )
            }

            is LessonReportStore.Intent.DeleteColumn -> {
                println("yes")
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

            LessonReportStore.Intent.ClearSelection -> dispatch(LessonReportStore.Message.SelectionCleared)
            is LessonReportStore.Intent.OpenSetMarksMenu -> {
                dispatch(
                    LessonReportStore.Message.MarksMenuOpened(
                        reasonId = intent.reasonId,
                        studentLogin = intent.studentLogin,
                        markValue = ""
                    )
                )
                setMarkMenuComponent.onEvent(
                    ListDialogStore.Intent.ShowDialog
                )
            }

            is LessonReportStore.Intent.OpenDeleteMarkMenu -> {
                dispatch(
                    LessonReportStore.Message.MarksMenuOpened(
                        reasonId = intent.reasonId,
                        studentLogin = intent.studentLogin,
                        markValue = intent.markValue.toString()
                    )
                )
                deleteMarkMenuComponent.onEvent(
                    ListDialogStore.Intent.ShowDialog
                )
            }

            is LessonReportStore.Intent.SetMark -> {
                val newList = state().students.toMutableList()
                val line = newList.first { it.login == state().selectedLogin }
                newList.remove(line)
                val newMarksList = line.marksOfCurrentLesson.toMutableList()
                newMarksList.add(
                    Mark(
                        intent.mark.toInt(),
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

            LessonReportStore.Intent.DeleteMark -> {
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

            is LessonReportStore.Intent.ChangeStups -> {

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
                        deployDate = getDate()
                    )
                )

                newList.add(
                    line.copy(
                        stupsOfCurrentLesson = newStupsList
                    )
                )

                dispatch(LessonReportStore.Message.StudentsUpdated(newList))

            }

            is LessonReportStore.Intent.ChangeSettingsTab -> dispatch(
                LessonReportStore.Message.SettingsTabChanged(
                    intent.settingsTab
                )
            )

            is LessonReportStore.Intent.ChangeTopic -> dispatch(
                LessonReportStore.Message.TopicChanged(
                    intent.topic
                )
            )

            is LessonReportStore.Intent.ChangeDescription -> dispatch(
                LessonReportStore.Message.DescriptionChanged(
                    intent.description
                )
            )

            is LessonReportStore.Intent.DislikeStudent -> {
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

            is LessonReportStore.Intent.LikeStudent -> {
                val newLikedList = state().likedList.toMutableList()
                val newDislikedList = state().dislikedList.toMutableList()
                newDislikedList.remove(intent.studentLogin)
                if (intent.studentLogin in newLikedList) {
                    newLikedList.remove(intent.studentLogin)
                } else {
                    newLikedList.add(intent.studentLogin)
                }

                println(newLikedList)
                dispatch(
                    LessonReportStore.Message.RepUpdated(
                        dislikedList = newDislikedList,
                        likedList = newLikedList
                    )
                )
            }

            is LessonReportStore.Intent.SetLateTime -> {
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
                println(state().students)
                println(intent.studentLogin)
                val student = state().students.first { it.login == intent.studentLogin }
                newStudentList.remove(student)
                newStudentList.add(student.copy(lateTime = "$result${if (intent.chosenTime != "0") " мин" else ""}"))

                dispatch(LessonReportStore.Message.StudentsUpdated(newStudentList))
            }


            is LessonReportStore.Intent.OpenSetLateTimeMenu -> {


                dispatch(
                    LessonReportStore.Message.LateTimeMenuOpened(
                        studentLogin = intent.studentLogin
                    )
                )
                setLateTimeMenuComponent.onEvent(
                    ListDialogStore.Intent.ShowDialog
                )
            }

            LessonReportStore.Intent.ChangeInfoShowing -> dispatch(LessonReportStore.Message.InfoShowingChanged)
            LessonReportStore.Intent.UpdateWholeReport -> {
                GlobalScope.launch (CDispatcher) {
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
                                        deployLogin = m.deployLogin
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
                                        deployDate = m.deployDate
                                    )
                                )
                            }
                        }
                        val editTime = getSixTime()

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
                                )
                            },
                            columnNames = state().columnNames.filter { it.type != ColumnTypes.prisut && it.type != ColumnTypes.opozdanie && it.type != ColumnTypes.srBall }
                                .map { it.type },
                            status = state().status,
                            ids = state().ids,
                            editTime = editTime,
                            isMentorWas = state().isMentorWas,
                            marks = marks,
                            stups = stups
                        )

                        journalRepository.updateWholeReport(r)
                        scope.launch {
                            dispatch(LessonReportStore.Message.EditTimeChanged(editTime))
                            dispatch(LessonReportStore.Message.IsSavedAnimation(true))
                            nInterface.nSuccess()
                        }
                    } catch (_: Throwable) {
//                        dispatch(LessonReportStore.Message.isFABShowing(true))
                        nInterface.nError("Что-то пошло не так") {
                            //TODO
                            nInterface.goToNone()
                        }
                    }
                }
            }

            LessonReportStore.Intent.ChangeIsMentorWas -> dispatch(LessonReportStore.Message.IsMentorWasChanged)
            is LessonReportStore.Intent.ChangeStatus -> dispatch(
                LessonReportStore.Message.StatusChanged(
                    intent.status
                )
            )

            is LessonReportStore.Intent.Init -> {
                scope.launch {

                    nInterface.nStartLoading()
                    try {
                        val studentsData = journalRepository.fetchReportStudents(
                            RFetchReportStudentsReceive(state().lessonReportId, header.module.toInt())
                        )

                        val students = mutableListOf<StudentLine>()
                        val likedList = mutableListOf<String>()
                        val dislikedList = mutableListOf<String>()


                        studentsData.students.forEach { student ->
                            students.add(
                                StudentLine(
                                    shortFio = student.shortFio,
                                    login = student.serverStudentLine.login,
                                    attended = Attented(true),
                                    lateTime = student.serverStudentLine.lateTime,
                                    avgMark = AvgMark(
                                        previousSum = student.prevSum,
                                        countOfMarks = student.prevCount,
//                                        value = student.prevSum / (student.prevCount.toFloat())
                                    ),
                                    marksOfCurrentLesson = studentsData.marks.filter { it.login == student.serverStudentLine.login }
                                        .map {
                                            Mark(
                                                value = it.content.toInt(),
                                                reason = it.reason,
                                                isGoToAvg = it.isGoToAvg,
                                                id = it.id,
                                                date = "sad",
                                                deployTime = it.deployTime,
                                                deployDate = it.deployDate,
                                                deployLogin = it.deployLogin
                                            )
                                        },
                                    stupsOfCurrentLesson = studentsData.stups.filter { it.login == student.serverStudentLine.login }
                                        .map {
                                            Stup(
                                                value = it.content.toInt(),
                                                reason = it.reason,
                                                id = it.id,
                                                deployTime = it.deployTime,
                                                deployDate = it.deployDate,
                                                deployLogin = it.deployLogin
                                            )
                                        }
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
                                dislikedList = dislikedList
                            )
                        )
                        nInterface.nSuccess()
                    } catch (_: Throwable) {
//                        dispatch(LessonReportStore.Message.isFABShowing(true))
                        nInterface.nError("Что-то пошло не так") {
                            //TODO
                            nInterface.goToNone()
                        }
                    }
                }
            }

            is LessonReportStore.Intent.OpenDetailedMarks -> openDetailedMarks(intent.studentLogin)
            is LessonReportStore.Intent.IsSavedAnimation -> dispatch(LessonReportStore.Message.IsSavedAnimation(intent.isSaved))
        }
    }

    private fun openDetailedMarks(login: String) {
        scope.launch {
            try {
                dispatch(LessonReportStore.Message.DetailedMarksOpened(login))
                marksDialogComponent.nInterface.nStartLoading()
                marksDialogComponent.onEvent(CAlertDialogStore.Intent.ShowDialog)
                val marks = journalRepository.fetchSubjectQuarterMarks(login, subjectId = state().subjectId, quartersNum = "").marks.sortedBy { getLocalDate(it.date).toEpochDays() }.reversed()
                if(login == state().detailedMarksLogin) {
                    dispatch(LessonReportStore.Message.DetailedMarksFetched(marks))
                    marksDialogComponent.nInterface.nSuccess()
                }
            } catch (_: Throwable) {
                marksDialogComponent.nInterface.nError("Что-то пошло не так") {
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


