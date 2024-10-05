package com.nevrozq.pansion.features.mentoring

import FIO
import MentorPerson
import admin.groups.Group
import admin.groups.GroupInit
import admin.groups.forms.CutedGroup
import admin.groups.forms.CutedGroupViaSubject
import com.nevrozq.pansion.database.deviceBinds.DeviceBinds
import com.nevrozq.pansion.database.forms.Forms
import com.nevrozq.pansion.database.groups.Groups
import com.nevrozq.pansion.database.parents.Parents
import com.nevrozq.pansion.database.parents.ParentsDTO
import com.nevrozq.pansion.database.preAttendance.PreAttendance
import com.nevrozq.pansion.database.ratingEntities.Marks
import com.nevrozq.pansion.database.ratingEntities.Stups
import com.nevrozq.pansion.database.schedule.Schedule
import com.nevrozq.pansion.database.studentGroups.StudentGroups
import com.nevrozq.pansion.database.studentLines.StudentLines
import com.nevrozq.pansion.database.studentsInForm.StudentInFormDTO
import com.nevrozq.pansion.database.studentsInForm.StudentsInForm
import com.nevrozq.pansion.database.subjects.Subjects
import com.nevrozq.pansion.database.users.UserDTO
import com.nevrozq.pansion.database.users.Users
import com.nevrozq.pansion.utils.createLogin
import com.nevrozq.pansion.utils.isMember
import com.nevrozq.pansion.utils.isMentor
import com.nevrozq.pansion.utils.isModer
import com.nevrozq.pansion.utils.login
import com.nevrozq.pansion.utils.toId
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationPlugin
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import journal.init.RFetchMentorGroupIdsResponse
import mentoring.MentorForms
import mentoring.RFetchJournalBySubjectsReceive
import mentoring.RFetchJournalBySubjectsResponse
import mentoring.RFetchMentoringStudentsResponse
import mentoring.preAttendance.ClientPreAttendance
import mentoring.preAttendance.RFetchPreAttendanceDayReceive
import mentoring.preAttendance.RFetchPreAttendanceDayResponse
import mentoring.preAttendance.RSavePreAttendanceDayReceive
import mentoring.preAttendance.ScheduleForAttendance
import org.jetbrains.exposed.sql.transactions.transaction
import registration.CloseRequestQRReceive
import registration.FetchLoginsReceive
import registration.FetchLoginsResponse
import registration.OpenRequestQRReceive
import registration.RegistrationRequest
import registration.ScanRequestQRReceive
import registration.ScanRequestQRResponse
import registration.SendRegistrationRequestReceive
import registration.SolveRequestReceive
import report.StudentNka
import report.UserMark
import report.UserMarkPlus
import server.Moderation
import server.Roles
import server.getLocalDate
import server.toMinutes
import java.util.UUID
import javax.management.relation.Role

val activeRegistrationForms = mutableListOf<Int>()
private val activeRegistrationRequests = mutableMapOf<
        UUID, RegistrationRequest
        >()

class MentoringController {

    suspend fun fetchJournalBySubjects(call: ApplicationCall) {
        val r = call.receive<RFetchJournalBySubjectsReceive>()
        try {
            val students = StudentsInForm.fetchStudentsLoginsByFormIds(r.forms).associateWith {
                StudentGroups.fetchGroupOfStudentIDS(it)
            }
            val groups = students.values.flatMap { it.map { it } }
            val subjects = Subjects.fetchAllSubjectsAsMap()

            var x = mutableListOf<Int>()
            groups.forEach {
                x.add(Groups.fetchSubjectIdOfGroup(it))
            }
            x = x.toSet().toMutableList()
            val ocenki = students.toList().associate { s ->
                s.first to x.flatMap { subjectId ->
                    (Marks.fetchForUserSubject(
                        login = s.first,
                        subjectId = subjectId
                    ) + Stups.fetchForUserSubject(
                        login = s.first,
                        subjectId = subjectId
                    )).map {
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
                            deployTime = it.deployTime,
                            deployDate = it.deployDate,
                            deployLogin = it.deployLogin
                        )
                    }
                        .sortedWith(
                            compareBy({ getLocalDate(it.deployDate).toEpochDays() },
                                { it.deployTime.toMinutes() })
                        )
                }
            }

            val nki = students.toList().associate { s ->
                s.first to s.second.flatMap { g ->
                    val sLines = StudentLines.fetchStudentLinesByLoginAndGroup(s.first, g)
                    sLines.mapNotNull {
                        if (it.attended !in listOf(
                                null,
                                "0"
                            )
                        ) StudentNka(date = it.date, isUv = it.attended == "2", groupId = g) else null
                    }
                }
            }

            call.respond(
                RFetchJournalBySubjectsResponse(
                    groups = Groups.getAllGroups().map {
                        CutedGroupViaSubject(
                            groupId = it.id,
                            groupName = it.name,
                            subjectId = it.subjectId
                        )
                    },
                    subjects = subjects.filter { it.key in x },
                    studentsGroups = students,
                    studentsMarks = ocenki,
                    studentsNki = nki
                )
            )

        } catch (e: Throwable) {
            call.respond(
                HttpStatusCode.BadRequest,
                "Can't fetch journal by subjects for mentors: ${e.localizedMessage}"
            )
        }
    }

    suspend fun fetchLogins(call: ApplicationCall) {
        val r = call.receive<FetchLoginsReceive>()
        try {
            call.respond(
                FetchLoginsResponse(
                    DeviceBinds.selectAll(r.deviceId.toId())
                )
            )
        } catch (e: Throwable) {
            call.respond(
                HttpStatusCode.BadRequest,
                "Can't fetch Logins: ${e.localizedMessage}"
            )
        }
    }

    suspend fun sendRegistrationRequest(call: ApplicationCall) {
        if (!call.isMember) {
            val r = call.receive<SendRegistrationRequestReceive>()
            try {
                activeRegistrationRequests[r.deviceId.toId()] = r.request
                call.respond(
                    HttpStatusCode.OK
                )
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't send request: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun scanRegistrationQR(call: ApplicationCall) {
        if (!call.isMember) {
            val r = call.receive<ScanRequestQRReceive>()
            try {
                if (r.formId in activeRegistrationForms) {
                    val f = Forms.fetchById(r.formId)
                    call.respond(
                        ScanRequestQRResponse(
                            formName = "${f.classNum} ${f.title}"
                        )
                    )
                } else {
                    throw Throwable()
                }
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't scan qr: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun solveRegistrationRequest(call: ApplicationCall) {
        if (call.isMentor) {
            val r = call.receive<SolveRequestReceive>()
            val parentFios = mutableListOf(r.request.fioFather, r.request.fioMother)
            try {
                val id =
                    activeRegistrationRequests.filterValues { it == r.request }.keys.first()
                if (r.isAccepted) {
                    transaction {


                        val login = createLogin(name = r.request.name, surname = r.request.surname)


                        Users.insert(
                            UserDTO(
                                login = login,
                                password = null,
                                name = r.request.name,
                                surname = r.request.surname,
                                praname = r.request.praname,
                                birthday = r.request.birthday,
                                role = Roles.student,
                                moderation = Moderation.nothing,
                                isParent = false,
                                avatarId = r.request.avatarId,
                                isActive = true
                            )
                        )


                        parentFios.filter { it.isNotBlank() }.forEach { p ->
                            val fio = p.split(" ")
                            val pLogin = createLogin(fio[1], fio[0])
                            Users.insert(
                                UserDTO(
                                    login = pLogin,
                                    password = null,
                                    name = fio[1],
                                    surname = fio[0],
                                    praname = fio.getOrNull(2),
                                    birthday = "01012000",
                                    role = Roles.nothing,
                                    moderation = Moderation.nothing,
                                    isParent = true,
                                    avatarId = 0,
                                    isActive = true
                                )
                            )

                            Parents.insert(
                                ParentsDTO(
                                    id = 0,
                                    studentLogin = login,
                                    parentLogin = pLogin
                                )
                            )
                            DeviceBinds.add(
                                id = id,
                                login = pLogin
                            )

                        }

                        DeviceBinds.add(
                            id = id,
                            login = login
                        )
                        StudentsInForm.insert(
                            StudentInFormDTO(
                                formId = r.request.formId,
                                login = login
                            )
                        )

                    }
                }
                activeRegistrationRequests.remove(id)
                call.respond(HttpStatusCode.OK)


            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't accept: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun openRegistrationQR(call: ApplicationCall) {
        if (call.isMentor) {

            try {
                val r = call.receive<OpenRequestQRReceive>()

                println("SERVERE: responded")
                activeRegistrationForms.add(r.formId)
                println("SERVERE: responded")
                call.respond(
                    HttpStatusCode.OK
                )
                println("SERVERE: responded")
            } catch (e: Throwable) {
                println("SERVERE: ${e}")
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't open qr: ${e.localizedMessage}"
                )
            }
        } else {
            println("sadixxx2")
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun closeRegistrationQR(call: ApplicationCall) {
        if (call.isMentor) {
            val r = call.receive<CloseRequestQRReceive>()
            try {
                activeRegistrationForms.remove(r.formId)
                call.respond(
                    HttpStatusCode.OK
                )
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't close qr: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchMentorGroupIds(call: ApplicationCall) {
        if (call.isMentor) {
            try {
                val forms = Forms.fetchMentorForms(call.login)
                val students = StudentsInForm.fetchStudentsLoginsByFormIds(forms.map { it.id })
                val groups = StudentGroups.fetchGroupIdsOfStudents(students.map { it })
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
                PreAttendance.savePreAttendance(
                    date = r.date,
                    login = r.studentLogin,
                    preAttendance = r.preAttendance
                )
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
        if (call.isMentor || call.isModer) {
            try {
                val forms = if (call.isModer) {
                    Forms.getAllForms().map {
                        MentorForms(
                            id = it.formId,
                            num = it.classNum,
                            title = it.title,
                            isQrActive = it.formId in activeRegistrationForms
                        )
                    }
                } else {
                    Forms.fetchMentorForms(call.login)
                }

                val studentLogins =
                    StudentsInForm.fetchStudentsLoginsAndIdsByFormIds(forms.map { it.id })
                val students =
                    Users.fetchByLoginsActivated(logins = studentLogins.map { it.login })
                val requests = activeRegistrationRequests.filter {
                    it.value.formId in forms.map { it.id }
                }

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
                        requests = requests.map { it.value }
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