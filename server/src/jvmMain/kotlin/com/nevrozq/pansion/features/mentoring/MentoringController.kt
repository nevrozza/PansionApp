package com.nevrozq.pansion.features.mentoring

import FIO
import MentorPerson
import com.nevrozq.pansion.database.forms.Forms
import com.nevrozq.pansion.database.preAttendance.PreAttendance
import com.nevrozq.pansion.database.schedule.Schedule
import com.nevrozq.pansion.database.studentGroups.StudentGroups
import com.nevrozq.pansion.database.studentsInForm.StudentsInForm
import com.nevrozq.pansion.database.subjects.Subjects
import com.nevrozq.pansion.database.users.Users
import com.nevrozq.pansion.utils.isMentor
import com.nevrozq.pansion.utils.login
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import journal.init.RFetchMentorGroupIdsResponse
import mentoring.RFetchMentoringStudentsResponse
import mentoring.preAttendance.ClientPreAttendance
import mentoring.preAttendance.RFetchPreAttendanceDayReceive
import mentoring.preAttendance.RFetchPreAttendanceDayResponse
import mentoring.preAttendance.RSavePreAttendanceDayReceive
import mentoring.preAttendance.ScheduleForAttendance

class MentoringController {

    suspend fun fetchMentorGroupIds(call: ApplicationCall) {
        if (call.isMentor) {
            try {
                val forms = Forms.fetchMentorForms(call.login)
                val students = StudentsInForm.fetchStudentsLoginsByFormIds(forms.map { it.id })
                val groups = StudentGroups.fetchGroupIdsOfStudents(students.map { it.login })
                call.respond(
                    RFetchMentorGroupIdsResponse(groups)
                )
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch mentor group ids: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun savePreAttendanceDay(call: ApplicationCall) {
        if (call.isMentor) {
            try {
                val r = call.receive<RSavePreAttendanceDayReceive>()
                PreAttendance.savePreAttendance(date = r.date, login = r.studentLogin, preAttendance = r.preAttendance)
                call.respond(HttpStatusCode.OK)
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't save preAttendance: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchPreAttendanceDay(call: ApplicationCall) {
        if (call.isMentor) {
            try {
                val r = call.receive<RFetchPreAttendanceDayReceive>()
                val subjects = Subjects.fetchAllSubjectsAsMap()
                val groups = StudentGroups.fetchGroupsOfStudent(r.studentLogin)
                val ids = groups.mapNotNull { if (it.isActive) it.id else null }
                val schedule = Schedule.getOnDate(r.date).mapNotNull { s ->
                    if (s.groupId in ids) {
                        val group = groups.first { it.id == s.groupId }
                        if (group.isActive) {
                            ScheduleForAttendance(
                                groupId = s.groupId,
                                subjectName = subjects[group.subjectId].toString(),
                                groupName = group.name,
                                start = s.t.start,
                                end = s.t.end
                            )
                        } else null
                    } else null
                }
                val preAttendance = PreAttendance.fetchPreAttendanceByDateAndLogin(
                    date = r.date,
                    login = r.studentLogin
                )

                call.respond(
                    RFetchPreAttendanceDayResponse(
                        schedule = schedule,
                        attendance = if (preAttendance != null) ClientPreAttendance(
                            start = preAttendance.start,
                            end = preAttendance.end,
                            reason = preAttendance.reason,
                            isGood = preAttendance.isGood
                        ) else null
                    )
                )

            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch preAttendance: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchStudents(call: ApplicationCall) {
        if (call.isMentor) {
            try {
                val forms = Forms.fetchMentorForms(call.login)
                val studentLogins = StudentsInForm.fetchStudentsLoginsByFormIds(forms.map { it.id })
                val students = Users.fetchByLoginsActivated(logins = studentLogins.map { it.login })


                call.respond(
                    RFetchMentoringStudentsResponse(
                        forms = forms,
                        students = students.map { s ->
                            MentorPerson(
                                login = s.login,
                                fio = FIO(
                                    name = s.name,
                                    surname = s.surname,
                                    praname = s.praname
                                ),
                                avatarId = s.avatarId,
                                formId = studentLogins.first { it.login == s.login }.formId
                            )
                        },
                    )
                )
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch students (mentor): ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

//    suspend fun checkHomeTask(call: ApplicationCall) {
//        if (call.isMember) {
//            try {
//                val r = call.receive<RCheckHomeTaskReceive>()
//                HomeTasksDone.checkTask(
//                    login = r.login,
//                    homeWorkId = r.homeWorkId,
//                    isDone = r.isCheck,
//                    id = r.id
//                )
//            } catch (e: Throwable) {
//                call.respond(
//                    HttpStatusCode.BadRequest,
//                    "Can't check Task: ${e.localizedMessage}"
//                )
//            }
//        } else {
//            call.respond(HttpStatusCode.Forbidden, "No permission")
//        }
//    }
//
//    suspend fun fetchHomeTasks(call: ApplicationCall) {
//        if (call.isMember) {
//            try {
//
//                val r = call.receive<RFetchHomeTasksReceive>()
//                val groupIDS = StudentGroups.fetchGroupOfStudentIDS(r.login) //
//                val homeTasks = HomeTasks.getClientHomeTasks(
//                    groupIds = groupIDS,
//                    login = r.login,
//                    date = r.date
//                )
//
//                call.respond(
//                    RFetchHomeTasksResponse(
//                        tasks = homeTasks
//                    )
//                )
//            } catch (e: Throwable) {
//                call.respond(
//                    HttpStatusCode.BadRequest,
//                    "Can't fetch homeTasks: ${e.localizedMessage}"
//                )
//            }
//        } else {
//            call.respond(HttpStatusCode.Forbidden, "No permission")
//        }
//    }
//
//    suspend fun fetchHomeTasksInit(call: ApplicationCall) {
//        if(call.isMember) {
//            try {
//                val r = call.receive<RFetchTasksInitReceive>()
//                val groups = StudentGroups.fetchGroupsOfStudent(r.login)
//                val subjects = Subjects.fetchAllSubjectsAsMap().filter { it.key in groups.map { g -> g.subjectId } }
//                val schedule = Schedule.getOnNext(getDate(), getSixTime())
//                    .sortedBy { getLocalDate(it.date).toEpochDays() + (it.start.toMinutes() / 1000f) }
//                val cutedDateTimeGroups = groups.map { g ->
//                    val lesson = schedule.firstOrNull { it.groupId == g.id }
//                    CutedDateTimeGroup(
//                        id = g.id,
//                        name = g.name,
//                        localDateTime = if (lesson != null) getLocalDateTime(
//                            date = lesson.date,
//                            time = lesson.start
//                        ) else null
//                    )
//                }
//                call.respond(
//                    RFetchTasksInitResponse(
//                        groups = cutedDateTimeGroups,
//                        subjects = subjects,
//                        dates = HomeTasks.getHomeTasksDateForGroupsLogin(
//                            groups.map { it.id },
//                            r.login
//                        )
//                    )
//                )
//
//            } catch (e: Throwable) {
//                call.respond(
//                    HttpStatusCode.BadRequest,
//                    "Can't fetch homeTasksInit: ${e.localizedMessage}"
//                )
//            }
//        } else {
//            call.respond(HttpStatusCode.Forbidden, "No permission")
//        }
//    }
}