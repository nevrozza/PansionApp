package com.nevrozq.pansion.features.user.manage

import FIO
import PersonParent
import admin.groups.forms.CutedForm
import admin.parents.RFetchParentsListResponse
import admin.parents.RUpdateParentsListReceive
import admin.users.RClearUserPasswordReceive
import admin.users.RCreateExcelStudentsReceive
import admin.users.RCreateUserResponse
import admin.users.RDeleteUserReceive
import admin.users.REditUserReceive
import admin.users.RFetchAllUsersResponse
import admin.users.RRegisterUserReceive
import com.nevrozq.pansion.database.forms.Forms
import com.nevrozq.pansion.database.parents.Parents
import com.nevrozq.pansion.database.parents.ParentsDTO
import com.nevrozq.pansion.database.studentsInForm.StudentInFormDTO
import com.nevrozq.pansion.database.studentsInForm.StudentsInForm
import com.nevrozq.pansion.database.subjects.Subjects
import com.nevrozq.pansion.database.tokens.FetchTokensResponse
import com.nevrozq.pansion.database.tokens.Tokens
import com.nevrozq.pansion.database.tokens.toFetchTokensResponse
import com.nevrozq.pansion.database.users.UserDTO
import com.nevrozq.pansion.database.users.Users
import com.nevrozq.pansion.database.users.mapToUser
import com.nevrozq.pansion.features.settings.DeleteTokenReceive
import com.nevrozq.pansion.utils.createLogin
import com.nevrozq.pansion.utils.isMember
import com.nevrozq.pansion.utils.isModer
import com.nevrozq.pansion.utils.isParent
import com.nevrozq.pansion.utils.login
import com.nevrozq.pansion.utils.toId
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import main.RFetchChildrenResponse
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.transactions.transaction
import server.Moderation
import server.Roles

class UserManageController() {

    suspend fun fetchAllParents(call: ApplicationCall) {
        if (call.isModer) {
            try {
                val users = Users.fetchAll().map {
                    PersonParent(
                        login = it.login,
                        fio = FIO(
                            name = it.name,
                            surname = it.surname,
                            praname = it.praname
                        ),
                        isActive = it.isActive,
                        isParent = it.isParent,
                        isStudent = it.role == Roles.student
                    )
                }.sortedBy { it.fio.surname }.sortedBy { !it.isActive }

                val lines = Parents.fetchAll()

                call.respond(
                    RFetchParentsListResponse(
                        users = users,
                        lines = lines
                    )
                )
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch all parentslines: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun updateParents(call: ApplicationCall) {
        if (call.isModer) {
            try {
                val r = call.receive<RUpdateParentsListReceive>()
                if (r.parentLogin == "0") {
                    if (r.id != 0) {
                        Parents.delete(r.id)
                    }
                } else if (r.studentLogin != "") {
                    Parents.insert(
                        ParentsDTO(
                            id = 0,
                            studentLogin = r.studentLogin,
                            parentLogin = r.parentLogin
                        )
                    )
                } else if (r.id != 0) {
                    Parents.update(
                        id = r.id,
                        parentLogin = r.parentLogin
                    )
                }


                fetchAllParents(call)
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't update parentslines: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun createUser(call: ApplicationCall) {
        val r = call.receive<RRegisterUserReceive>()
        if (call.isModer) {
            val login = createLogin(r.userInit.fio.name, r.userInit.fio.surname)
            var pLogins: MutableList<String>? = null
            try {
                Users.insert(
                    UserDTO(
                        login = login,
                        password = null,
                        name = r.userInit.fio.name,
                        surname = r.userInit.fio.surname,
                        praname = r.userInit.fio.praname,
                        birthday = r.userInit.birthday,
                        role = r.userInit.role,
                        moderation = r.userInit.moderation,
                        isParent = r.userInit.isParent,
                        avatarId = 0,
                        isActive = true,
                        subjectId = r.subjectId
                    )
                )

                if (r.parentFIOs != null) {
                    pLogins = mutableListOf()
                    r.parentFIOs!!.forEach { p ->
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
                        pLogins.add(pLogin)
                    }
                }
                if (r.formId != 0) {
                    StudentsInForm.insert(
                        StudentInFormDTO(
                            formId = r.formId,
                            login = login
                        )
                    )
                }

                call.respond(RCreateUserResponse(login, pLogins))
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "This User already exists")
            } catch (e: Throwable) {
                println(e)
                call.respond(HttpStatusCode.BadRequest, "Can't create user: ${e.localizedMessage}")
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun createExcelStudents(call: ApplicationCall) {
        val r = call.receive<RCreateExcelStudentsReceive>()
        if (call.isModer) {
            try {
                transaction {
                    println("STUDENTS: ${r.students}")
                    r.students.forEach { l ->
                        val studentPreviousLogin = Users.getLoginWithFIO(
                            fio = FIO(
                                name = l.user.fio.name.replace("ë", "ё"),
                                surname = l.user.fio.surname.replace("ë", "ё"),
                                praname = l.user.fio.praname?.replace("ë", "ё")
                            ),
                            itShouldBeStudent = true
                        )
                        var student: UserDTO? = null
                        var login: String? = null
                        println("GOT IT: ${studentPreviousLogin == null} ${Users.fetchUser(studentPreviousLogin ?: "")?.birthday != l.user.birthday}")
                        if (studentPreviousLogin == null || Users.fetchUser(studentPreviousLogin)?.birthday != l.user.birthday) {

                            login =
                                createLogin(name = l.user.fio.name, surname = l.user.fio.surname)
                            student = UserDTO(
                                login = login,
                                password = null,
                                name = l.user.fio.name.replace("ë", "ё"),
                                surname = l.user.fio.surname.replace("ë", "ё"),
                                praname = l.user.fio.praname?.replace("ë", "ё"),
                                birthday = l.user.birthday,
                                role = l.user.role,
                                moderation = l.user.moderation,
                                isParent = l.user.isParent,
                                avatarId = 1,
                                isActive = true,
                                subjectId = null
                            )
                        }
                        l.parents.forEach { pp ->
                            val p = pp.first.split(" ")
                            val pFio = FIO(
                                name = p[1].replace("ë", "ё"),
                                surname = p[0].replace("ë", "ё"),
                                praname = (p.getOrNull(2) + (if (p.getOrNull(3) != null) " "+p.getOrNull(3) else "")).replace("ë", "ё")
                            )

                            val parentPreviousLogin = Users.getLoginWithFIO(
                                fio = FIO(
                                    name = l.user.fio.name.replace("ë", "ё"),
                                    surname = l.user.fio.surname.replace("ë", "ё"),
                                    praname = l.user.fio.praname?.replace("ë", "ё")
                                ),
                                itShouldBeStudent = false
                            )
                            var pLogin: String? = null
                            if (parentPreviousLogin == null || Users.fetchUser(parentPreviousLogin)?.birthday != pp.second) {
                                pLogin = createLogin(pFio.name, pFio.surname, 1)
                                val parent = UserDTO(
                                    login = pLogin,
                                    password = null,
                                    name = pFio.name,
                                    surname = pFio.surname,
                                    praname = pFio.praname,
                                    birthday = pp.second.replace(".", ""),
                                    role = Roles.nothing,
                                    moderation = Moderation.nothing,
                                    isParent = true,
                                    avatarId = 1,
                                    isActive = true,
                                    subjectId = null
                                )

                                Users.insert(
                                    listOf(student, parent)
                                        .mapNotNull { it }
                                )
                            }
                            if (login != null || pLogin != null) {
                                Parents.insert(
                                    ParentsDTO(
                                        id = 0,
                                        studentLogin = login ?: studentPreviousLogin!!,
                                        parentLogin = pLogin ?: parentPreviousLogin!!
                                    )
                                )
                            }
                        }
                        if (l.formId != 0 && login != null) {
                            StudentsInForm.insert(
                                StudentInFormDTO(
                                    formId = l.formId,
                                    login = login
                                )
                            )
                        }
                    }
                }

                call.respond(HttpStatusCode.OK)
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "This User already exists")
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.BadRequest, "Can't create user: ${e.localizedMessage}")
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchAllUsers(call: ApplicationCall) {
        if (call.isMember) {
            try {
                val users = Users.fetchAll()
                val forms = Forms.getAllForms().mapNotNull {
                    if (it.isActive) {
                        CutedForm(
                            id = it.formId,
                            title = it.title,
                            classNum = it.classNum
                        )
                    } else null
                }
                val subjects = Subjects.fetchAllActiveSubjectsAsMap()
                call.respond(
                    RFetchAllUsersResponse(
                        users.map { it.mapToUser() }.sortedBy { it.user.fio.surname },
                        forms,
                        subjects = subjects
                    )
                )
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch all users: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchChildren(call: ApplicationCall) {
        if (call.isParent) {
            try {
                val children = Parents.fetchChildren(call.login)
                call.respond(RFetchChildrenResponse(children))
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch children: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun performSearchTokens(call: ApplicationCall) {
        val token = call.request.headers["Bearer-Authorization"].toId()
        if (Tokens.isTokenValid(token)) {
            val login = Tokens.getLoginOfThisToken(token)
            call.respond(
                FetchTokensResponse(
                    Tokens.getTokensOfThisLogin(login).map { it.toFetchTokensResponse() }
                )
            )
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Token expired")
        }
    }

    suspend fun deleteToken(call: ApplicationCall) {
        val token = call.request.headers["Bearer-Authorization"].toId()

        val deleteTokenReceive = call.receive<DeleteTokenReceive>()
        if (Tokens.isTokenValid(token)) {
            val login = Tokens.getLoginOfThisToken(token)
            if (Tokens.getTokensOfThisLogin(login).any { it.deviceId == deleteTokenReceive.id }) {
                Tokens.deleteTokenByIdAndLogin(deleteTokenReceive.id, login)
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid id")
            }
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Token expired")
        }
    }

//    suspend fun fetchAllUsersByClass(call: ApplicationCall) {
//        val formId = call.receive<RFetchStudentsInFormReceive>().formId
//        if (call.isMember) {
//            try {
//                val students = Users.fetchStude(formId)
//
//                call.respond(RFetchStudentsInFormResponse(students))
//            } catch (e: Throwable) {
//                call.respond(
//                    HttpStatusCode.BadRequest,
//                    "Can't fetch teachers: ${e.localizedMessage}"
//                )
//            }
//        } else {
//            call.respond(HttpStatusCode.Forbidden, "No permission")
//        }
//    }

    suspend fun performEditUser(call: ApplicationCall) {
        val r = call.receive<REditUserReceive>()
        if (call.isModer) {
            try {
                Users.update(
                    login = r.login,
                    newName = r.user.fio.name,
                    newSurname = r.user.fio.surname,
                    newPraname = r.user.fio.praname,
                    newBirthday = r.user.birthday,
                    newRole = r.user.role,
                    newModeration = r.user.moderation,
                    newIsParent = r.user.isParent,
                    newSubjectId = r.subjectId
                )
                call.respond(HttpStatusCode.OK)
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "SQL Conflict")
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.BadRequest, "Can't edit user: ${e.localizedMessage}")
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun performDeleteUser(call: ApplicationCall) {
        val r = call.receive<RDeleteUserReceive>()
        if (call.isModer) {
            try {
                Users.update(
                    login = r.login,
                    newName = r.user.fio.name,
                    newSurname = r.user.fio.surname,
                    newPraname = r.user.fio.praname,
                    newBirthday = r.user.birthday,
                    newRole = r.user.role,
                    newModeration = r.user.moderation,
                    newIsParent = r.user.isParent,
                    newIsActive = false,
                    newSubjectId = null
                )
                call.respond(HttpStatusCode.OK)
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "SQL Conflict")
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.BadRequest, "Can't delete user: ${e.localizedMessage}")
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun clearUserPassword(call: ApplicationCall) {
        val r = call.receive<RClearUserPasswordReceive>()
        if (call.isModer) {
            try {
                Users.clearPassword(r.login)
                Tokens.deleteTokenByLogin(r.login)
                call.respond(HttpStatusCode.OK)
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "SQL Conflict")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't clear user password: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }
}