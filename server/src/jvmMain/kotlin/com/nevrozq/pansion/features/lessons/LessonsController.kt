package com.nevrozq.pansion.features.lessons

import admin.CreateNewGSubjectReceive
import admin.CreateNewGSubjectResponse
import admin.CreateNewGroupReceive
import admin.CreateNewGroupResponse
import admin.FetchAllGSubjectsResponse
import admin.FetchAllTeachersForGroupsResponse
import admin.FetchSubjectGroupsReceive
import admin.FetchSubjectGroupsResponse
import admin.AdultForGroup
import admin.CreateFormGroupsReceive
import admin.CreateFormGroupsResponse
import admin.CreateNewFormReceive
import admin.CreateNewFormResponse
import admin.CreateUserFormReceive
import admin.CreateUserFormResponse
import admin.FetchAllFormsResponse
import admin.FetchAllMentorsForGroupsResponse
import admin.FetchFormGroupsOfSubjectReceive
import admin.FetchFormGroupsOfSubjectResponse
import admin.FetchFormGroupsReceive
import admin.FetchFormGroupsResponse
import admin.FetchStudentGroupsOfStudentReceive
import admin.FetchStudentGroupsOfStudentResponse
import admin.FetchStudentsInFormReceive
import admin.FetchStudentsInFormResponse
import admin.Student
import com.nevrozq.pansion.database.defaultGroupsForms.DefaultGroupFormDTO
import com.nevrozq.pansion.database.defaultGroupsForms.DefaultGroupsForms
import com.nevrozq.pansion.database.forms.Forms
import com.nevrozq.pansion.database.forms.FormsDTO
import com.nevrozq.pansion.database.groups.Groups
import com.nevrozq.pansion.database.groups.GroupsDTO
import com.nevrozq.pansion.database.studentLessons.StudentGroups
import com.nevrozq.pansion.database.subjects.GSubjects
import com.nevrozq.pansion.database.subjects.GSubjectsDTO
import com.nevrozq.pansion.database.tokens.Tokens
import com.nevrozq.pansion.database.userForms.UserForms
import com.nevrozq.pansion.database.userForms.UserFormsDTO
import com.nevrozq.pansion.database.users.Users
import com.nevrozq.pansion.utils.isTeacher
import com.nevrozq.pansion.utils.toId
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import journal.init.FetchStudentsInGroupReceive
import journal.init.FetchStudentsInGroupResponse
import journal.init.FetchTeacherGroupsResponse
import org.jetbrains.exposed.exceptions.ExposedSQLException
import server.Moderation

class LessonsController() {
    suspend fun fetchAllSubjects(call: ApplicationCall) {
        val token = call.request.headers["Bearer-Authorization"]

        if (Tokens.getIsMember(token.toId())) {
            try {
                val gSubjects = GSubjects.getSubjects()

                call.respond(
                    FetchAllGSubjectsResponse(gSubjects)
                )
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch gSubjects: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchAllTeachersForGroups(call: ApplicationCall) {
        val token = call.request.headers["Bearer-Authorization"]

        if (Tokens.getIsMember(token.toId())) {
            try {
                val mentors = Users.fetchAllTeachers()

                call.respond(FetchAllTeachersForGroupsResponse(
                    mentors.map {
                        AdultForGroup(
                            login = it.login,
                            name = it.name,
                            surname = it.surname,
                            praname = it.praname
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
        val token = call.request.headers["Bearer-Authorization"]

        if (Tokens.getIsMember(token.toId())) {
            try {
                val mentors = Users.fetchAllMentors()
                call.respond(FetchAllMentorsForGroupsResponse(
                    mentors.map {
                        AdultForGroup(
                            login = it.login,
                            name = it.name,
                            surname = it.surname,
                            praname = it.praname
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

    suspend fun fetchSubjectGroupsButFormGroup(call: ApplicationCall) {
        val token = call.request.headers["Bearer-Authorization"]
        val fetchGroupsReceive = call.receive<FetchFormGroupsOfSubjectReceive>()
        if (Tokens.getIsMember(token.toId())) {
            try {
                val groups = Groups.getGroupsOfGSubjectButFormGroup(fetchGroupsReceive.subjectId)

                call.respond(
                    FetchFormGroupsOfSubjectResponse(groups)
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

    suspend fun fetchSubjectGroups(call: ApplicationCall) {
        val token = call.request.headers["Bearer-Authorization"]
        val fetchGroupsReceive = call.receive<FetchSubjectGroupsReceive>()
        if (Tokens.getIsMember(token.toId())) {
            try {
                val groups = Groups.getGroupsOfGSubject(fetchGroupsReceive.id)

                call.respond(
                    FetchSubjectGroupsResponse(groups)
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
        val token = call.request.headers["Bearer-Authorization"]
        if (Tokens.getIsMember(token.toId())) {
            try {
                val forms = Forms.getAllForms()

                call.respond(
                    FetchAllFormsResponse(forms)
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

    suspend fun createNewGroup(call: ApplicationCall) {
        val createGroupReceive = call.receive<CreateNewGroupReceive>()
        val token = call.request.headers["Bearer-Authorization"]
        val moderation = Users.getModeration(Tokens.getLoginOfThisToken(token.toId()))
        //Users.fetchUser(---login)
        //admin.User already exists
        //else {
        if (moderation != Moderation.mentor && moderation != Moderation.nothing) {
            try {
                Groups.insert(
                    GroupsDTO(
                        name = createGroupReceive.name,
                        teacherLogin = createGroupReceive.mentorLogin,
                        gSubjectId = createGroupReceive.gSubjectId,
                        difficult = createGroupReceive.difficult,
                        isActivated = true
                    )
                )

                call.respond(CreateNewGroupResponse(Groups.getGroupsOfGSubject(gSubjectId = createGroupReceive.gSubjectId)))
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
    suspend fun createNewForm(call: ApplicationCall) {
        val createFormReceive = call.receive<CreateNewFormReceive>()
        val token = call.request.headers["Bearer-Authorization"]
        val moderation = Users.getModeration(Tokens.getLoginOfThisToken(token.toId()))
        //Users.fetchUser(---login)
        //admin.User already exists
        //else {
        if (moderation != Moderation.mentor && moderation != Moderation.nothing) {
            try {
                Forms.insert(
                    FormsDTO(
                        name = createFormReceive.name,
                        classNum = createFormReceive.classNum,
                        mentorLogin = createFormReceive.mentorLogin,
                        shortName = createFormReceive.shortName,
                        isActivated = true
                    )
                )

                call.respond(CreateNewFormResponse(Forms.getAllForms()))
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

    suspend fun createNewFormGroup(call: ApplicationCall) {
        val createFormGroupReceive = call.receive<CreateFormGroupsReceive>()
        val token = call.request.headers["Bearer-Authorization"]
        val moderation = Users.getModeration(Tokens.getLoginOfThisToken(token.toId()))
        //Users.fetchUser(---login)
        //admin.User already exists
        //else {
        if (moderation != Moderation.mentor && moderation != Moderation.nothing) {
            try {
                DefaultGroupsForms.insert(
                    DefaultGroupFormDTO(
                        formId = createFormGroupReceive.formId,
                        groupId = createFormGroupReceive.groupId,
                        subjectId = createFormGroupReceive.subjectId
                    )
                )
                val groups = DefaultGroupsForms.getGroupsOfThisForm(createFormGroupReceive.formId)
                call.respond(CreateFormGroupsResponse(groups))
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
        val token = call.request.headers["Bearer-Authorization"]
        val receive = call.receive<FetchFormGroupsReceive>()
        if (Tokens.getIsMember(token.toId())) {
            try {
                val forms = DefaultGroupsForms.getGroupsOfThisForm(receive.formId)

                call.respond(
                    FetchFormGroupsResponse(forms)
                )
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch groups: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    private fun fetchStudentsInFormNotCall(formId: Int): List<Student> {
        if(formId == 0) {
            val loginsWithGroups = UserForms.fetchAllStudentsLogins()
            val students = Users.fetchAllStudents().filter { it.login !in loginsWithGroups }
            val response = students.map {
                Student(
                    login = it.login,
                    name = it.name,
                    surname = it.surname,
                    praname = it.praname
                )
            }
            return response
        } else {
            val logins = UserForms.getStudentLoginsInForm(formId = formId)
            val response = logins.map {
                val user = Users.fetchUser(it)
                if (user == null) {
                    null
                } else {
                    Student(
                        login = user.login,
                        name = user.name,
                        surname = user.surname,
                        praname = user.praname
                    )
                }
            }
            return response.filterNotNull()
        }
    }

    suspend fun fetchStudentGroups(call: ApplicationCall) {
        val token = call.request.headers["Bearer-Authorization"]
        val receive = call.receive<FetchStudentGroupsOfStudentReceive>()
        if (Tokens.getIsMember(token.toId())) {
            try {
                call.respond(FetchStudentGroupsOfStudentResponse(StudentGroups.getGroupsOfStudent(receive.studentLogin)))
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch groups: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }
    suspend fun fetchTeacherGroups(call: ApplicationCall) {
        val login = Tokens.getLoginOfThisToken(call.request.headers["Bearer-Authorization"].toId())
        if (login.isTeacher()) {
            try {
                val groups = Groups.getGroupsOfTeacher(login)
                call.respond(FetchTeacherGroupsResponse(groups))
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch groups: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchStudentsInGroup(call: ApplicationCall) {
        val token = call.request.headers["Bearer-Authorization"]
        val receive = call.receive<FetchStudentsInGroupReceive>()
        if (Tokens.getIsMember(token.toId())) {
            try {
                val students = StudentGroups.getStudentsOfGroup(
                    groupId = receive.groupId
                )
                call.respond(FetchStudentsInGroupResponse(students))
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch groups: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchStudentsInForm(call: ApplicationCall) {
        val token = call.request.headers["Bearer-Authorization"]
        val receive = call.receive<FetchStudentsInFormReceive>()
        if (Tokens.getIsMember(token.toId())) {
            try {
                call.respond(FetchStudentsInFormResponse(fetchStudentsInFormNotCall(receive.formId)))
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch groups: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun createUserForm(call: ApplicationCall) {
        val receive = call.receive<CreateUserFormReceive>()
        val token = call.request.headers["Bearer-Authorization"]
        val moderation = Users.getModeration(Tokens.getLoginOfThisToken(token.toId()))
        //Users.fetchUser(---login)
        //admin.User already exists
        //else {
        if (moderation != Moderation.mentor && moderation != Moderation.nothing) {
            try {
                UserForms.insert(
                    UserFormsDTO(
                        formId = receive.hisFormId,
                        login = receive.studentLogin
                    )
                )
                val students = fetchStudentsInFormNotCall(receive.currentFormId)
                call.respond(CreateUserFormResponse(students))
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

    suspend fun createNewGSubject(call: ApplicationCall) {
        val createGSubjectReceive = call.receive<CreateNewGSubjectReceive>()
        val token = call.request.headers["Bearer-Authorization"]
        val moderation = Users.getModeration(Tokens.getLoginOfThisToken(token.toId()))
        //Users.fetchUser(---login)
        //admin.User already exists
        //else {
        if (moderation != Moderation.mentor && moderation != Moderation.nothing) {
            try {
                GSubjects.insert(
                    GSubjectsDTO(
                        name = createGSubjectReceive.name,
                        isActivated = true
                    )
                )

                call.respond(CreateNewGSubjectResponse(GSubjects.getSubjects()))
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


//    suspend fun registerNewUser(call: ApplicationCall) {
//        val registerReceive = call.receive<RegisterReceive>()
//
//        val moderation = Users.getModeration(Tokens.getLoginOfThisToken(registerReceive.token.toId()))
//        //Users.fetchUser(---login)
//        //admin.User already exists
//        //else {
//        if (moderation != Moderation.mentor && moderation != Moderation.nothing) {
//            val login = createLogin(registerReceive.name, registerReceive.surname)
//
//            try {
//                Users.insert(
//                    UserDTO(
//                        login = login,
//                        password = null,
//                        name = registerReceive.name,
//                        surname = registerReceive.surname,
//                        praname = registerReceive.praname,
//                        birthday = registerReceive.birthday,
//                        role = registerReceive.role,
//                        moderation = registerReceive.moderation,
//                        isParent = registerReceive.isParent,
//                        avatarId = 0
//                    )
//                )
//
//                call.respond(RegisterResponse(login))
//            } catch (e: ExposedSQLException) {
//                call.respond(HttpStatusCode.Conflict, "admin.User already exists")
//            } catch (e: Throwable) {
//                call.respond(HttpStatusCode.BadRequest, "Can't create user: ${e.localizedMessage}")
//            }
//        } else {
//            call.respond(HttpStatusCode.Forbidden, "No permission")
//        }
//    }
//
//    suspend fun fetchAllUsers(call: ApplicationCall) {
//        val fetchAllUsersReceive = call.receive<FetchAllUsersReceive>()
//
//        if (Users.getIsMember(Tokens.getLoginOfThisToken(fetchAllUsersReceive.token.toId()))) {
//            try {
//                val teachers = Users.fetchAllSubjects()
//
//                call.respond(FetchAllUsersResponse(teachers))
//            }
//            catch (e: Throwable) {
//                call.respond(HttpStatusCode.BadRequest, "Can't fetch teachers: ${e.localizedMessage}")
//            }
//        } else {
//            call.respond(HttpStatusCode.Forbidden, "No permission")
//        }
//    }
//
//    suspend fun performEditUser(call: ApplicationCall) {
//        val editUserReceive = call.receive<EditUserReceive>()
//
//        val moderation = Users.getModeration(Tokens.getLoginOfThisToken(editUserReceive.token.toId()))
//
//        if (moderation != Moderation.mentor && moderation != Moderation.nothing) {
//            try {
//                Users.update(
//                    login = editUserReceive.login,
//                    newName = editUserReceive.name,
//                    newSurname = editUserReceive.surname,
//                    newPraname = editUserReceive.praname,
//                    newBirthday = editUserReceive.birthday,
//                    newRole = editUserReceive.role,
//                    newModeration = editUserReceive.moderation,
//                    newIsParent = editUserReceive.isParent
//                )
//                call.respond(EditUserResponse(true))
//            } catch (e: ExposedSQLException) {
//                call.respond(HttpStatusCode.Conflict, "SQL Conflict")
//            } catch (e: Throwable) {
//                call.respond(HttpStatusCode.BadRequest, "Can't edit user: ${e.localizedMessage}")
//            }
//        } else {
//            call.respond(HttpStatusCode.Forbidden, "No permission")
//        }
//    }
//
//    suspend fun clearUserPassword(call: ApplicationCall) {
//        val editUserReceive = call.receive<ClearUserPasswordReceive>()
//
//        val moderation = Users.getModeration(Tokens.getLoginOfThisToken(editUserReceive.token.toId()))
//
//        if (moderation != Moderation.mentor && moderation != Moderation.nothing) {
//            try {
//                Users.clearPassword(editUserReceive.login)
//                Tokens.deleteTokenByLogin(editUserReceive.login)
//                call.respond(ClearUserPasswordResponse(true))
//            } catch (e: ExposedSQLException) {
//                call.respond(HttpStatusCode.Conflict, "SQL Conflict")
//            } catch (e: Throwable) {
//                call.respond(HttpStatusCode.BadRequest, "Can't -password user: ${e.localizedMessage}")
//            }
//        } else {
//            call.respond(HttpStatusCode.Forbidden, "No permission")
//        }
//    }
}