package com.nevrozq.pansion.features.lessons

import FIO
import Person
import admin.cabinets.CabinetItem
import admin.cabinets.RFetchCabinetsResponse
import admin.cabinets.RUpdateCabinetsReceive
import admin.groups.forms.RCreateFormGroupReceive
import admin.groups.forms.RFetchCutedGroupsResponse
import admin.groups.forms.RFetchFormGroupsReceive
import admin.groups.forms.RFetchFormGroupsResponse
import admin.groups.forms.outside.CreateFormReceive
import admin.groups.forms.outside.RFetchFormsResponse
import admin.groups.forms.outside.RFetchMentorsResponse
import admin.groups.students.RBindStudentToFormReceive
import admin.groups.students.RFetchStudentsInFormReceive
import admin.groups.students.RFetchStudentsInFormResponse
import admin.groups.students.deep.RCreateStudentGroupReceive
import admin.groups.students.deep.RFetchStudentGroupsReceive
import admin.groups.students.deep.RFetchStudentGroupsResponse
import admin.groups.subjects.RCreateGroupReceive
import admin.groups.subjects.RFetchGroupsReceive
import admin.groups.subjects.RFetchGroupsResponse
import admin.groups.subjects.RFetchTeachersResponse
import admin.groups.subjects.topBar.RCreateSubjectReceive
import admin.groups.subjects.topBar.RFetchAllSubjectsResponse
import admin.schedule.RFetchInitScheduleResponse
import admin.schedule.ScheduleGroup
import admin.schedule.SchedulePerson
import admin.schedule.ScheduleSubject
import com.nevrozq.pansion.database.cabinets.Cabinets
import com.nevrozq.pansion.database.cabinets.CabinetsDTO
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
import com.nevrozq.pansion.database.ratingEntities.Marks
import com.nevrozq.pansion.database.ratingEntities.Stups
import com.nevrozq.pansion.database.ratingTable.RatingWeekTable
import com.nevrozq.pansion.database.schedule.Schedule
import com.nevrozq.pansion.database.schedule.ScheduleDTO
import com.nevrozq.pansion.database.studentGroups.StudentGroupDTO
import com.nevrozq.pansion.database.studentGroups.StudentGroups
import com.nevrozq.pansion.database.studentsInForm.StudentInFormDTO
import com.nevrozq.pansion.database.studentsInForm.StudentsInForm
import com.nevrozq.pansion.database.subjects.SubjectDTO
import com.nevrozq.pansion.database.subjects.Subjects
import com.nevrozq.pansion.database.subjects.mapToSubject
import com.nevrozq.pansion.database.users.Users
import com.nevrozq.pansion.utils.isMember
import com.nevrozq.pansion.utils.isModer
import com.nevrozq.pansion.utils.isTeacher
import com.nevrozq.pansion.utils.login
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import journal.init.RFetchStudentsInGroupReceive
import journal.init.RFetchStudentsInGroupResponse
import journal.init.RFetchTeacherGroupsResponse
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import rating.RFetchScheduleSubjectsResponse
import rating.RFetchSubjectRatingReceive
import rating.RFetchSubjectRatingResponse
import rating.RatingItem
import schedule.PersonScheduleItem
import schedule.RFetchScheduleDateReceive
import schedule.RPersonScheduleList
import schedule.RScheduleList

class LessonsController() {


    suspend fun fetchRating(call: ApplicationCall) {
        if (call.isMember) {
            val r = call.receive<RFetchSubjectRatingReceive>()
            try {
                val table = when (r.period) {
//                    0 -> RatingWeekTable
//                    1 -> RatingModuleTable
//                    2 -> RatingYearTable
                    else -> RatingWeekTable
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
                    )
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
                    RFetchScheduleSubjectsResponse(subjects.map {
                        ScheduleSubject(
                            id = it.id,
                            name = it.name
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
            val r = call.receive<RFetchScheduleDateReceive>()

            try {
                var items = Schedule.getOnDate(r.day)
                if (items.isEmpty()) {
                    items = Schedule.getOnDate(r.dayOfWeek)
                }

                items = if (call.isTeacher) {
                    items.filter { it.teacherLogin == call.login }
                } else {
                    val idList = StudentGroups.fetchGroupsOfStudent(call.login)

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
                    val teacher =
                        teachers.firstOrNull { teacher -> teacher.login == it.teacherLogin }
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
                            teacherFio = fio
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
                            cabinet = it.cabinet.toString()
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

                val tt = Users.fetchAllTeachers().filter { it.isActive }
                val ss = Users.fetchAllStudents().filter { it.isActive }
                val gg = Groups.getAllGroups().filter { it.isActive }
                val gs = StudentGroups.fetchAll()
                val subjects = Subjects.fetchAllSubjects()

                tt.forEach { t ->
                    val groups = gg.filter { it.teacherLogin == t.login }.map { it.id }
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
                            val id = it.groupId
                            gg.first { it.id == id }.isActive
                        }.map { it.groupId }

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
                        subjects = subjects.filter { it.isActive }.map {
                            ScheduleSubject(
                                id = it.id,
                                name = it.name
                            )
                        }
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
        if (call.isTeacher) {
            try {
                val groups = Groups.getGroupsOfTeacher(call.login)
                call.respond(RFetchTeacherGroupsResponse(groups.map { it.mapToTeacherGroup() }))
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
                )
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
}