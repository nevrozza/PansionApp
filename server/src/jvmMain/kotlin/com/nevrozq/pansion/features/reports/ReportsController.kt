package com.nevrozq.pansion.features.reports

import ForAvg
import ReportData
import com.nevrozq.pansion.database.achievements.Achievements
import com.nevrozq.pansion.database.calendar.Calendar
import com.nevrozq.pansion.database.calendar.CalendarDTO
import com.nevrozq.pansion.database.forms.Forms
import com.nevrozq.pansion.database.groups.Groups
import com.nevrozq.pansion.database.homework.HomeTasks
import com.nevrozq.pansion.database.homework.HomeTasksDTO
import com.nevrozq.pansion.database.preAttendance.PreAttendance
import com.nevrozq.pansion.database.ratingEntities.Marks
import com.nevrozq.pansion.database.ratingEntities.Stups
import com.nevrozq.pansion.database.ratingEntities.mapToServerRatingUnit
import com.nevrozq.pansion.database.ratingTable.getModuleDays
import com.nevrozq.pansion.database.reportHeaders.ReportHeaders
import com.nevrozq.pansion.database.reportHeaders.ReportHeadersDTO
import com.nevrozq.pansion.database.schedule.Schedule
import com.nevrozq.pansion.database.studentGroups.StudentGroups
import com.nevrozq.pansion.database.studentLines.StudentLines
import com.nevrozq.pansion.database.studentsInForm.StudentsInForm
import com.nevrozq.pansion.database.subjects.Subjects
import com.nevrozq.pansion.database.users.Users
import com.nevrozq.pansion.utils.*
import homework.ClientReportHomeworkItem
import homework.CreateReportHomeworkItem
import homework.RFetchGroupHomeTasksReceive
import homework.RFetchGroupHomeTasksResponse
import homework.RFetchReportHomeTasksReceive
import homework.RFetchReportHomeTasksResponse
import homework.RSaveReportHomeTasksReceive
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.*
import main.Period
import main.RChangeToUv
import main.RFetchMainAVGReceive
import main.RFetchMainAVGResponse
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import report.AddStudentLine
import report.AllGroupMarksStudent
import report.Attended
import report.DetailedStupsSubject
import report.DnevnikRuMarksSubject
import report.Grade
import report.RCreateReportReceive
import report.RCreateReportResponse
import report.RFetchAllGroupMarksReceive
import report.RFetchAllGroupMarksResponse
import report.RFetchDetailedStupsReceive
import report.RFetchDetailedStupsResponse
import report.RFetchDnevnikRuMarksReceive
import report.RFetchDnevnikRuMarksResponse
import report.RFetchFullReportData
import report.RFetchHeadersResponse
import report.RFetchRecentGradesReceive
import report.RFetchRecentGradesResponse
import report.RFetchReportDataReceive
import report.RFetchReportDataResponse
import report.RFetchReportStudentsReceive
import report.RFetchReportStudentsResponse
import report.RFetchStudentLinesReceive
import report.RFetchStudentLinesResponse
import report.RFetchStudentReportReceive
import report.RFetchStudentReportResponse
import report.RFetchSubjectQuarterMarksReceive
import report.RFetchSubjectQuarterMarksResponse
import report.RIsQuartersReceive
import report.RIsQuartersResponse
import report.RUpdateReportReceive
import report.ReportHeader
import report.ServerStudentLine
import report.StudentNka
import report.StudentReportInfo
import report.UserMark
import report.UserMarkPlus
import server.getCurrentDate
import server.getDate
import server.getLocalDate
import server.getSixTime
import server.getWeekDays
import server.toMinutes

class ReportsController() {

    suspend fun updateReport(call: ApplicationCall) {
        val r = call.receive<RUpdateReportReceive>()
        val perm =
            (call.isTeacher && ReportHeaders.fetchHeader(r.lessonReportId).teacherLogin == call.login) || call.isModer
        call.dRes(perm, "Can't update report") {
            ReportHeaders.updateWholeReport(r)
            this.respond(HttpStatusCode.OK).done
        }
    }

    suspend fun fetchClientStudentLines(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch studentLines") {
            val r = this.receive<RFetchStudentLinesReceive>()

            val studentLines = StudentLines.fetchClientStudentLines(login = r.login)

            this.respond(
                RFetchStudentLinesResponse(
                    studentLines = studentLines
                )
            ).done
        }
    }

    suspend fun fetchStudentReport(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch studentReport") {
            val r = this.receive<RFetchStudentReportReceive>()

            val sl = StudentLines.fetchClientStudentLine(login = r.login, reportId = r.reportId)
            if (sl == null) {
                this.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch studentLine: null"
                ).done
            } else {
                val marks = Marks.fetchForUserReport(sLogin = r.login, reportId = r.reportId)
                val stups = Stups.fetchForUserReport(sLogin = r.login, reportId = r.reportId)

                val h = ReportHeaders.fetchHeader(reportId = r.reportId)

                val info = StudentReportInfo(
                    subjectName = h.subjectName,
                    groupName = h.groupName,
                    teacherName = h.teacherName,
                    date = h.date,
                    module = h.module,
                    time = h.time,
                    theme = h.topic,
                    reportId = h.id
                )

                val homeTasks =
                    HomeTasks.getAllHomeTasksByReportId(reportId = r.reportId).filter {
                        it.studentLogins == null || r.login in it.studentLogins
                    }

                this.respond(
                    RFetchStudentReportResponse(
                        studentLine = sl,
                        marks = marks.mapNotNull {
                            if (it.groupId != null && it.reportId != null) {
                                UserMarkPlus(
                                    mark = UserMark(
                                        id = it.id,
                                        content = it.content,
                                        reason = it.reason,
                                        isGoToAvg = it.isGoToAvg,
                                        groupId = it.groupId,
                                        date = it.date,
                                        reportId = it.reportId,
                                        module = it.part
                                    ),
                                    deployDate = it.deployDate,
                                    deployTime = it.deployTime,
                                    deployLogin = it.deployLogin
                                )
                            } else null
                        },
                        stups = stups.mapNotNull {
                            if (it.groupId != null && it.reportId != null) {
                                UserMarkPlus(
                                    mark = UserMark(
                                        id = it.id,
                                        content = it.content,
                                        reason = it.reason,
                                        isGoToAvg = it.isGoToAvg,
                                        groupId = it.groupId,
                                        date = it.date,
                                        reportId = it.reportId,
                                        module = it.part
                                    ),
                                    deployDate = it.deployDate,
                                    deployTime = it.deployTime,
                                    deployLogin = it.deployLogin
                                )
                            } else null
                        },
                        info = info,
                        homeTasks = homeTasks.map {
                            it.text
                        }
                    )
                ).done
            }
        }

    }

    suspend fun fetchFullReportData(call: ApplicationCall) {
        val perm = call.isTeacher || call.isMember
        call.dRes(perm, "Can't fetch full report") {
            val r = this.receive<RFetchFullReportData>()
            val l = ReportHeaders.fetchHeader(r.reportId)
            this.respond(
                ReportData(
                    header = ReportHeader(
                        reportId = l.id,
                        subjectName = l.subjectName,
                        subjectId = l.subjectId,
                        groupName = l.groupName,
                        groupId = l.groupId,
                        teacherName = l.teacherName,
                        teacherLogin = l.teacherLogin,
                        date = l.date,
                        module = l.module,
                        time = l.time,
                        status = l.status,
                        theme = l.topic
                    ),
//                    topic = l.topic,
                    description = l.description,
                    editTime = l.editTime,
                    ids = l.ids,
                    isMentorWas = l.isMentorWas,
                    isEditable = this.login == l.teacherLogin || this.isModer,
                    customColumns = l.customColumns
                )
            ).done
        }
    }

    suspend fun fetchReportData(call: ApplicationCall) {
        val r = call.receive<RFetchReportDataReceive>()
        val perm = call.isTeacher || call.isMember
        call.dRes(perm, "Can't fetch reportData") {
            val reportHeader = ReportHeaders.fetchHeader(r.reportId)
            val columns = reportHeader.customColumns.ifEmpty {
                HomeTasks.fetchPreviousHomeTasks(
                    reportId = reportHeader.id,
                    groupId = reportHeader.groupId
                )
            }

            val response = RFetchReportDataResponse(
                topic = reportHeader.topic,
                description = reportHeader.description,
                editTime = reportHeader.editTime,
                ids = reportHeader.ids,
                isMentorWas = reportHeader.isMentorWas,
                isEditable = call.login == Groups.getTeacherLogin(reportHeader.groupId),
                customColumns = columns
            )
            this.respond(response).done
        }
    }


    suspend fun changeToUv(call: ApplicationCall) {
        val perm = call.isMentor
        call.dRes(perm, "Can't change to uv") {

            val r = this.receive<RChangeToUv>()
            transaction {
                StudentLines.update({ (StudentLines.login eq r.login) and (StudentLines.reportId eq r.reportId) }) {
                    it[StudentLines.attended] = "2"
                }
                Stups.deleteWhere { (Stups.reportId eq r.reportId) and (Stups.login eq r.login) and (Stups.reason eq "!ds3") }
            }

            this.respond(HttpStatusCode.OK).done
        }
    }


    suspend fun createReport(call: ApplicationCall) {
        val perm = call.isTeacher

        call.dRes(perm, "Can't create report") {
            val r = this.receive<RCreateReportReceive>()
            val id = ReportHeaders.createReport(r, this.login)
            Schedule.markLesson(lessonId = r.lessonId, lessonDate = r.date)
            this.respond(RCreateReportResponse(id)).done
        }
    }

    suspend fun fetchMainAVG(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch mainAVG") {
            val r = this.receive<RFetchMainAVGReceive>()
            val module = (getModuleByDate(getDate())?.num ?: 1).toString()
            val avg: ForAvg
            val stStups: Int
            var achievementsStups: MutableMap<Period, Pair<Int, Int>>? = null

            if (r.isFirst) {
                achievementsStups = mutableMapOf(
                    Period.WEEK to Pair(0, 0),
                    Period.MODULE to Pair(0, 0),
                    Period.HALF_YEAR to Pair(0, 0),
                    Period.YEAR to Pair(0, 0)
                )
                val achievements = Achievements.fetchAllByLogin(login = r.login)
                val weekDays = getWeekDays()
                achievements.forEach { a ->
                    val date = (if (((a.showDate)?.length ?: 0) > 5) a.showDate
                        ?: a.date else a.date)
                    val epoch = getLocalDate(date).toEpochDays()
                    val pair = getModuleDays(module)
                    val start = getLocalDate(pair.first)
                    val end = if (pair.second != null) getLocalDate(pair.second!!) else null

                    val isMain = a.subjectId > -2//!in listOf(-4, -3, -2)


                    val i = achievementsStups[Period.YEAR]!!
                    achievementsStups[Period.YEAR] = i.copy(
                        first = if (isMain) i.first + a.stups else i.first,
                        second = if (isMain) i.second else i.second + a.stups
                    )

                    if (date in weekDays) { //WEEK
                        val i = achievementsStups[Period.WEEK]!!
                        achievementsStups[Period.WEEK] = i.copy(
                            first = if (isMain) i.first + a.stups else i.first,
                            second = if (isMain) i.second else i.second + a.stups
                        )

                    }
                    if (epoch >= start.toEpochDays() && (end == null || epoch < (end.toEpochDays()
                            ?: 0))
                    ) {
                        val i = achievementsStups[Period.MODULE]!!
                        achievementsStups[Period.MODULE] = i.copy(
                            first = if (isMain) i.first + a.stups else i.first,
                            second = if (isMain) i.second else i.second + a.stups
                        )
                    }

                    val half = Calendar.getHalfOfModule(module.toInt())

                    val modules = Calendar.getAllModulesOfHalf(half).sorted()
                    if (modules.isNotEmpty()) {
                        val firstModuleStartDate =
                            Calendar.getModuleStart(modules.first()) ?: "01.01.2000"
                        val lastModuleStartDate = Calendar.getModuleStart(modules.last() + 1)

                        if (epoch >= getLocalDate(firstModuleStartDate).toEpochDays() && (lastModuleStartDate == null || epoch < getLocalDate(
                                lastModuleStartDate
                            ).toEpochDays())
                        ) {
                            val i = achievementsStups[Period.HALF_YEAR]!!
                            achievementsStups[Period.HALF_YEAR] = i.copy(
                                first = if (isMain) i.first + a.stups else i.first,
                                second = if (isMain) i.second else i.second + a.stups
                            )
                        }
                    } else {
                        achievementsStups[Period.HALF_YEAR] =
                            achievementsStups[Period.YEAR] ?: Pair(0, 0)
                    }


                }
            }

            when (r.period) {
                "0" -> { //week
                    avg = Marks.fetchWeekAVG(r.login)
                    val stups = Stups.fetchForAWeek(r.login)

                    stStups = stups.filter { it.reason.subSequence(0, 3) == "!st" }
                        .map { it.content.toInt() }.sum()
                }

                "1" -> { //module

                    avg = Marks.fetchModuleAVG(r.login, module)
                    val stups = Stups.fetchForUserQuarters(
                        login = r.login,
                        quartersNum = module,
                        isQuarters = true
                    )
                    stStups = stups.filter { it.reason.subSequence(0, 3) == "!st" }
                        .map { it.content.toInt() }.sum()
                }

                "2" -> { //halfyear
                    avg = Marks.fetchHalfYearAVG(r.login, module)
                    val c = Calendar.getHalfOfModule(module.toInt())
                    val stups = Stups.fetchForUserQuarters(
                        login = r.login,
                        quartersNum = c.toString(),
                        isQuarters = false
                    )
                    stStups = stups.filter { it.reason.subSequence(0, 3) == "!st" }
                        .map { it.content.toInt() }.sum()
                }

                "3" -> {
                    avg = Marks.fetchYearAVG(r.login)
                    val stups = Stups.fetchForUser(
                        login = r.login
                    )
                    stStups = stups.filter { it.reason.subSequence(0, 3) == "!st" }
                        .map { it.content.toInt() }.sum()
                } //year
                else -> {
                    throw Throwable()
                }
            }
            call.respond(
                RFetchMainAVGResponse(
                    avg = avg.sum / avg.count.toFloat(),
                    stups = stStups,
                    achievementsStups = achievementsStups
                )
            ).done
        }
    }


    suspend fun fetchIsQuarter(call: ApplicationCall) {
        val r = call.receive<RIsQuartersReceive>()
        val perm = call.isMember
        call.dRes(perm, "Can't fetch isQuarter") {
            val isQuarter = isQuarter(r)
            val currentModule = getModuleByDate(getCurrentDate().second) ?: CalendarDTO(
                num = 1,
                start = "01.01.2000",
                halfNum = 1
            )
            this.respond(
                RIsQuartersResponse(
                    isQuarters = isQuarter,
                    num = if (isQuarter) Calendar.getAllModules().size else 2,
                    currentIndex = if (isQuarter) currentModule.num else currentModule.halfNum
                )
            ).done
        }
    }


    suspend fun fetchAllGroupMarks(call: ApplicationCall) {
        val r = call.receive<RFetchAllGroupMarksReceive>()
        val perm = call.isMember

        call.dRes(perm, "Can't fetch groupMarks") {

            val firstHalfNums = Calendar.getAllModulesOfHalf(1)


            val result = mutableListOf<AllGroupMarksStudent>()
            val students = StudentGroups.fetchStudentsOfGroup(r.groupId)
            students.filter { it.isActive }.forEach { s ->
                val isQuarter = isQuarter(RIsQuartersReceive(s.login))
                val marks = Marks.fetchForUserGroup(
                    login = s.login,
                    groupId = r.groupId
                ).sortedBy { it.deployTime.toMinutes() }.mapNotNull {
                    if (it.groupId != null && it.reportId != null) {
                        UserMarkPlus(
                            mark = UserMark(
                                id = it.id,
                                content = it.content,
                                reason = it.reason,
                                isGoToAvg = it.isGoToAvg,
                                groupId = it.groupId,
                                date = it.date,
                                reportId = it.reportId,
                                module = it.part
                            ),
                            deployDate = it.deployDate,
                            deployTime = it.deployTime,
                            deployLogin = it.deployLogin
                        )
                    } else null
                }
                val stups = Stups.fetchForUserGroup(
                    login = s.login,
                    groupId = r.groupId
                ).sortedWith(
                    compareBy(
                        { getLocalDate(it.deployDate).toEpochDays() },
                        { it.deployTime.toMinutes() })
                ).mapNotNull {
                    if (it.groupId != null && it.reportId != null) {
                        UserMarkPlus(
                            mark = UserMark(
                                id = it.id,
                                content = it.content,
                                reason = it.reason,
                                isGoToAvg = it.isGoToAvg,
                                groupId = it.groupId,
                                date = it.date,
                                reportId = it.reportId,
                                module = it.part
                            ),
                            deployDate = it.deployDate,
                            deployTime = it.deployTime,
                            deployLogin = it.deployLogin
                        )
                    } else null
                }
                val shortFio =
                    "${s.fio.surname} ${s.fio.name[0]}.${if (s.fio.praname != null) " " + s.fio.praname!![0] + "." else ""}"

                val nki = mutableListOf<StudentNka>()
                StudentLines.fetchStudentLinesByLoginAndGroup(
                    login = s.login,
                    groupId = r.groupId
                ).filter { it.attended != null && it.attended != "0" }.map { x ->
                    nki.add(StudentNka(x.date, isUv = x.attended == "2", module = x.module))
                }


                result.add(
                    AllGroupMarksStudent(
                        login = s.login,
                        shortFIO = shortFio,
                        marks = marks,
                        stups = stups,
                        isQuarters = isQuarter,
                        nki = nki
                    )
                )
            }
            this.respond(RFetchAllGroupMarksResponse(students = result, firstHalfNums)).done

        }
    }


    suspend fun fetchSubjectQuarterMarks(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch marks") {
            val r = this.receive<RFetchSubjectQuarterMarksReceive>()
            val isQuarter = isQuarter(RIsQuartersReceive(r.login))
            val half = Calendar.getHalfOfModule(r.quartersNum.toInt())
            var modules = ""
            Calendar.getAllModulesOfHalf(half).forEach {
                modules += it
            }
            val marks = Marks.fetchForUserSubjectQuarter(
                login = r.login,
                subjectId = r.subjectId,
                quartersNum = if (isQuarter) r.quartersNum else modules
            ).sortedWith(
                compareBy(
                    { getLocalDate(it.deployDate).toEpochDays() },
                    { it.deployTime.toMinutes() })
            ).mapNotNull {
                if (it.groupId != null && it.reportId != null) {
                    UserMark(
                        id = it.id,
                        content = it.content,
                        reason = it.reason,
                        isGoToAvg = it.isGoToAvg,
                        groupId = it.groupId,
                        date = it.date,
                        reportId = it.reportId,
                        module = it.part
                    )
                } else null
            }

            this.respond(
                RFetchSubjectQuarterMarksResponse(marks)
            ).done
        }
    }

    suspend fun fetchRecentGrades(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch recent grades") {
            val r = this.receive<RFetchRecentGradesReceive>()
            val subjects = Subjects.fetchAllSubjects()
            val limit = 7
            val stups = Stups.fetchForUser(r.login).filter {
                (it.reason.subSequence(0, 3) == "!st" ||
                        it.content.toInt() < 0)
            }
            val n = if (limit > stups.size) stups.size else limit
            val preGrades = (Marks.fetchRecentForUser(
                login = r.login,
                limit = limit
            ) + //Stups.fetchRecentForUser(r.login, 7))
                    stups.slice(0..n - 1)
                    )
            val grades = preGrades.sortedWith(
                compareBy(
                    { getLocalDate(it.deployDate).toEpochDays() },
                    { it.deployTime.toMinutes() })
            ).map { p ->
                Grade(
                    content = p.content,
                    reason = p.reason,
                    date = p.date,
                    reportId = p.reportId,
                    subjectName = if (p.subjectId != null) subjects.first { it.id == p.subjectId }.name else "Министерство"
                )
            }

            this.respond(
                RFetchRecentGradesResponse(grades)
            ).done
        }
    }

    suspend fun fetchDetailedStups(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch detailed stups") {
            val r = this.receive<RFetchDetailedStupsReceive>()
            val allSubjects = Subjects.fetchAllSubjects()
            val stups = Stups.fetchForUser(login = r.login)

            val responseList = mutableListOf<DetailedStupsSubject>()

            val subjects = StudentGroups.fetchSubjectsOfStudent(studentLogin = r.login)
            val stupSubjects = stups.mapNotNull { s ->
                if (s.subjectId != null) {
                    allSubjects.first { it.id == s.subjectId }
                } else null
            }

            val all = subjects.union(stupSubjects)

            all.forEach { s ->
                val iStups = stups.filter { it.subjectId == s.id }
                responseList.add(
                    DetailedStupsSubject(
                        subjectName = s.name,
                        stups = iStups.mapNotNull {
                            if (it.groupId != null && it.reportId != null) {
                                UserMark(
                                    id = it.id,
                                    content = it.content,
                                    reason = it.reason,
                                    isGoToAvg = it.isGoToAvg,
                                    groupId = it.groupId,
                                    date = it.date,
                                    reportId = it.reportId,
                                    module = it.part
                                )
                            } else null
                        }
                    )
                )
            }

            this.respond(
                RFetchDetailedStupsResponse(
                    responseList
                )
            ).done
        }
    }

    suspend fun fetchDnevnikRuMarks(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch dnevnikMarks") {
            val r = this.receive<RFetchDnevnikRuMarksReceive>()
            val allSubjects = Subjects.fetchAllSubjects()

            val marks = Marks.fetchForUserQuarters(
                login = r.login,
                quartersNum = r.quartersNum,
                isQuarters = r.isQuarters
            ).sortedWith(
                compareBy(
                    { getLocalDate(it.deployDate).toEpochDays() },
                    { it.deployTime.toMinutes() })
            )
            val stups = Stups.fetchForUserQuarters(
                login = r.login,
                quartersNum = r.quartersNum,
                isQuarters = r.isQuarters
            )

            val responseList = mutableListOf<DnevnikRuMarksSubject>()

            val subjects = StudentGroups.fetchSubjectsOfStudent(studentLogin = r.login)
            val markSubjects = marks.map { m ->
                allSubjects.first { it.id == m.subjectId }
            }
            val stupSubjects = stups.mapNotNull { s ->
                if (s.subjectId != null) {
                    allSubjects.first { it.id == s.subjectId }
                } else null
            }

            val groupIds = (marks + stups).filter { it.subjectId != null && it.groupId != null }
                .associate { it.subjectId!! to it.groupId!! }

            val all = subjects.union(markSubjects).union(stupSubjects)

            all.forEach { s ->
                val iMarks = marks.filter { it.subjectId == s.id }
                val iStups = stups.filter { it.subjectId == s.id }
                responseList.add(
                    DnevnikRuMarksSubject(
                        subjectId = s.id,
                        subjectName = s.name,
                        marks = iMarks.mapNotNull {
                            if (it.groupId != null && it.reportId != null) {
                                UserMark(
                                    id = it.id,
                                    content = it.content,
                                    reason = it.reason,
                                    isGoToAvg = it.isGoToAvg,
                                    groupId = it.groupId,
                                    date = it.date,
                                    reportId = it.reportId,
                                    module = it.part
                                )
                            } else null
                        },
                        stups = iStups.mapNotNull {
                            if (it.groupId != null && it.reportId != null) {
                                UserMark(
                                    id = it.id,
                                    content = it.content,
                                    reason = it.reason,
                                    isGoToAvg = it.isGoToAvg,
                                    groupId = it.groupId,
                                    date = it.date,
                                    reportId = it.reportId,
                                    module = it.part
                                )
                            } else null
                        },
                        nki = StudentLines.fetchStudentLinesByLoginAndGroup(
                            login = r.login,
                            groupId = groupIds[s.id] ?: 0
                        ).mapNotNull {
                            if (it.attended !in listOf(
                                    null,
                                    "0"
                                )
                            ) StudentNka(
                                date = it.date,
                                isUv = it.attended == "2",
                                module = it.module
                            ) else null
                        }
                    )
                )
            }

            this.respond(
                RFetchDnevnikRuMarksResponse(
                    responseList
                )
            ).done
        }
    }

    suspend fun fetchReportStudents(call: ApplicationCall) {
        val r = call.receive<RFetchReportStudentsReceive>()
        val perm = call.isTeacher || call.isModer || call.isMentor
        call.dRes(perm, "Can't fetch reportStudents") {
            val report = ReportHeaders.fetchHeader(r.reportId)
            val students = StudentLines.fetchStudentLinesOfReport(r.reportId)
            val marks = Marks.fetchForReport(r.reportId).sortedWith(
                compareBy(
                    { getLocalDate(it.deployDate).toEpochDays() },
                    { it.deployTime.toMinutes() })
            )
            val stups = Stups.fetchForReport(r.reportId).sortedBy { it.deployTime.toMinutes() }
            this.respond(
                RFetchReportStudentsResponse(
                    students = students.map {
                        val user = Users.fetchUser(it.login)!!
                        val shortFio =
                            "${user.surname} ${user.name}${if (user.praname != null) " " + user.praname[0] + "." else ""}"
                        val forAvg =
                            Marks.fetchModuleSubjectAVG(
                                it.login,
                                Groups.fetchSubjectIdOfGroup(it.groupId),
                                module = r.module.toString()
                            )
                        var preAttendance = PreAttendance.fetchPreAttendanceByDateAndLogin(
                            date = r.date,
                            login = it.login
                        )
                        preAttendance =
                            if (preAttendance != null && preAttendance.start.toMinutes() <= r.minutes && preAttendance.end.toMinutes() > r.minutes) preAttendance else null
//
                        AddStudentLine(
                            serverStudentLine = ServerStudentLine(
                                login = it.login,
                                lateTime = it.lateTime,
                                isLiked = it.isLiked,
                                attended = if (it.attended != null) Attended(
                                    attendedType = it.attended,
                                    reason = it.aReason
                                ) else if (preAttendance != null) Attended(
                                    attendedType = if (preAttendance.isGood) "2" else "1",
                                    reason = preAttendance.reason
                                ) else null
                            ),
                            shortFio = shortFio,
                            prevSum = forAvg.sum - marks.filter { m -> m.login == it.login }
                                .sumOf { it.content.toInt() },
                            prevCount = forAvg.count - marks.filter { m -> m.login == it.login }.size
                        )
                    },
                    marks = marks.mapToServerRatingUnit(),
                    stups = stups.mapToServerRatingUnit(),
                    newStatus = report.status,
                    newTopic = report.topic
                )
            ).done
        }
    }

    suspend fun fetchReportHomeTasks(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch report home tasks") {
            val r = this.receive<RFetchReportHomeTasksReceive>()
            val tasks = HomeTasks.getAllHomeTasksByReportId(r.reportId)
            this.respond(
                RFetchReportHomeTasksResponse(
                    tasks = tasks.map {
                        CreateReportHomeworkItem(
                            id = it.id,
                            isNew = false,
                            type = it.type,
                            text = it.text,
                            stups = it.stups,
                            fileIds = it.filesId,
                            studentLogins = it.studentLogins,
                            isNec = it.isNecessary
                        )
                    }
                )
            ).done
        }
    }

    suspend fun fetchGroupHomeTasks(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch group home tasks") {
            val r = this.receive<RFetchGroupHomeTasksReceive>()
            val tasks = HomeTasks.getAllHomeTasksByGroupId(groupId = r.groupId)

            this.respond(
                RFetchGroupHomeTasksResponse(
                    tasks = tasks.map {
                        ClientReportHomeworkItem(
                            id = it.id,
                            type = it.type,
                            text = it.text,
                            stups = it.stups,
                            fileIds = it.filesId,
                            studentLogins = it.studentLogins,
                            subjectId = it.subjectId,
                            groupId = it.groupId,
                            date = it.date,
                            time = it.time,
                            isNec = it.isNecessary
                        )
                    }
                )
            ).done
        }
    }

    suspend fun saveReportHomeTasks(call: ApplicationCall) {
        val r = call.receive<RSaveReportHomeTasksReceive>()
        val login = call.login
        val perm = call.isTeacher && login == ReportHeaders.fetchHeader(r.reportId).teacherLogin
        call.dRes(perm, "Can't save report home tasks") {
            r.tasks.filter { it.isNew }.forEach { t ->
                HomeTasks.insert(
                    HomeTasksDTO(
                        id = 0,
                        date = getCurrentDate().second,
                        time = getSixTime(),
                        type = t.type,
                        subjectId = r.subjectId,
                        groupId = r.groupId,
                        reportId = r.reportId,
                        studentLogins = t.studentLogins,
                        teacherLogin = login,
                        stups = t.stups,
                        text = t.text,
                        filesId = t.fileIds,
                        isNecessary = t.isNec
                    )
                )
            }
            r.tasks.filter { !it.isNew }.forEach { t ->
                if (t.text.isNotBlank()) {
                    transaction {
                        HomeTasks.update({ HomeTasks.id eq t.id }) {
                            it[text] = t.text
                            it[type] = t.type
                            it[stups] = t.stups
                            it[studentLogins] = t.studentLogins.toStr()
                            it[filesId] = t.fileIds?.map { it.toString() }.toStr()
                        }
                    }
                } else {
                    transaction {
                        HomeTasks.deleteWhere { HomeTasks.id eq t.id }
                    }
                }
            }
            val newTasks = HomeTasks.getAllHomeTasksByReportId(r.reportId)

            this.respond(
                RFetchReportHomeTasksResponse(
                    tasks = newTasks.map {
                        CreateReportHomeworkItem(
                            id = it.id,
                            isNew = false,
                            type = it.type,
                            text = it.text,
                            stups = it.stups,
                            fileIds = it.filesId,
                            studentLogins = it.studentLogins,
                            isNec = it.isNecessary
                        )
                    }
                )
            ).done
        }
    }


    suspend fun fetchReportHeaders(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch reportHeaders") {
            val headersNo = ReportHeaders.fetchReportHeaders()
                var headers = headersNo.map {
                    ReportHeader(
                        reportId = it.id,
                        subjectName = it.subjectName,
                        subjectId = it.subjectId,
                        groupName = it.groupName,
                        groupId = it.groupId,
                        teacherName = it.teacherName,
                        teacherLogin = it.teacherLogin,
                        date = it.date,
                        time = it.time,
                        status = it.status,
                        module = it.module,
                        theme = it.topic
//                            ids = it.ids,
//                            isMentorWas = it.isMentorWas
                    )
                }
                val groups: MutableList<Int> = mutableListOf()
                if (this.isTeacher) {
                    groups += Groups.getGroupsOfTeacher(this.login).map { it.id }
                }
                if (this.isMentor) {
                    val forms = Forms.fetchMentorForms(this.login)
                    val students = StudentsInForm.fetchStudentsLoginsByFormIds(forms.map { it.id })
                    groups += StudentGroups.fetchGroupIdsOfStudents(students.map { it })
                }
                if (this.isModer) {
                    groups += headersNo.map { it.groupId }
                }

                headers = headers.filter { it.groupId in groups.map { it } }

                this.respond(
                    RFetchHeadersResponse(
                        headers, currentModule = getModuleByDate(
                            getCurrentDate().second
                        )?.num.toString()
                    )
                ).done
        }
    }
}

fun isQuarter(r: RIsQuartersReceive): Boolean {
    val formId = StudentsInForm.fetchFormIdOfLogin(r.login)
    val form = Forms.fetchById(formId)
    val classNum = form.classNum
    return !(classNum == 11 || classNum == 10)
}