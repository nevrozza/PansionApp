package com.nevrozq.pansion.features.lessons

import FIO
import Person
import RFetchGroupDataReceive
import RFetchGroupDataResponse
import TeacherPerson
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
import admin.groups.subjects.RAddStudentToGroup
import admin.groups.subjects.RCreateGroupReceive
import admin.groups.subjects.REditGroupReceive
import admin.groups.subjects.RFetchGroupsReceive
import admin.groups.subjects.RFetchGroupsResponse
import admin.groups.subjects.RFetchTeachersResponse
import admin.groups.subjects.topBar.RCreateSubjectReceive
import admin.groups.subjects.topBar.RDeleteSubject
import admin.groups.subjects.topBar.REditSubjectReceive
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
import com.nevrozq.pansion.database.duty.Duty
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
import com.nevrozq.pansion.database.holidays.Holidays
import com.nevrozq.pansion.database.parents.Parents
import com.nevrozq.pansion.database.ratingEntities.Marks
import com.nevrozq.pansion.database.ratingEntities.Stups
import com.nevrozq.pansion.database.ratingTable.*
import com.nevrozq.pansion.database.reportHeaders.ReportHeaders
import com.nevrozq.pansion.database.reportHeaders.ReportHeadersDTO
import com.nevrozq.pansion.database.schedule.Schedule
import com.nevrozq.pansion.database.schedule.ScheduleDTO
import com.nevrozq.pansion.database.scheduleConflicts.ScheduleConflicts
import com.nevrozq.pansion.database.scheduleConflicts.ScheduleConflictsDTO
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
import com.nevrozq.pansion.lastTimeScheduleUpdate
import com.nevrozq.pansion.utils.*
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import journal.init.PersonForGroup
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
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import rating.*
import rating.RatingItem
import report.RMarkLessonReceive
import report.UserMark
import schedule.*
import server.*

class LessonsController() {


    suspend fun fetchGroupData(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch group data") {
            val r = this.receive<RFetchGroupDataReceive>()
            val group = Groups.getGroupById(r.groupId)
            this.respond(
                RFetchGroupDataResponse(
                    groupName = group?.name ?: "",
                    subjectId = group?.subjectId,
                    subjectName = if (group?.subjectId != null) Subjects.fetchName(group.subjectId) else "",
                    teacherLogin = group?.teacherLogin ?: ""
                )
            ).done
        }
    }

    suspend fun markLesson(call: ApplicationCall) {
        val perm = call.isTeacher
        call.dRes(perm, "Can't mark lesson") {
            val r = this.receive<RMarkLessonReceive>()
            Schedule.markLesson(lessonId = r.lessonId, lessonDate = r.date)
            this.respond(HttpStatusCode.OK).done
        }
    }

    suspend fun addStudentToGroupFromSubject(call: ApplicationCall) {
        val perm = call.isModer

        call.dRes(perm, "Can't add student to group from subject") {
            val r = this.receive<RAddStudentToGroup>()
            val login = Users.getLoginWithFIO(r.fio, itShouldBeStudent = true)!!
            StudentGroups.insert(
                StudentGroupDTO(
                    groupId = r.groupId,
                    subjectId = r.subjectId,
                    studentLogin = login
                )
            )
            this.respond(
                HttpStatusCode.OK
            ).done
        }
    }

    suspend fun checkMainNotification(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't check notification") {
            val r = this.receive<RDeleteMainNotificationsReceive>()
            CheckedNotifications.insert(
                CheckedNotificationsDTO(
                    studentLogin = this.login,
                    key = r.key
                )
            )
            this.respond(HttpStatusCode.OK).done
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
            StudentLines.fetchStudentLinesByLogin(login = studentLogin, edYear = getCurrentEdYear()).mapNotNull { x ->
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
        val perm = call.isMember
        call.dRes(perm, "Can't fetch children notifications") {

            val checkedNotifications = CheckedNotifications.fetchByLogin(this.login)
            val groups: List<GroupDTO> = Groups.getAllGroups()
            val reports: List<ReportHeadersDTO> = ReportHeaders.fetchReportHeaders()
            val subjects = Subjects.fetchAllSubjectsAsMap() + mapOf(
                ExtraSubjectsId.mvd to "Дисциплина",
                ExtraSubjectsId.social to "Общественная работа",
                ExtraSubjectsId.creative to "Творчество"
            )

            val logins: MutableList<Person> = mutableListOf()


            if (this.isParent) {
                logins.addAll(Parents.fetchChildren(parentLogin = this.login).map {
                    Person(
                        login = it.login,
                        fio = it.fio,
                        isActive = it.isActive
                    )
                }
                )
            }

            if (this.isMentor) {
                val forms = Forms.fetchMentorForms(this.login)
                logins.addAll(
                    StudentsInForm.fetchStudentsLoginsByFormIds(forms.map { it.id }).mapNotNull {
                        val user = Users.fetchUser(it)
                        if (user != null) {
                            Person(
                                login = it,
                                fio = FIO(
                                    name = user.name,
                                    surname = user.surname,
                                    praname = user.praname
                                ),
                                isActive = user.isActive
                            )
                        } else null
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

            this.respond(
                RFetchChildrenMainNotificationsResponse(
                    students = endLogins.filter { !end[it.login].isNullOrEmpty() },
                    notifications = end
                )
            ).done
        }
    }

    suspend fun fetchMainNotifications(
        call: ApplicationCall,
    ) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch notifications") {
            val r = this.receive<RFetchMainNotificationsReceive>()

            val checkedNotifications = CheckedNotifications.fetchByLogin(this.login)
            val groups: List<GroupDTO> = Groups.getAllGroups()
            val reports: List<ReportHeadersDTO> = ReportHeaders.fetchReportHeaders()
            val subjects = Subjects.fetchAllSubjectsAsMap() + mapOf(
                ExtraSubjectsId.mvd to "Дисциплина",
                ExtraSubjectsId.social to "Общественная работа",
                ExtraSubjectsId.creative to "Творчество"
            )

            val filtered = fetchMainNotificationsServer(
                groups = groups,
                checkedNotifications = checkedNotifications,
                reports = reports,
                subjects = subjects,
                studentLogin = r.studentLogin
            )

            this.respond(
                RFetchMainNotificationsResponse(
                    filtered
                )
            ).done
        }
    }

    suspend fun fetchCalendar(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch calendar") {
            val calendar = Calendar.getAllModules()
            val holidays = Holidays.fetchAll()
            this.respond(
                RFetchCalendarResponse(
                    items = calendar.map {
                        CalendarModuleItem(
                            num = it.num,
                            start = it.start,
                            halfNum = it.halfNum
                        )
                    },
                    holidays = holidays
                )
            ).done
        }
    }

    suspend fun updateCalendar(call: ApplicationCall) {
        val perm = call.isModer
        call.dRes(perm, "Can't update calendar") {
            val r = this.receive<RUpdateCalendarReceive>()
            Calendar.insertList(r.items.map {
                CalendarDTO(
                    num = it.num,
                    start = it.start,
                    halfNum = it.halfNum
                )
            })
            Holidays.insertList(r.holidays)
            this.respond(HttpStatusCode.OK).done
        }
    }

    suspend fun fetchRating(call: ApplicationCall) {
        // NEXT TIME
        if (call.isMember) {
            try {
                val r = call.receive<RFetchSubjectRatingReceive>()
                val period = r.period ?: rating.PansionPeriod.Week(getCurrentWeek().num)
                //Pair(0, "Все"), Pair(1, "5-8 классы"), Pair(2, "9-11 классы")
                val table = when (r.forms) {
                    1 -> RatingLowSchoolTable
                    2 -> RatingHighSchoolTable
                    else -> RatingCommonSchoolTable
                }
                val allItems = table.fetchAllRatings(
                    subjectId = r.subjectId,
                    edYear = getCurrentEdYear(),
                    period = period
                )
                val items = allItems
                val me = items.firstOrNull { it.login == r.login }
                call.respond(
                    RFetchSubjectRatingResponse(
                        mapOf(
                            period.toStr() to mapOf(
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
                            )),
                        me = mapOf(
                            period.toStr() to mapOf(
                                r.subjectId to if (me != null) Pair(me.top, me.stups) else null
                            )
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
        val perm = call.isMember
        call.dRes(perm, "Can't fetch schedule subjects") {
            val subjects = Subjects.fetchAllSubjects()
            val module = getModuleByDate(getCurrentDate().second)
            this.respond(
                RFetchScheduleSubjectsResponse(
                    subjects.mapNotNull {
                        ScheduleSubject(
                            id = it.id,
                            name = it.name,
                            isActive = it.isActive
                        )
                    },
                    holiday = Holidays.fetch(getCurrentEdYear()),
                    currentModule = module?.num ?: 1,
                    currentHalf = module?.halfNum ?: 1
                )
            ).done
        }
    }

    suspend fun fetchSchedule(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch schedule") {
            val r = this.receive<RFetchScheduleDateReceive>()
            val items = Schedule.getOnDate(r.day)
            val conflictItems = ScheduleConflicts.fetchByDate(r.day)
            val map = mutableMapOf(
                r.day to items
            )
            val conflictMap = mutableMapOf(
                r.day to conflictItems.associate { it.lessonIndex to it.logins }.toMutableMap()
            )
            if (r.isFirstTime) {
                for (i in (1..5)) {
                    map[i.toString()] = Schedule.getOnDate(i.toString())
                    conflictMap[i.toString()] =
                        ScheduleConflicts.fetchByDate(i.toString()).associate { it.lessonIndex to it.logins }
                            .toMutableMap()
                }
            }
            this.respond(
                RScheduleList(
                    map.toMap(HashMap()),
                    conflictList = conflictMap.toMap(HashMap())
                )
            ).done
        }
    }

    suspend fun fetchPersonSchedule(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch personal Schedule") {
            val r = call.receive<RFetchPersonScheduleReceive>()
            val alreadyGroups = mutableListOf<Int>()
            val isTeacher = Users.getRole(r.login) != Roles.student
            val items = com.nevrozq.pansion.features.lessons.fetchSchedule(
                isTeacher = isTeacher,
                day = r.day,
                dayOfWeek = r.dayOfWeek,
                login = r.login
            )

            val subjects = Subjects.fetchAllSubjects()
            val groups = Groups.getAllGroups()
            val teachers = Users.fetchAllTeachers()

            val students = Users.fetchAllStudents().filter { it.isActive }

            val personItems = items.mapNotNull {
                val teacher =
                    teachers.firstOrNull { teacher -> teacher.login == it.teacherLogin }
                val fio = FIO(
                    name = teacher?.name ?: "null",
                    surname = teacher?.surname ?: "null",
                    praname = teacher?.praname
                )
                if (it.groupId !in listOf(-6, -11, 0)) {
                    val group = groups.firstOrNull { group -> group.id == it.groupId }
                    alreadyGroups.add(it.groupId)
                    val marks = Marks.fetchUserByDate(login = r.login, date = r.day)
                        .filter { x -> x.groupId == it.groupId }
                    val stups = Stups.fetchUserByDate(login = r.login, date = r.day)
                        .filter { x -> x.groupId == it.groupId }
                    if (group != null) {
                        PersonScheduleItem(
                            groupId = it.groupId,
                            cabinet = it.cabinet,
                            start = it.t.start,
                            end = it.t.end,
                            subjectName = subjects.firstOrNull { it.id == group.subjectId }?.name.toString(),
                            groupName = group.name,
                            teacherFio = fio,
                            marks = if ((alreadyGroups.find { x -> x == it.groupId }
                                    ?: 0) > 1) listOf() else marks.mapNotNull {
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
                                } else {
                                    null
                                }
                            },
                            stupsSum = stups.sumOf { it.content.toInt() },
                            isSwapped = it.teacherLoginBefore != it.teacherLogin,
                            lessonIndex = it.index,
                            isMarked = it.isMarked
                        )
                    } else {
                        null
                    }
                } else {
                    if (it.groupId == ScheduleIds.extra) {
                        val dopFio = if (!isTeacher) fio
                        else {
                            val users = students.filter { x -> it.custom.contains(x.login) }
                            FIO(
                                name = "",
                                praname = null,
                                surname = "${users.map { "${it.surname} ${it.name[0]}" }}".replace("[", "")
                                    .replace("]", "")
                            )
                        }
                        PersonScheduleItem(
                            groupId = it.groupId,
                            cabinet = it.cabinet,
                            start = it.t.start,
                            end = it.t.end,
                            subjectName = subjects.firstOrNull { x -> x.id == it.subjectId }?.name.toString(),
                            groupName = "Доп с",
                            teacherFio = dopFio,
                            marks = listOf(),
                            stupsSum = 0,
                            isSwapped = it.teacherLoginBefore != it.teacherLogin,
                            lessonIndex = it.index,
                            isMarked = it.isMarked
                        )
                    } else if (it.groupId == ScheduleIds.food) {
                        PersonScheduleItem(
                            groupId = it.groupId,
                            cabinet = it.cabinet,
                            start = it.t.start,
                            end = it.t.end,
                            subjectName = "",
                            groupName = "",
                            teacherFio = FIO("", "", ""),
                            marks = listOf(),
                            stupsSum = 0,
                            isSwapped = it.teacherLoginBefore != it.teacherLogin,
                            lessonIndex = it.index,
                            isMarked = it.isMarked
                        )
                    } else {
                        PersonScheduleItem(
                            groupId = it.groupId,
                            cabinet = it.cabinet,
                            start = it.t.start,
                            end = it.t.end,
                            subjectName = it.custom.firstOrNull().toString(),
                            groupName = "",
                            teacherFio = FIO("", "", ""),
                            marks = listOf(),
                            stupsSum = 0,
                            isSwapped = it.teacherLoginBefore != it.teacherLogin,
                            lessonIndex = it.index,
                            isMarked = it.isMarked
                        )
                    }
                }
            }

            this.respond(
                RPersonScheduleList(
                    (hashMapOf(r.day to personItems)),
                    lastUpdate = lastTimeScheduleUpdate
                )
            ).done
        }
    }

    suspend fun saveSchedule(call: ApplicationCall) {
        val perm = call.isModer
        call.dRes(perm, "Can't save schedule") {
            val r = this.receive<RScheduleList>()

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
                        teacherLoginBefore = it.teacherLoginBefore,
                        formId = it.formId,
                        custom = it.custom,
                        id = it.index,
                        subjectId = it.subjectId,
                        isMarked = it.isMarked
                    )
                }
            }
            val conflictList = r.conflictList.map { item ->
                val date = item.key
                item.value.map {
                    ScheduleConflictsDTO(
                        date = date,
                        lessonIndex = it.key,
                        logins = it.value
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
                val conflictDates: List<String> = conflictList.flatMap { it.map { it.date } }
                ScheduleConflicts.deleteWhere {
                    (date.inList(conflictDates))
                }
                conflictList.forEach {
                    ScheduleConflicts.insertList(
                        it
                    )
                }
            }
            lastTimeScheduleUpdate = getStringDayTime()
            this.respond(HttpStatusCode.OK).done
        }
    }

    suspend fun fetchAllSubjects(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch all subjects") {
            val subjects = Subjects.fetchAllSubjects()
            this.respond(
                RFetchAllSubjectsResponse(subjects.map { it.mapToSubject() })
            ).done
        }
    }

    suspend fun fetchAllTeachersForGroups(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch all teachers") {
            val teachers = Users.fetchAllTeachers()

            this.respond(
                RFetchTeachersResponse(
                    teachers.filter { it.isActive }.map {
                        TeacherPerson(
                            login = it.login,
                            fio = FIO(
                                name = it.name,
                                surname = it.surname,
                                praname = it.praname
                            ),
                            isActive = true,
                            subjectId = it.subjectId
                        )
                    }
                )).done
        }
    }

    suspend fun fetchAllMentorsForGroups(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch mentors") {
            val mentors = Users.fetchAllMentors()
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
            this.respond(
                RFetchMentorsResponse(
                    result
                )
            ).done
        }
    }


    suspend fun fetchInitSchedule(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch init schedule") {
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
                        groups = groups,
                        subjectId = t.subjectId
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
                        groups = groups,
                        subjectId = s.subjectId
                    )
                )
            }

            this.respond(
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
            ).done
        }
    }

    suspend fun fetchCutedGroups(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch groupsOfThisSubject") {
            val r = this.receive<RFetchGroupsReceive>()
            val groups = Groups.fetchGroupOfSubject(r.subjectId).filter { it.isActive }

            this.respond(
                RFetchCutedGroupsResponse(groups.map { it.mapToCutedGroup() })
            ).done
        }
    }

    suspend fun fetchGroups(call: ApplicationCall) {
        val perm = call.isMember

        call.dRes(perm, "Can't fetch groupsOfThisSubject") {
            val r = this.receive<RFetchGroupsReceive>()
            val groups = Groups.fetchGroupOfSubject(r.subjectId).map { it.mapToGroup() }

            this.respond(
                RFetchGroupsResponse(groups)
            ).done
        }
    }


    suspend fun fetchAllCabinets(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch cabinets") {
            val cabinets = Cabinets.getAllCabinets()
            this.respond(
                RFetchCabinetsResponse(
                    cabinets.map {
                        CabinetItem(
                            login = it.login,
                            cabinet = it.cabinet
                        )
                    }
                )).done
        }
    }


    suspend fun fetchAllForms(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch forms") {
            val forms = Forms.getAllForms()

            this.respond(
                RFetchFormsResponse(forms.map { it.mapToForm() })
            ).done
        }
    }

    suspend fun updateCabinets(call: ApplicationCall) {
        val perm = call.isModer
        call.dRes(perm, "Can't update cabinet") {
            val r = this.receive<RUpdateCabinetsReceive>()
            Cabinets.insertList(r.cabinets.map {
                CabinetsDTO(
                    login = it.login,
                    cabinet = it.cabinet
                )
            })
            this.respond(HttpStatusCode.OK).done
        }
    }

    suspend fun createGroup(call: ApplicationCall) {
        val perm = call.isModer

        call.dRes(perm, "Can't create group") {
            val r = this.receive<RCreateGroupReceive>()
            Groups.insert(
                GroupDTO(
                    name = r.group.name,
                    teacherLogin = r.group.teacherLogin,
                    subjectId = r.group.subjectId,
                    difficult = r.group.difficult,
                    isActive = true
                )
            )

            this.respond(HttpStatusCode.OK).done
        }
    }

    suspend fun editGroup(call: ApplicationCall) {
        val perm = call.isModer
        call.dRes(perm, "Can't edit group") {
            val r = this.receive<REditGroupReceive>()
            Groups.update(id = r.id, r)
            this.respond(HttpStatusCode.OK).done
        }
    }

    suspend fun createForm(call: ApplicationCall) {
        val r = call.receive<CreateFormReceive>()
        val perm = call.isModer
        call.dRes(perm, "Can't create form") {
            Forms.insert(
                FormDTO(
                    title = r.form.title,
                    classNum = r.form.classNum,
                    mentorLogin = r.form.mentorLogin,
                    shortTitle = r.form.shortTitle,
                    isActive = true
                )
            )

            this.respond(HttpStatusCode.OK).done
        }
    }

    suspend fun editForm(call: ApplicationCall) {
        val perm = call.isModer
        call.dRes(perm, "Can't edit form") {
            val r = this.receive<REditFormReceive>()
            val oldForm = Forms.fetchById(r.id)

            Forms.update(r)

            if (oldForm.mentorLogin != r.form.mentorLogin) {
                val students = StudentsInForm.fetchStudentsLoginsByFormId(oldForm.formId)
                transaction {
                    Duty.update({
                        (Duty.mentorLogin eq oldForm.mentorLogin) and
                                (Duty.studentLogin.inList(students))
                    }) {
                        it[mentorLogin] = r.form.mentorLogin
                    }
                }
            }

            this.respond(HttpStatusCode.OK).done
        }
    }


    suspend fun deleteFormGroup(call: ApplicationCall) {

        val perm = call.isModer
        call.dRes(perm, "Can't delete formGroup") {
            val r = this.receive<RCreateFormGroupReceive>()
            FormGroups.delete(
                FormGroupDTO(
                    formId = r.formId,
                    groupId = r.groupId,
                    subjectId = r.subjectId
                )
            )
            this.respond(HttpStatusCode.OK).done
        }
    }

    suspend fun deleteStudentGroup(call: ApplicationCall) {

        val perm = call.isModer
        call.dRes(perm, "Can't delete studentGroup") {
            val r = this.receive<RCreateStudentGroupReceive>()
            StudentGroups.delete(
                StudentGroupDTO(
                    studentLogin = r.studentLogin,
                    groupId = r.groupId,
                    subjectId = r.subjectId
                )
            )
            this.respond(HttpStatusCode.OK).done
        }
    }

    suspend fun createStudentGroup(call: ApplicationCall) {
        val perm = call.isModer

        call.dRes(perm, "Can't create studentGroup") {
            val r = this.receive<RCreateStudentGroupReceive>()
            StudentGroups.insert(
                StudentGroupDTO(
                    studentLogin = r.studentLogin,
                    groupId = r.groupId,
                    subjectId = r.subjectId
                )
            )
            this.respond(HttpStatusCode.OK).done
        }
    }

    suspend fun createFormGroup(call: ApplicationCall) {
        val perm = call.isModer
        call.dRes(perm, "Can't create formGroup") {
            val r = this.receive<RCreateFormGroupReceive>()
            FormGroups.insert(
                FormGroupDTO(
                    formId = r.formId,
                    groupId = r.groupId,
                    subjectId = r.subjectId
                )
            )
            this.respond(HttpStatusCode.OK).done
        }
    }

    suspend fun fetchFormGroups(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch formGroups") {
            val r = this.receive<RFetchFormGroupsReceive>()
            val forms = FormGroups.getGroupsOfThisForm(r.formId)

            this.respond(
                RFetchFormGroupsResponse(forms.map { it.mapToFormGroup() })
            ).done
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
        val perm = call.isMember
        call.dRes(perm, "Can't fetch groups for students") {
            val r = this.receive<RFetchStudentGroupsReceive>()
            this.respond(
                RFetchStudentGroupsResponse(
                    StudentGroups.fetchGroupsOfStudent(r.studentLogin).map { it.mapToGroup() })
            ).done
        }
    }

    suspend fun fetchTeacherGroups(call: ApplicationCall) {
        val perm = call.isTeacher || call.isModer || call.isMentor
        call.dRes(perm, "Can't fetch teacher(cuted+) groups") {

            val groups = if (this.isModer) {
                Groups.getAllGroups().filter { it.isActive }
            } else if (this.isMentor) {
                val forms = Forms.fetchMentorForms(this.login)
                StudentsInForm.fetchStudentsLoginsByFormIds(forms.map { it.id })
                    .flatMap { s ->
                        StudentGroups.fetchGroupsOfStudent(s)
                    }.toSet().toList().filter { it.isActive }
            } else {
                Groups.getGroupsOfTeacher(this.login).filter { it.isActive }
            }
            this.respond(RFetchTeacherGroupsResponse(groups.map { it.mapToTeacherGroup(this.login) })).done
        }
    }

    suspend fun fetchStudentsInGroup(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch students in group") {
            val r = this.receive<RFetchStudentsInGroupReceive>()
            val students = StudentGroups.fetchStudentsOfGroup(
                groupId = r.groupId
            ).filter { it.isActive }
            val deletedLogins = if (r.date != null && r.lessonId != null) {
                ScheduleConflicts.fetchByDateAndLessonId(r.date!!, r.lessonId!!)?.logins ?: listOf()
            } else listOf()

            this.respond(
                RFetchStudentsInGroupResponse(
                    students.map {
                        PersonForGroup(
                            p = it,
                            isDeleted = it.login in deletedLogins
                        )
                    }
                )).done
        }
    }

    suspend fun fetchStudentsInForm(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch students in form") {
            val r = this.receive<RFetchStudentsInFormReceive>()
            this.respond(RFetchStudentsInFormResponse(fetchStudentsInFormNotCall(r.formId))).done
        }
    }

    suspend fun bindStudentToForm(call: ApplicationCall) {
        val perm = call.isModer
        call.dRes(perm, "Can't bind student to form") {
            val r = this.receive<RBindStudentToFormReceive>()
            StudentsInForm.insert(
                StudentInFormDTO(
                    formId = r.formId,
                    login = r.studentLogin
                )
            )
            this.respond(HttpStatusCode.OK).done
        }
    }

    suspend fun createSubject(call: ApplicationCall) {

        val perm = call.isModer

        call.dRes(perm, "Can't create subject") {
            val r = this.receive<RCreateSubjectReceive>()

            Subjects.insert(
                SubjectDTO(
                    name = r.name,
                    isActive = true
                )
            )

            this.respond(HttpStatusCode.OK).done
        }
    }

    suspend fun editSubject(call: ApplicationCall) {
        val perm = call.isModer
        call.dRes(perm, "Can't edit subject") {
            val r = this.receive<REditSubjectReceive>()

            Subjects.update(r.subjectId, r.name)

            this.respond(HttpStatusCode.OK).done
        }
    }

    suspend fun deleteSubject(call: ApplicationCall) {
        val perm = call.isModer
        call.dRes(perm, "Can't delete subject") {
            val r = this.receive<RDeleteSubject>()
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
            this.respond(HttpStatusCode.OK).done
        }
    }
}

fun fetchSchedule(
    isTeacher: Boolean, day: String, dayOfWeek: String,
    login: String
): List<ScheduleItem> {
    var items = Schedule.getOnDate(day)
    if (items.isEmpty()) {
        items = Schedule.getOnDate(dayOfWeek)
    }

    var deletedItems = ScheduleConflicts.fetchByDate(day)
    if (deletedItems.isEmpty()) {
        deletedItems = ScheduleConflicts.fetchByDate(dayOfWeek)
    }

    return if (isTeacher) {
        items.filter { it.teacherLogin == login }
    } else {
        val formId = StudentsInForm.fetchFormIdOfLogin(login)
        val idList = StudentGroups.fetchGroupsOfStudent(login)
//                    val parts = r.day.split(".")
//                    val date = "${parts[0]}.${parts[1]}.${parts[2]}"
//                    val marks = Marks.fetchUserByDate(login = call.login, date = date)
//                    val stups = Stups.fetchUserByDate(login = call.login, date = date)
        items.filter {
            ((it.groupId in idList.filter { it.isActive }.map { it.id }) ||
                    (it.groupId == -6 && it.custom.contains(login)) ||
                    (it.groupId in listOf(-11, 0) && it.formId == formId)) &&
                    it.index !in deletedItems.filter { login in it.logins }.map { it.lessonIndex }
        }
    }
}