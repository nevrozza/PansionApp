package com.nevrozq.pansion.features.lessons

import FIO
import Person
import admin.cabinets.CabinetItem
import admin.cabinets.RFetchCabinetsResponse
import admin.cabinets.RUpdateCabinetsReceive
import admin.calendar.CalendarModuleItem
import admin.calendar.RFetchCalendarResponse
import admin.calendar.RUpdateCalendarReceive
import admin.groups.forms.RCreateFormGroupReceive
import admin.groups.forms.RFetchCutedGroupsResponse
import admin.groups.forms.RFetchFormGroupsReceive
import admin.groups.forms.RFetchFormGroupsResponse
import admin.groups.forms.outside.CreateFormReceive
import admin.groups.forms.outside.REditFormReceive
import admin.groups.forms.outside.RFetchFormsResponse
import admin.groups.forms.outside.RFetchMentorsResponse
import admin.groups.students.RBindStudentToFormReceive
import admin.groups.students.RFetchStudentsInFormReceive
import admin.groups.students.RFetchStudentsInFormResponse
import admin.groups.students.deep.RCreateStudentGroupReceive
import admin.groups.students.deep.RFetchStudentGroupsReceive
import admin.groups.students.deep.RFetchStudentGroupsResponse
import admin.groups.subjects.RCreateGroupReceive
import admin.groups.subjects.REditGroupReceive
import admin.groups.subjects.topBar.RDeleteSubject
import admin.groups.subjects.topBar.REditSubjectReceive
import admin.groups.subjects.RFetchGroupsReceive
import admin.groups.subjects.RFetchGroupsResponse
import admin.groups.subjects.RFetchTeachersResponse
import admin.groups.subjects.topBar.RCreateSubjectReceive
import admin.groups.subjects.topBar.RFetchAllSubjectsResponse
import admin.schedule.RFetchInitScheduleResponse
import admin.schedule.ScheduleFormValue
import admin.schedule.ScheduleGroup
import admin.schedule.SchedulePerson
import admin.schedule.ScheduleSubject
import com.nevrozq.pansion.database.achievements.Achievements
import com.nevrozq.pansion.database.cabinets.Cabinets
import com.nevrozq.pansion.database.cabinets.CabinetsDTO
import com.nevrozq.pansion.database.calendar.Calendar
import com.nevrozq.pansion.database.calendar.CalendarDTO
import com.nevrozq.pansion.database.checkedNotifications.CheckedNotifications
import com.nevrozq.pansion.database.checkedNotifications.CheckedNotificationsDTO
import com.nevrozq.pansion.database.formGroups.FormGroupDTO
import com.nevrozq.pansion.database.formGroups.FormGroups
import com.nevrozq.pansion.database.formGroups.mapToFormGroup
import com.nevrozq.pansion.database.forms.FormDTO
import com.nevrozq.pansion.database.forms.Forms
import com.nevrozq.pansion.database.forms.mapToForm
import com.nevrozq.pansion.database.groups.GroupDTO
import com.nevrozq.pansion.database.groups.Groups
import com.nevrozq.pansion.database.groups.mapToCutedGroup
import com.nevrozq.pansion.database.groups.mapToGroup
import com.nevrozq.pansion.database.groups.mapToTeacherGroup
import com.nevrozq.pansion.database.parents.Parents
import com.nevrozq.pansion.database.preAttendance.PreAttendance
import com.nevrozq.pansion.database.ratingEntities.Marks
import com.nevrozq.pansion.database.ratingEntities.Stups
import com.nevrozq.pansion.database.ratingTable.RatingModule0Table
import com.nevrozq.pansion.database.ratingTable.RatingModule1Table
import com.nevrozq.pansion.database.ratingTable.RatingModule2Table
import com.nevrozq.pansion.database.ratingTable.RatingPreviousWeek0Table
import com.nevrozq.pansion.database.ratingTable.RatingPreviousWeek1Table
import com.nevrozq.pansion.database.ratingTable.RatingPreviousWeek2Table
import com.nevrozq.pansion.database.ratingTable.RatingWeek0Table
import com.nevrozq.pansion.database.ratingTable.RatingWeek1Table
import com.nevrozq.pansion.database.ratingTable.RatingWeek2Table
import com.nevrozq.pansion.database.ratingTable.RatingYear0Table
import com.nevrozq.pansion.database.ratingTable.RatingYear1Table
import com.nevrozq.pansion.database.ratingTable.RatingYear2Table
import com.nevrozq.pansion.database.reportHeaders.ReportHeaders
import com.nevrozq.pansion.database.reportHeaders.ReportHeadersDTO
import com.nevrozq.pansion.database.schedule.Schedule
import com.nevrozq.pansion.database.schedule.ScheduleDTO
import com.nevrozq.pansion.database.studentGroups.StudentGroupDTO
import com.nevrozq.pansion.database.studentGroups.StudentGroups
import com.nevrozq.pansion.database.studentLines.StudentLines
import com.nevrozq.pansion.database.studentsInForm.StudentInFormDTO
import com.nevrozq.pansion.database.studentsInForm.StudentsInForm
import com.nevrozq.pansion.database.subjects.SubjectDTO
import com.nevrozq.pansion.database.subjects.Subjects
import com.nevrozq.pansion.database.subjects.mapToSubject
import com.nevrozq.pansion.database.users.Users
import com.nevrozq.pansion.lastTimeRatingUpdate
import com.nevrozq.pansion.utils.isMember
import com.nevrozq.pansion.utils.isMentor
import com.nevrozq.pansion.utils.isModer
import com.nevrozq.pansion.utils.isParent
import com.nevrozq.pansion.utils.isTeacher
import com.nevrozq.pansion.utils.login
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import journal.init.RFetchStudentsInGroupReceive
import journal.init.RFetchStudentsInGroupResponse
import journal.init.RFetchTeacherGroupsResponse
import main.ClientMainNotification
import main.RDeleteMainNotificationsReceive
import main.RFetchChildrenMainNotificationsResponse
import main.RFetchMainNotificationsReceive
import main.RFetchMainNotificationsResponse
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import rating.RFetchScheduleSubjectsResponse
import rating.RFetchSubjectRatingReceive
import rating.RFetchSubjectRatingResponse
import rating.RatingItem
import report.UserMark
import schedule.PersonScheduleItem
import schedule.RFetchPersonScheduleReceive
import schedule.RFetchScheduleDateReceive
import schedule.RPersonScheduleList
import schedule.RScheduleList
import server.getLocalDate
import server.toMinutes
import java.util.HashMap
import kotlin.math.log

class LessonsController() {

    suspend fun checkMainNotification(call: ApplicationCall) {
        if (call.isMember) {
            try {
                val r = call.receive<RDeleteMainNotificationsReceive>()
                CheckedNotifications.insert(
                    CheckedNotificationsDTO(
                        studentLogin = call.login,
                        key = r.key
                    )
                )
                call.respond(HttpStatusCode.OK)
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't check notification: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    private fun fetchMainNotificationsServer(
        groups: List<GroupDTO>,
        reports: List<ReportHeadersDTO>,
        subjects: Map<Int, String>,
        checkedNotifications: List<String>,
        studentLogin: String
    ): List<ClientMainNotification> {
        val achievements = Achievements.fetchAllByLogin(studentLogin).map {
            val xDate =
                if ((it.showDate?.length ?: 0) > 5) it.showDate ?: it.date else it.date
            ClientMainNotification(
                key = "A.${it.studentLogin}.${it.id}",
                subjectName = subjects[it.subjectId].toString(),
                reason = "A.${it.text}.${it.stups}",
                date = xDate,
                reportTime = null,
                groupName = null,
                reportId = null
            )
        }
        val nKiOpozd =
            StudentLines.fetchStudentLinesByLogin(login = studentLogin).mapNotNull { x ->
                val isL = x.isLiked in listOf("f", "t")
                val isNka = x.attended == "1" || x.attended == "2"
                val group = groups.firstOrNull { it.id == x.groupId }
                val header =
                    if (group != null) reports.firstOrNull { it.id == x.reportId } else null
                if (header != null) {
//                    val pa = PreAttendance.fetchPreAttendanceByDateAndLogin(
//                        date = header.date,
//                        login = studentLogin
//                    )
                    val time = header.time.toMinutes()
//                    val is2NKA =
//                        if (pa != null && !isNka) pa.start.toMinutes() <= time && pa.end.toMinutes() > time else false
                    val isLate = (x.lateTime.isNotEmpty() && x.lateTime != "0")
                    if (isLate || isNka || isL) { // || is2NKA
                        val subject =
                            if (group != null) subjects[group.subjectId].toString() else "null"
                        ClientMainNotification(
                            key = if (/*is2NKA ||*/ isNka) "N.${x.login}.${x.reportId}" else if (isLate) "Op.${x.login}.${x.reportId}" else "L.${x.login}.${x.reportId}",
                            subjectName = subject,
                            reason = /*if (is2NKA) "N.${if (pa!!.isGood) "2" else "1"}" else */if (isNka) "N.${x.attended}" else if (isLate) "Op.${x.lateTime}" else "L.${if (x.isLiked == "t") "T" else "F"}",
                            date = header.date.toString(),
                            reportTime = header.time.toString(),
                            groupName = group?.name.toString(),
                            reportId = x.reportId
                        )
                    } else null
                } else null
            }
        return (achievements + nKiOpozd).filter { it.key !in checkedNotifications }
            .sortedWith(
                compareBy(
                    { getLocalDate(it.date).toEpochDays() },
                    { it.reportTime?.toMinutes() })
            ).reversed()
    }

    suspend fun fetchMainChildrenNotifications(
        call: ApplicationCall
    ) {
        if (call.isMember) {
            try {

                val checkedNotifications = CheckedNotifications.fetchByLogin(call.login)
                val groups: List<GroupDTO> = Groups.getAllGroups()
                val reports: List<ReportHeadersDTO> = ReportHeaders.fetchReportHeaders()
                val subjects = Subjects.fetchAllSubjectsAsMap() + mapOf(
                    -2 to "Дисциплина",
                    -3 to "Общественная работа",
                    -4 to "Творчество"
                )

                val logins: MutableList<Person> = mutableListOf()

                if (call.isParent) {
                    logins.addAll(Parents.fetchChildren(parentLogin = call.login).map {
                        Person(
                            login = it.login,
                            fio = it.fio,
                            isActive = it.isActive
                        )
                    }
                    )
                }
                if (call.isMentor) {
                    val forms = Forms.fetchMentorForms(call.login)
                    logins.addAll(
                        StudentsInForm.fetchStudentsLoginsByFormIds(forms.map { it.id }).map {
                            val user = Users.fetchUser(it)!!
                            Person(
                                login = it,
                                fio = FIO(
                                    name = user.name,
                                    surname = user.surname,
                                    praname = user.praname
                                ),
                                isActive = user.isActive
                            )
                        }
                    )
                }
                val endLogins = logins.toSet().toList()
                val end = endLogins.associate {
                    it.login to fetchMainNotificationsServer(
                        groups = groups,
                        checkedNotifications = checkedNotifications,
                        reports = reports,
                        subjects = subjects,
                        studentLogin = it.login
                    )
                }

                call.respond(
                    RFetchChildrenMainNotificationsResponse(
                        students = endLogins.filter { !end[it.login].isNullOrEmpty() },
                        notifications = end
                    )
                )
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch notifications: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchMainNotifications(
        call: ApplicationCall,
    ) {
        if (call.isMember) {
            try {
                val r = call.receive<RFetchMainNotificationsReceive>()

                val checkedNotifications = CheckedNotifications.fetchByLogin(call.login)
                val groups: List<GroupDTO> = Groups.getAllGroups()
                val reports: List<ReportHeadersDTO> = ReportHeaders.fetchReportHeaders()
                val subjects = Subjects.fetchAllSubjectsAsMap() + mapOf(
                    -2 to "Дисциплина",
                    -3 to "Общественная работа",
                    -4 to "Творчество"
                )

                val filtered = fetchMainNotificationsServer(
                    groups = groups,
                    checkedNotifications = checkedNotifications,
                    reports = reports,
                    subjects = subjects,
                    studentLogin = r.studentLogin
                )

                call.respond(
                    RFetchMainNotificationsResponse(
                        filtered
                    )
                )
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch notifications: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchCalendar(call: ApplicationCall) {
        if (call.isMember) {
            try {
                val calendar = Calendar.getAllModules()
                call.respond(RFetchCalendarResponse(
                    items = calendar.map {
                        CalendarModuleItem(
                            num = it.num,
                            start = it.start,
                            halfNum = it.halfNum
                        )
                    }
                ))
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch calendar: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun updateCalendar(call: ApplicationCall) {
        if (call.isModer) {
            val r = call.receive<RUpdateCalendarReceive>()
            try {
                Calendar.insertList(r.items.map {
                    CalendarDTO(
                        num = it.num,
                        start = it.start,
                        halfNum = it.halfNum
                    )
                })
                call.respond(HttpStatusCode.OK)
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Calendar already exists")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't create calendar: ${e.localizedMessage}"
                )
            }
        }
    }

    suspend fun fetchRating(call: ApplicationCall) {
        if (call.isMember) {
            val r = call.receive<RFetchSubjectRatingReceive>()
            try {
                val table = when (r.period) {
                    1 -> when (r.forms) { //Module
                        1 -> RatingModule1Table
                        2 -> RatingModule2Table
                        else -> RatingModule0Table
                    }

                    2 -> when (r.forms) { //Year
                        1 -> RatingYear1Table
                        2 -> RatingYear2Table
                        else -> RatingYear0Table
                    }
                    3 -> when (r.forms) { //Year
                        1 -> RatingPreviousWeek1Table
                        2 -> RatingPreviousWeek2Table
                        else -> RatingPreviousWeek0Table
                    }

                    else -> when (r.forms) { //0
                        1 -> RatingWeek1Table
                        2 -> RatingWeek2Table
                        else -> RatingWeek0Table
                    }
                }
                val allItems = table.fetchAllRatings()
                val items = allItems.filter { it.subjectId == r.subjectId }
                val me = items.firstOrNull { it.login == r.login }
                call.respond(RFetchSubjectRatingResponse(
                    hashMapOf(
                        r.subjectId to items.map {
                            RatingItem(
                                login = it.login,
                                fio = FIO(
                                    name = it.name,
                                    surname = it.surname,
                                    praname = it.praname
                                ),
                                avatarId = it.avatarId,
                                stups = it.stups,
                                top = it.top,
                                groupName = it.groupName,
                                formNum = it.formNum,
                                formShortTitle = it.formShortTitle,
                                avg = it.avg
                            )
                        }
                    ),
                    me = hashMapOf(
                        r.subjectId to if (me != null) Pair(me.top, me.stups) else null
                    ),
                    lastTimeEdit = lastTimeRatingUpdate
                ))
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch rating: ${e.message}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchScheduleSubjects(call: ApplicationCall) {
        if (call.isMember) {
            try {
                val subjects = Subjects.fetchAllSubjects()
                call.respond(
                    RFetchScheduleSubjectsResponse(subjects.mapNotNull {
                        ScheduleSubject(
                            id = it.id,
                            name = it.name,
                            isActive = it.isActive
                        )
                    })
                )
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch subjects: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchSchedule(call: ApplicationCall) {
        if (call.isMember) {
            val r = call.receive<RFetchScheduleDateReceive>()
            try {
                var items = Schedule.getOnDate(r.day)
//                println(items.isEmpty())
                if (items.isEmpty()) {
                    items = Schedule.getOnDate(r.dayOfWeek)
                }
                call.respond(
                    RScheduleList(hashMapOf(r.day to items))
                )
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch scheduleItems: ${e.message}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

//    suspend fun fetchPersonMarks

    suspend fun fetchPersonSchedule(call: ApplicationCall) {
        if (call.isMember) {
            val r = call.receive<RFetchPersonScheduleReceive>()
            val alreadyGroups = mutableListOf<Int>()
            try {
                var items = Schedule.getOnDate(r.day)
                if (items.isEmpty()) {
                    items = Schedule.getOnDate(r.dayOfWeek)
                }

                items = if (call.isTeacher) {
                    items.filter { it.teacherLogin == r.login }
                } else {
                    val idList = StudentGroups.fetchGroupsOfStudent(r.login)

//                    val parts = r.day.split(".")
//                    val date = "${parts[0]}.${parts[1]}.${parts[2]}"
//                    val marks = Marks.fetchUserByDate(login = call.login, date = date)
//                    val stups = Stups.fetchUserByDate(login = call.login, date = date)
                    items.filter { it.groupId in idList.filter { it.isActive }.map { it.id } }
                }

                val subjects = Subjects.fetchAllSubjects()
                val groups = Groups.getAllGroups()
                val teachers = Users.fetchAllTeachers()

                val personItems = items.mapNotNull {
                    val group = groups.firstOrNull { group -> group.id == it.groupId }
                    alreadyGroups.add(it.groupId)
                    val teacher =
                        teachers.firstOrNull { teacher -> teacher.login == it.teacherLogin }

                    val marks = Marks.fetchUserByDate(login = r.login, date = r.day)
                        .filter { x -> x.groupId == it.groupId }
                    val stups = Stups.fetchUserByDate(login = r.login, date = r.day)
                        .filter { x -> x.groupId == it.groupId }


                    val fio = FIO(
                        name = teacher?.name ?: "null",
                        surname = teacher?.surname ?: "null",
                        praname = teacher?.praname
                    )

                    if (group != null) {
                        PersonScheduleItem(
                            groupId = it.groupId,
                            cabinet = it.cabinet,
                            start = it.t.start,
                            end = it.t.end,
                            subjectName = subjects.first { it.id == group.subjectId }.name,
                            groupName = group.name,
                            teacherFio = fio,
                            marks = if ((alreadyGroups.find { x -> x == it.groupId }
                                    ?: 0) > 1) listOf() else marks.map {
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
                            },
                            stupsSum = stups.sumOf { it.content.toInt() },
                            isSwapped = it.teacherLoginBefore != it.teacherLogin
                        )
                    } else {
                        null
                    }
                }

                call.respond(RPersonScheduleList((hashMapOf(r.day to personItems))))

            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch personScheduleItems: ${e.message}"
                )
            }
        } else {
            call.respond(
                HttpStatusCode.OK, "No permission"
            )
        }
    }

    suspend fun saveSchedule(call: ApplicationCall) {
        if (call.isModer) {
            val r = call.receive<RScheduleList>()
            try {
                val list = r.list.map { item ->
                    val date = item.key
                    item.value.map {
                        ScheduleDTO(
                            date = date,
                            teacherLogin = it.teacherLogin,
                            groupId = it.groupId,
                            start = it.t.start,
                            end = it.t.end,
                            cabinet = it.cabinet.toString(),
                            teacherLoginBefore = it.teacherLoginBefore
                        )
                    }
                }
                transaction {
                    val dates: List<String> = list.flatMap { it.map { it.date } }
                    Schedule.deleteWhere {
                        (date.inList(dates))
                    }
                    list.forEach {
                        Schedule.insertList(
                            it
                        )
                    }
                }
                call.respond(HttpStatusCode.OK)
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Schedules already exists")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't create schedule: ${e.localizedMessage}"
                )
            }
        }
    }

    suspend fun fetchAllSubjects(call: ApplicationCall) {
        if (call.isMember) {
            try {
                val subjects = Subjects.fetchAllSubjects()

                call.respond(
                    RFetchAllSubjectsResponse(subjects.map { it.mapToSubject() })
                )
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch subjects: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchAllTeachersForGroups(call: ApplicationCall) {
        if (call.isMember) {
            try {
                val teachers = Users.fetchAllTeachers()

                call.respond(RFetchTeachersResponse(
                    teachers.filter { it.isActive }.map {
                        Person(
                            login = it.login,
                            fio = FIO(
                                name = it.name,
                                surname = it.surname,
                                praname = it.praname
                            ),
                            isActive = true
                        )
                    }
                ))
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch teachers: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchAllMentorsForGroups(call: ApplicationCall) {
        if (call.isMember) {
            try {
                val mentors = Users.fetchAllMentors()
                println("fuck: ${mentors}")
                val result = mentors.filter { it.isActive }.map {
                    Person(
                        login = it.login,
                        fio = FIO(
                            name = it.name,
                            surname = it.surname,
                            praname = it.praname
                        ),
                        isActive = true
                    )
                }
                println("fuck2: $result")
                call.respond(
                    RFetchMentorsResponse(
                        result
                    )
                )
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch mentors: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }


    suspend fun fetchInitSchedule(call: ApplicationCall) {
        if (call.isModer) {
            try {
                val teachers = mutableListOf<SchedulePerson>()
                val students = mutableListOf<SchedulePerson>()


                val ff = Forms.getAllForms().filter { it.isActive }
                val fs = StudentsInForm.fetchAll()
                val forms = ff.map {
                    it.formId to ScheduleFormValue(
                        num = it.classNum,
                        shortTitle = it.shortTitle,
                        logins = fs.filter { x -> x.formId == it.formId }.map { it.login }
                    )
                }.toMap(HashMap())

                val tt = Users.fetchAllTeachers().filter { it.isActive }
                val ss = Users.fetchAllStudents().filter { it.isActive }
                val gg = Groups.getAllGroups().sortedBy { it.isActive }
                val gs = StudentGroups.fetchAll()
                val subjects = Subjects.fetchAllSubjects()

                tt.forEach { t ->
                    val groups =
                        gg.filter { it.teacherLogin == t.login }.map { Pair(it.id, it.isActive) }
                    teachers.add(
                        SchedulePerson(
                            login = t.login,
                            fio = FIO(
                                name = t.name,
                                surname = t.surname,
                                praname = t.praname
                            ),
                            groups = groups
                        )
                    )
                }

                ss.forEach { s ->
                    val groups =
                        gs.filter { it.studentLogin == s.login }.filter {
                            true
//                            val id = it.groupId
//                            gg.firstOrNull { it.id == id }?.isActive == true
                        }.map { xs ->
                            Pair(
                                xs.groupId,
                                gg.firstOrNull { it.id == xs.groupId }?.isActive == true
                            )
                        }

                    students.add(
                        SchedulePerson(
                            login = s.login,
                            fio = FIO(
                                name = s.name,
                                surname = s.surname,
                                praname = s.praname
                            ),
                            groups = groups
                        )
                    )
                }

                call.respond(
                    RFetchInitScheduleResponse(
                        teachers = teachers.filter { it.groups.isNotEmpty() },
                        students = students.filter { it.groups.isNotEmpty() },
                        groups = gg.map {
                            ScheduleGroup(
                                id = it.id,
                                subjectId = it.subjectId,
                                name = it.name
                            )
                        },
                        subjects = subjects.map {
                            ScheduleSubject(
                                id = it.id,
                                name = it.name,
                                isActive = it.isActive
                            )
                        },
                        forms = forms
                    )
                )

            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch schedule: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchCutedGroups(call: ApplicationCall) {
        val r = call.receive<RFetchGroupsReceive>()
        if (call.isMember) {
            try {
                val groups = Groups.fetchGroupOfSubject(r.subjectId).filter { it.isActive }

                call.respond(
                    RFetchCutedGroupsResponse(groups.map { it.mapToCutedGroup() })
                )
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch groupsOfThisSubject: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchGroups(call: ApplicationCall) {
        val r = call.receive<RFetchGroupsReceive>()
        if (call.isMember) {
            try {
                val groups = Groups.fetchGroupOfSubject(r.subjectId).map { it.mapToGroup() }

                call.respond(
                    RFetchGroupsResponse(groups)
                )
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch groupsOfThisSubject: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }


    suspend fun fetchAllCabinets(call: ApplicationCall) {
        if (call.isMember) {
            try {
                val cabinets = Cabinets.getAllCabinets()
                call.respond(RFetchCabinetsResponse(
                    cabinets.map {
                        CabinetItem(
                            login = it.login,
                            cabinet = it.cabinet
                        )
                    }
                ))
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch cabinets: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }


    suspend fun fetchAllForms(call: ApplicationCall) {
        if (call.isMember) {
            try {
                val forms = Forms.getAllForms()

                call.respond(
                    RFetchFormsResponse(forms.map { it.mapToForm() })
                )
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch forms: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun updateCabinets(call: ApplicationCall) {
        if (call.isModer) {
            val r = call.receive<RUpdateCabinetsReceive>()
            try {
                Cabinets.insertList(r.cabinets.map {
                    CabinetsDTO(
                        login = it.login,
                        cabinet = it.cabinet
                    )
                })
                call.respond(HttpStatusCode.OK)
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Cabinet already exists")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't create cabinet: ${e.localizedMessage}"
                )
            }
        }
    }

    suspend fun createGroup(call: ApplicationCall) {
        val r = call.receive<RCreateGroupReceive>()
        if (call.isModer) {
            try {
                Groups.insert(
                    GroupDTO(
                        name = r.group.name,
                        teacherLogin = r.group.teacherLogin,
                        subjectId = r.group.subjectId,
                        difficult = r.group.difficult,
                        isActive = true
                    )
                )

                call.respond(HttpStatusCode.OK)
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Group already exists")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't create group: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun editGroup(call: ApplicationCall) {
        if (call.isModer) {
            val r = call.receive<REditGroupReceive>()
            try {
                Groups.update(id = r.id, r)

                call.respond(HttpStatusCode.OK)
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Group already exists")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't edit group: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun createForm(call: ApplicationCall) {
        val r = call.receive<CreateFormReceive>()
        if (call.isModer) {
            try {
                Forms.insert(
                    FormDTO(
                        title = r.form.title,
                        classNum = r.form.classNum,
                        mentorLogin = r.form.mentorLogin,
                        shortTitle = r.form.shortTitle,
                        isActive = true
                    )
                )

                call.respond(HttpStatusCode.OK)
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Form already exists")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't create group: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun editForm(call: ApplicationCall) {
        if (call.isModer) {
            val r = call.receive<REditFormReceive>()
            try {

                Forms.update(r)

                call.respond(HttpStatusCode.OK)
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Form already exists")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't create group: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }


    suspend fun deleteFormGroup(call: ApplicationCall) {
        val r = call.receive<RCreateFormGroupReceive>()
        if (call.isModer) {
            try {

                FormGroups.delete(
                    FormGroupDTO(
                        formId = r.formId,
                        groupId = r.groupId,
                        subjectId = r.subjectId
                    )
                )
                call.respond(HttpStatusCode.OK)
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "wtfIsGoingOn")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't delete: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun deleteStudentGroup(call: ApplicationCall) {
        val r = call.receive<RCreateStudentGroupReceive>()
        if (call.isModer) {
            try {
                StudentGroups.delete(
                    StudentGroupDTO(
                        studentLogin = r.studentLogin,
                        groupId = r.groupId,
                        subjectId = r.subjectId
                    )
                )
                call.respond(HttpStatusCode.OK)
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "wtfIsGoingOn")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't delete: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun createStudentGroup(call: ApplicationCall) {
        val r = call.receive<RCreateStudentGroupReceive>()
        if (call.isModer) {
            try {
                StudentGroups.insert(
                    StudentGroupDTO(
                        studentLogin = r.studentLogin,
                        groupId = r.groupId,
                        subjectId = r.subjectId
                    )
                )
                call.respond(HttpStatusCode.OK)
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "StudentGroup already exists")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't create group: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun createFormGroup(call: ApplicationCall) {
        val r = call.receive<RCreateFormGroupReceive>()
        if (call.isModer) {
            try {
                FormGroups.insert(
                    FormGroupDTO(
                        formId = r.formId,
                        groupId = r.groupId,
                        subjectId = r.subjectId
                    )
                )
                call.respond(HttpStatusCode.OK)
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "FormGroup already exists")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't create group: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchFormGroups(call: ApplicationCall) {
        val r = call.receive<RFetchFormGroupsReceive>()
        if (call.isMember) {
            try {
                val forms = FormGroups.getGroupsOfThisForm(r.formId)

                call.respond(
                    RFetchFormGroupsResponse(forms.map { it.mapToFormGroup() })
                )
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch formGroups(binding): ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    private fun fetchStudentsInFormNotCall(formId: Int): List<Person> {
        if (formId == 0) {
            val loginsWithGroups = StudentsInForm.fetchAllStudentsLogins()
            val students =
                Users.fetchAllStudents().filter { it.login !in loginsWithGroups && it.isActive }
            val response = students.map {
                Person(
                    login = it.login,
                    fio = FIO(
                        name = it.name,
                        surname = it.surname,
                        praname = it.praname
                    ),
                    isActive = true
                )
            }
            return response
        } else {
            val logins = StudentsInForm.fetchStudentLoginsInForm(formId = formId)
            val response = logins.map {
                val user = Users.fetchUser(it)
                if (user == null || !user.isActive) {
                    null
                } else {
                    Person(
                        login = user.login,
                        fio = FIO(
                            name = user.name,
                            surname = user.surname,
                            praname = user.praname
                        ),
                        isActive = true
                    )
                }
            }
            return response.filterNotNull()
        }
    }

    suspend fun fetchStudentGroups(call: ApplicationCall) {
        val r = call.receive<RFetchStudentGroupsReceive>()
        if (call.isMember) {
            try {
                call.respond(
                    RFetchStudentGroupsResponse(
                        StudentGroups.fetchGroupsOfStudent(r.studentLogin).map { it.mapToGroup() })
                )
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch groups for students: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchTeacherGroups(call: ApplicationCall) {
        if (call.isTeacher || call.isModer || call.isMentor) {
            try {
                val groups = if (call.isModer) {
                    Groups.getAllGroups().filter { it.isActive }
                } else if (call.isMentor) {
                    val forms = Forms.fetchMentorForms(call.login)
                    StudentsInForm.fetchStudentsLoginsByFormIds(forms.map { it.id })
                        .flatMap { s ->
                            StudentGroups.fetchGroupsOfStudent(s)
                        }.toSet().toList().filter { it.isActive }
                } else {
                    Groups.getGroupsOfTeacher(call.login).filter { it.isActive }
                }
                call.respond(RFetchTeacherGroupsResponse(groups.map { it.mapToTeacherGroup(call.login) }))
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch teacher(cuted+) groups: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchStudentsInGroup(call: ApplicationCall) {
        val r = call.receive<RFetchStudentsInGroupReceive>()
        if (call.isMember) {
            try {
                val students = StudentGroups.fetchStudentsOfGroup(
                    groupId = r.groupId
                ).filter { it.isActive }
                call.respond(RFetchStudentsInGroupResponse(students))
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch students in group: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchStudentsInForm(call: ApplicationCall) {
        val r = call.receive<RFetchStudentsInFormReceive>()
        if (call.isMember) {
            try {
                call.respond(RFetchStudentsInFormResponse(fetchStudentsInFormNotCall(r.formId)))
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch students in form: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun bindStudentToForm(call: ApplicationCall) {
        val r = call.receive<RBindStudentToFormReceive>()
        if (call.isModer) {
            try {
                StudentsInForm.insert(
                    StudentInFormDTO(
                        formId = r.formId,
                        login = r.studentLogin
                    )
                )
//                val students = fetchStudentsInFormNotCall(r.currentFormId)
                call.respond(HttpStatusCode.OK)
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't bind student to form: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun createSubject(call: ApplicationCall) {
        val r = call.receive<RCreateSubjectReceive>()
        if (call.isModer) {
            try {
                Subjects.insert(
                    SubjectDTO(
                        name = r.name,
                        isActive = true
                    )
                )

                call.respond(HttpStatusCode.OK)
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Subject already exists")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't create subject: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun editSubject(call: ApplicationCall) {
        if (call.isModer) {
            val r = call.receive<REditSubjectReceive>()
            try {
                Subjects.update(r.subjectId, r.name)

                call.respond(HttpStatusCode.OK)
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Subject already exists")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't edit subject: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun deleteSubject(call: ApplicationCall) {
        if (call.isModer) {
            val r = call.receive<RDeleteSubject>()
            try {
                val groups = Groups.fetchGroupOfSubject(r.subjectId)
                transaction {
                    groups.forEach {
                        Groups.update(
                            it.id,
                            REditGroupReceive(
                                id = it.id,
                                name = it.name,
                                mentorLogin = it.teacherLogin,
                                difficult = it.difficult,
                                isActive = false
                            )
                        )
                    }
                }
                Subjects.update(r.subjectId, null, false)
                call.respond(HttpStatusCode.OK)
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Subject already exists")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't delete subject: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }
}