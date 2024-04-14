package com.nevrozq.pansion.features.reports

import com.nevrozq.pansion.database.forms.Forms
import com.nevrozq.pansion.database.groups.Groups
import com.nevrozq.pansion.database.ratingEntities.Marks
import com.nevrozq.pansion.database.ratingEntities.Stups
import com.nevrozq.pansion.database.ratingEntities.mapToServerRatingUnit
import com.nevrozq.pansion.database.reportHeaders.ReportHeaders
import com.nevrozq.pansion.database.studentGroups.StudentGroups
import com.nevrozq.pansion.database.studentLines.StudentLines
import com.nevrozq.pansion.database.studentsInForm.StudentsInForm
import com.nevrozq.pansion.database.subjects.Subjects
import com.nevrozq.pansion.database.users.Users
import com.nevrozq.pansion.utils.isMember
import com.nevrozq.pansion.utils.isModer
import com.nevrozq.pansion.utils.isTeacher
import com.nevrozq.pansion.utils.login
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import main.RFetchMainAVGReceive
import main.RFetchMainAVGResponse
import org.jetbrains.exposed.exceptions.ExposedSQLException
import report.AddStudentLine
import report.AllGroupMarksStudent
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
import report.RFetchHeadersResponse
import report.RFetchRecentGradesReceive
import report.RFetchRecentGradesResponse
import report.RFetchReportDataReceive
import report.RFetchReportDataResponse
import report.RFetchReportStudentsReceive
import report.RFetchReportStudentsResponse
import report.RFetchSubjectQuarterMarksReceive
import report.RFetchSubjectQuarterMarksResponse
import report.RIsQuartersReceive
import report.RIsQuartersResponse
import report.RUpdateReportReceive
import report.ReportHeader
import report.ServerStudentLine
import report.UserMark

class ReportsController() {

    suspend fun updateReport(call: ApplicationCall) {
        val r = call.receive<RUpdateReportReceive>()
        if (call.isTeacher) {
            try {
                ReportHeaders.updateWholeReport(r)

                call.respond(HttpStatusCode.OK)
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Conflict!")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't update report: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchReportData(call: ApplicationCall) {
        val r = call.receive<RFetchReportDataReceive>()
        if (call.isTeacher || call.isMember) {
            try {
                val reportHeader = ReportHeaders.fetchHeader(r.reportId)
                val response = RFetchReportDataResponse(
                    topic = reportHeader.topic,
                    description = reportHeader.description,
                    editTime = reportHeader.editTime,
                    ids = reportHeader.ids,
                    isMentorWas = reportHeader.isMentorWas,
                    isEditable = call.login == Groups.getTeacherLogin(reportHeader.groupId),
                    customColumns = reportHeader.customColumns
                )
                call.respond(response)
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Conflict!")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't create report: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }


    suspend fun createReport(call: ApplicationCall) {
        val r = call.receive<RCreateReportReceive>()
        if (call.isTeacher) {
            try {
                val id = ReportHeaders.createReport(r, call.login)

                call.respond(RCreateReportResponse(id))
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Conflict!")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't create report: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchMainAVG(call: ApplicationCall) {
        val r = call.receive<RFetchMainAVGReceive>()
        if (call.isMember) {
            try {
                when (r.period) {
                    "0" -> { //week
                        val avg = Marks.fetchWeekAVG(r.login)
                        val stups = Stups.fetchForAWeek(r.login)

                        val notDsStups = stups.filter { it.reason.subSequence(0, 3) != "!ds" }.map { it.content.toInt() }.sum()
                        val allStups = stups.map { it.content.toInt() }.sum()
                        call.respond(
                            RFetchMainAVGResponse(
                                avg = avg.sum / avg.count.toFloat(),
                                stups = Pair(notDsStups, (allStups-notDsStups) )
                            )
                        )
                    }

                    "1" -> {} //module
                    "2" -> {} //halfyear
                    "3" -> {} //year
                    else -> {
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }

            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Conflict!")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't isQuarter: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }



    suspend fun fetchIsQuarter(call: ApplicationCall) {
        val r = call.receive<RIsQuartersReceive>()
        if (call.isMember) {
            try {
                val isQuarter = isQuarter(r)
                call.respond(RIsQuartersResponse(isQuarter))
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Conflict!")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't isQuarter: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }


    suspend fun fetchAllGroupMarks(call: ApplicationCall) {
        val r = call.receive<RFetchAllGroupMarksReceive>()
        if (call.isMember) {
            try {
                val result = mutableListOf<AllGroupMarksStudent>()
                val students = StudentGroups.fetchStudentsOfGroup(r.groupId)
                students.forEach { s ->
                    val isQuarter = isQuarter(RIsQuartersReceive(s.login))
                    val marks = Marks.fetchForUserSubjectQuarter(
                        login = s.login,
                        subjectId = r.subjectId,
                        quartersNum = if (isQuarter) "4" else "34"
                    ).map {
                        UserMark(
                            id = it.id,
                            content = it.content,
                            reason = it.reason,
                            isGoToAvg = it.isGoToAvg,
                            groupId = it.groupId,
                            date = it.date
                        )
                    }
                    val stups = Stups.fetchForUserSubjectQuarter(
                        login = s.login,
                        subjectId = r.subjectId,
                        quartersNum = if (isQuarter) "4" else "34"
                    ).map {
                        UserMark(
                            id = it.id,
                            content = it.content,
                            reason = it.reason,
                            isGoToAvg = it.isGoToAvg,
                            groupId = it.groupId,
                            date = it.date
                        )
                    }

                    val shortFio =
                        "${s.fio.surname} ${s.fio.name[0]}.${if (s.fio.praname != null) " " + s.fio.praname!![0] + "." else ""}"

                    result.add(AllGroupMarksStudent(login = s.login, shortFIO = shortFio, marks = marks, stups = stups))
                }
                call.respond(RFetchAllGroupMarksResponse(result))

            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Conflict!")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch students for a grouppp: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }


    suspend fun fetchSubjectQuarterMarks(call: ApplicationCall) {
        val r = call.receive<RFetchSubjectQuarterMarksReceive>()
        if (call.isMember) {
            try {
                val isQuarter = isQuarter(RIsQuartersReceive(r.login))
                val marks = Marks.fetchForUserSubjectQuarter(
                    login = r.login,
                    subjectId = r.subjectId,
                    quartersNum = if (isQuarter) "4" else "34"
                ).map {
                    UserMark(
                        id = it.id,
                        content = it.content,
                        reason = it.reason,
                        isGoToAvg = it.isGoToAvg,
                        groupId = it.groupId,
                        date = it.date
                    )
                }

                call.respond(
                    RFetchSubjectQuarterMarksResponse(marks)
                )
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Conflict!")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch marks: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchRecentGrades(call: ApplicationCall) {
        val r = call.receive<RFetchRecentGradesReceive>()
        if (call.isMember) {
            try {
                val subjects = Subjects.fetchAllSubjects()
                val preGrades = (Marks.fetchRecentForUser(
                    login = r.login,
                    limit = 7
                ) + Stups.fetchRecentForUser(r.login, 7))
                val grades = preGrades.map { p ->
                    Grade(
                        content = p.content,
                        reason = p.reason,
                        date = p.date,
                        reportId = p.reportId,
                        subjectName = subjects.first { it.id ==  p.subjectId}.name
                    )
                }
                println("grades: $grades")

                call.respond(
                    RFetchRecentGradesResponse(grades)
                )
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Conflict!")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch grades: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchDetailedStups(call: ApplicationCall) {
        val r = call.receive<RFetchDetailedStupsReceive>()
        if (call.isMember) {
            try {
                val allSubjects = Subjects.fetchAllSubjects()
                val stups = Stups.fetchForAWeek(login = r.login)

                val responseList = mutableListOf<DetailedStupsSubject>()

                val subjects = StudentGroups.fetchSubjectsOfStudent(studentLogin = r.login)
                val stupSubjects = stups.map { s ->
                    allSubjects.first { it.id == s.subjectId }
                }

                val all = subjects.union(stupSubjects)

                all.forEach { s ->
                    val iStups = stups.filter { it.subjectId == s.id }
                    responseList.add(
                        DetailedStupsSubject(
                            subjectName = s.name,
                            stups = iStups.map {
                                UserMark(
                                    id = it.id,
                                    content = it.content,
                                    reason = it.reason,
                                    isGoToAvg = it.isGoToAvg,
                                    groupId = it.groupId,
                                    date = it.date
                                )
                            }
                        )
                    )
                }

                call.respond(
                    RFetchDetailedStupsResponse(
                        responseList
                    )
                )
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Conflict!")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch detailed stups: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchDnevnikRuMarks(call: ApplicationCall) {
        val r = call.receive<RFetchDnevnikRuMarksReceive>()
        if (call.isMember) {
            try {
                val allSubjects = Subjects.fetchAllSubjects()

                val marks = Marks.fetchForUserQuarters(login = r.login, quartersNum = r.quartersNum)
                val stups = Stups.fetchForUserQuarters(login = r.login, quartersNum = r.quartersNum).filter { it.reason.subSequence(0, 3) != "!ds" }

                val responseList = mutableListOf<DnevnikRuMarksSubject>()

                val subjects = StudentGroups.fetchSubjectsOfStudent(studentLogin = r.login)
                val markSubjects = marks.map { m ->
                    allSubjects.first { it.id == m.subjectId }
                }
                val stupSubjects = stups.map { s ->
                    allSubjects.first { it.id == s.subjectId }
                }

                val all = subjects.union(markSubjects).union(stupSubjects)

                all.forEach { s ->
                    val iMarks = marks.filter { it.subjectId == s.id }
                    val iStups = stups.filter { it.subjectId == s.id }
                    responseList.add(
                        DnevnikRuMarksSubject(
                            subjectId = s.id,
                            subjectName = s.name,
                            marks = iMarks.map {
                                UserMark(
                                    id = it.id,
                                    content = it.content,
                                    reason = it.reason,
                                    isGoToAvg = it.isGoToAvg,
                                    groupId = it.groupId,
                                    date = it.date
                                )
                            },
                            stupCount = iStups.sumOf { it.content.toInt() }
                        )
                    )
                }

                call.respond(
                    RFetchDnevnikRuMarksResponse(
                        responseList
                    )
                )
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Conflict!")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch marks: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchReportStudents(call: ApplicationCall) {
        val r = call.receive<RFetchReportStudentsReceive>()
        if (call.isTeacher || call.isModer) {
            try {
                val students = StudentLines.fetchStudentLinesOfReport(r.reportId)
                val marks = Marks.fetchForReport(r.reportId)
                val stups = Stups.fetchForReport(r.reportId)
                call.respond(
                    RFetchReportStudentsResponse(
                        students = students.map {
                            val user = Users.fetchUser(it.login)!!
                            val shortFio =
                                "${user.surname} ${user.name[0]}.${if (user.praname != null) " " + user.praname[0] + "." else ""}"
                            val forAvg =
                                Marks.fetchAVG(it.login, Groups.fetchSubjectIdOfGroup(it.groupId))
                            AddStudentLine(
                                serverStudentLine = ServerStudentLine(
                                    login = it.login,
                                    lateTime = it.lateTime,
                                    isLiked = it.isLiked
                                ),
                                shortFio = shortFio,
                                prevSum = forAvg.sum,
                                prevCount = forAvg.count
                            )
                        },
                        marks = marks.mapToServerRatingUnit(),
                        stups = stups.mapToServerRatingUnit()
                    )
                )
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Conflict!")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch report students: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }


    suspend fun fetchReportHeaders(call: ApplicationCall) {
        if (call.isMember) {
            try {
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
//                            ids = it.ids,
//                            isMentorWas = it.isMentorWas
                    )
                }
                if(call.isTeacher) {
                    val groups = Groups.getGroupsOfTeacher(call.login)
                    headers = headers.filter { it.groupId in groups.map { it.id } }
                }

                call.respond(RFetchHeadersResponse(headers))
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Conflict!")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch reportHeaders: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }
}

fun isQuarter(r: RIsQuartersReceive): Boolean {
    val formId = StudentsInForm.fetchFormIdOfLogin(r.login)
    val form = Forms.fetchById(formId)
    val classNum = form.classNum
    return !(classNum == 11 || classNum == 10)
}