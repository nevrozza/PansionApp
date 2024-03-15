package com.nevrozq.pansion.features.lessons

import FIO
import Person
import admin.groups.forms.RCreateFormGroupReceive
import admin.groups.subjects.RCreateGroupReceive
import admin.groups.subjects.topBar.RFetchAllSubjectsResponse
import admin.groups.subjects.RFetchTeachersResponse
import admin.groups.forms.outside.CreateFormReceive
import admin.groups.students.RBindStudentToFormReceive
import admin.groups.forms.outside.RFetchFormsResponse
import admin.groups.forms.outside.RFetchMentorsResponse
import admin.groups.subjects.RFetchGroupsReceive
import admin.groups.forms.RFetchFormGroupsReceive
import admin.groups.forms.RFetchFormGroupsResponse
import admin.groups.students.deep.RFetchStudentGroupsReceive
import admin.groups.students.deep.RFetchStudentGroupsResponse
import admin.groups.students.RFetchStudentsInFormReceive
import admin.groups.students.RFetchStudentsInFormResponse
import admin.groups.forms.RFetchCutedGroupsResponse
import admin.groups.subjects.RFetchGroupsResponse
import admin.groups.subjects.topBar.RCreateSubjectReceive
import com.nevrozq.pansion.database.formGroups.FormGroupDTO
import com.nevrozq.pansion.database.formGroups.FormGroups
import com.nevrozq.pansion.database.formGroups.mapToFormGroup
import com.nevrozq.pansion.database.forms.Forms
import com.nevrozq.pansion.database.forms.FormDTO
import com.nevrozq.pansion.database.forms.mapToForm
import com.nevrozq.pansion.database.groups.Groups
import com.nevrozq.pansion.database.groups.GroupDTO
import com.nevrozq.pansion.database.groups.mapToCutedGroup
import com.nevrozq.pansion.database.groups.mapToGroup
import com.nevrozq.pansion.database.groups.mapToTeacherGroup
import com.nevrozq.pansion.database.studentGroups.StudentGroups
import com.nevrozq.pansion.database.subjects.Subjects
import com.nevrozq.pansion.database.subjects.SubjectDTO
import com.nevrozq.pansion.database.studentsInForm.StudentsInForm
import com.nevrozq.pansion.database.studentsInForm.StudentInFormDTO
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

class LessonsController() {
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
                call.respond(RFetchMentorsResponse(
                    mentors.filter { it.isActive }.map {
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
                    "Can't fetch mentors: ${e.localizedMessage}"
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