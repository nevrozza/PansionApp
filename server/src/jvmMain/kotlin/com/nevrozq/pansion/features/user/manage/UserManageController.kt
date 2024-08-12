package com.nevrozq.pansion.features.user.manage

import admin.groups.students.RFetchStudentsInFormReceive
import admin.groups.students.RFetchStudentsInFormResponse
import admin.users.RClearUserPasswordReceive
import admin.users.REditUserReceive
import server.Moderation
import admin.users.RRegisterUserReceive
import admin.users.RCreateUserResponse
import admin.users.RDeleteUserReceive
import admin.users.RFetchAllUsersResponse
import com.nevrozq.pansion.database.parents.Parents
import com.nevrozq.pansion.database.parents.ParentsDTO
import com.nevrozq.pansion.database.tokens.Tokens
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import com.nevrozq.pansion.database.users.UserDTO
import com.nevrozq.pansion.database.users.Users
import com.nevrozq.pansion.database.users.mapToUser
import com.nevrozq.pansion.utils.createLogin
import com.nevrozq.pansion.utils.isMember
import com.nevrozq.pansion.utils.isModer
import server.Roles

class UserManageController() {
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
                        isActive = true
                    )
                )

                if(r.parentFIOs != null) {
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
                        pLogins.add(pLogin)
                    }
                }


                call.respond(RCreateUserResponse(login, pLogins))
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
                call.respond(RFetchAllUsersResponse(users.map { it.mapToUser() }.sortedBy { it.user.fio.surname }))
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
                    newIsParent = r.user.isParent
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
                    newIsActive = false
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