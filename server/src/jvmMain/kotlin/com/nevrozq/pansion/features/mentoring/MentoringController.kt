package com.nevrozq.pansion.features.mentoring

import FIO
import MentorPerson
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
import com.nevrozq.pansion.features.lessons.fetchSchedule
import com.nevrozq.pansion.utils.*
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.*
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
import server.*
import java.util.UUID

val activeRegistrationForms = mutableListOf<Int>()
private val activeRegistrationRequests = mutableMapOf<
        UUID, RegistrationRequest
        >()

class MentoringController {

    suspend fun fetchJournalBySubjects(call: ApplicationCall) {
        call.dRes(true, "Can't fetch journal by subjects for mentors") {
            val r = this.receive<RFetchJournalBySubjectsReceive>()
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
                        subjectId = subjectId,
                        edYear = r.edYear
                    ) + Stups.fetchForUserSubject(
                        login = s.first,
                        subjectId = subjectId,
                        edYear = r.edYear
                    )).mapNotNull {
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
                                deployTime = it.deployTime,
                                deployDate = it.deployDate,
                                deployLogin = it.deployLogin
                            )
                        } else null
                    }
                        .sortedWith(
                            compareBy(
                                { getLocalDate(it.deployDate).toEpochDays() },
                                { it.deployTime.toMinutes() })
                        )
                }
            }

            val nki = students.toList().associate { s ->
                s.first to s.second.flatMap { g ->
                    val sLines = StudentLines.fetchStudentLinesByLoginAndGroup(s.first, g, edYear = r.edYear)
                    sLines.mapNotNull {
                        if (it.attended !in listOf(
                                null,
                                "0"
                            )
                        ) StudentNka(
                            date = it.date,
                            isUv = it.attended == "2",
                            groupId = g,
                            module = it.module
                        ) else null
                    }
                }
            }

            this.respond(
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
            ).done
        }
    }

    suspend fun fetchLogins(call: ApplicationCall) {
        call.dRes(true, "Can't fetch logins") {

            val r = this.receive<FetchLoginsReceive>()
            this.respond(
                FetchLoginsResponse(
                    DeviceBinds.selectAll(r.deviceId.toId())
                )
            ).done
        }
    }

    suspend fun sendRegistrationRequest(call: ApplicationCall) {
        val perm = !call.isMember
        call.dRes(perm, "Can't send request") {
            val r = this.receive<SendRegistrationRequestReceive>()
            activeRegistrationRequests[r.deviceId.toId()] = r.request
            this.respond(
                HttpStatusCode.OK
            ).done
        }
    }

    suspend fun scanRegistrationQR(call: ApplicationCall) {
        val perm = !call.isMember
        call.dRes(perm, "Can't scan qr") {
            val r = this.receive<ScanRequestQRReceive>()
            if (r.formId in activeRegistrationForms) {
                val f = Forms.fetchById(r.formId)
                this.respond(
                    ScanRequestQRResponse(
                        formName = "${f.classNum} ${f.title}"
                    )
                ).done
            } else {
                throw Throwable()
            }
        }
    }

    suspend fun solveRegistrationRequest(call: ApplicationCall) {
        val perm = call.isMentor
        call.dRes(perm, "Can't accept request") {

            val r = this.receive<SolveRequestReceive>()
            val parentFios = mutableListOf(r.request.fioFather, r.request.fioMother)
            val users = Users.fetchAll()

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
                            isActive = true,
                            subjectId = null
                        )
                    )


                    parentFios.filter { it.isNotBlank() }.forEach { p ->
                        val fio = p.split(" ")
                        val ff = FIO(
                            name = fio[1],
                            surname = fio[0],
                            praname = fio.getOrNull(2)
                        )
                        if (ff !in users.map { FIO(name = it.name, surname = it.surname, praname = it.praname) }) {

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
                                    isActive = true,
                                    subjectId = null
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
            this.respond(HttpStatusCode.OK).done

        }
    }

    suspend fun openRegistrationQR(call: ApplicationCall) {
        val perm = call.isMentor
        call.dRes(perm, "Can't open qr") {
            val r = this.receive<OpenRequestQRReceive>()

            activeRegistrationForms.add(r.formId)
            this.respond(
                HttpStatusCode.OK
            ).done
        }
    }

    suspend fun closeRegistrationQR(call: ApplicationCall) {
        val perm = call.isMentor
        call.dRes(perm, "Can't close qr") {
            val r = this.receive<CloseRequestQRReceive>()
            activeRegistrationForms.remove(r.formId)
            this.respond(
                HttpStatusCode.OK
            ).done
        }
    }

    suspend fun fetchMentorGroupIds(call: ApplicationCall) {
        val perm = call.isMentor
        call.dRes(perm, "Can't fetch mentor group ids") {
            val forms = Forms.fetchMentorForms(this.login)
            val students = StudentsInForm.fetchStudentsLoginsByFormIds(forms.map { it.id })
            val groups = StudentGroups.fetchGroupIdsOfStudents(students.map { it })
            this.respond(
                RFetchMentorGroupIdsResponse(groups)
            ).done
        }
    }

    suspend fun savePreAttendanceDay(call: ApplicationCall) {
        val perm = call.isMentor
        call.dRes(perm, "Can't save preAttendance") {
            val r = this.receive<RSavePreAttendanceDayReceive>()
            PreAttendance.savePreAttendance(
                date = r.date,
                login = r.studentLogin,
                preAttendance = r.preAttendance
            )
            this.respond(HttpStatusCode.OK).done
        }
    }

    suspend fun fetchPreAttendanceDay(call: ApplicationCall) {
        val perm = call.isMentor
        call.dRes(perm, "Can't fetch preAttendance") {
            val r = call.receive<RFetchPreAttendanceDayReceive>()
                val subjects = Subjects.fetchAllSubjectsAsMap()
                val groups = StudentGroups.fetchGroupsOfStudent(r.studentLogin)
                val ids = groups.mapNotNull { if (it.isActive) it.id else null }
                val schedule = fetchSchedule(
                    isTeacher = false,
                    day = r.date,
                    dayOfWeek = r.dayOfWeek,
                    login = r.studentLogin
                ).mapNotNull { s ->
                    if (s.groupId in (ids + (-11) + (-6) + (0))) {
                        val group = groups.firstOrNull { it.id == s.groupId }
                        if (group?.isActive == true || s.groupId in listOf(-11, -6, 0)) {
                            ScheduleForAttendance(
                                groupId = s.groupId,
                                subjectName = if (group != null) subjects[group.subjectId].toString() else when (s.groupId) {
                                    ScheduleIds.food -> "Приём пищи"
                                    ScheduleIds.extra -> "Доп занятие"
                                    else -> s.custom.firstOrNull().toString()
                                },
                                groupName = group?.name ?: when (s.groupId) {
                                    ScheduleIds.food -> ""
                                    ScheduleIds.extra -> s.teacherLogin
                                    else -> ""
                                },
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

                this.respond(
                    RFetchPreAttendanceDayResponse(
                        schedule = schedule,
                        attendance = if (preAttendance != null) ClientPreAttendance(
                            start = preAttendance.start,
                            end = preAttendance.end,
                            reason = preAttendance.reason,
                            isGood = preAttendance.isGood
                        ) else null
                    )
                ).done
        }
    }

    suspend fun fetchStudents(call: ApplicationCall) {
        val perm = call.isMentor || call.isModer
        call.dRes(perm, "Can't fetch students (mentor)") {
            val forms = if (this.isModer) {
                    Forms.getAllForms().map {
                        MentorForms(
                            id = it.formId,
                            num = it.classNum,
                            title = it.title,
                            isQrActive = it.formId in activeRegistrationForms
                        )
                    }
                } else {
                    Forms.fetchMentorForms(this.login)
                }

                val studentLogins =
                    StudentsInForm.fetchStudentsLoginsAndIdsByFormIds(forms.map { it.id })
                val students =
                    Users.fetchByLoginsActivated(logins = studentLogins.map { it.login })
                val requests = activeRegistrationRequests.filter {
                    it.value.formId in forms.map { it.id }
                }

                this.respond(
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
                ).done
        }
    }
}