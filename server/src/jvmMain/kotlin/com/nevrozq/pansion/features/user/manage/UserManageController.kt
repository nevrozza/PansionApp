package com.nevrozq.pansion.features.user.manage

import admin.ClearUserPasswordReceive
import admin.ClearUserPasswordResponse
import admin.EditUserReceive
import admin.EditUserResponse
import admin.FetchAllStudentsByClassReceive
import admin.FetchAllStudentsByClassResponse
import admin.FetchAllUsersResponse
import server.Moderation
import admin.RegisterReceive
import admin.RegisterResponse
import com.nevrozq.pansion.database.groups.Groups
import com.nevrozq.pansion.database.tokens.Tokens
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import com.nevrozq.pansion.database.users.UserDTO
import com.nevrozq.pansion.database.users.Users
import com.nevrozq.pansion.utils.createLogin
import com.nevrozq.pansion.utils.toId
import org.jetbrains.exposed.sql.select

class UserManageController() {
    suspend fun registerNewUser(call: ApplicationCall) {
        val registerReceive = call.receive<RegisterReceive>()
        val token = call.request.headers["Bearer-Authorization"]
        val moderation = Users.getModeration(Tokens.getLoginOfThisToken(token.toId()))
        //Users.fetchUser(---login)
        //admin.User already exists
        //else {
        if (moderation != Moderation.mentor && moderation != Moderation.nothing) {
            val login = createLogin(registerReceive.name, registerReceive.surname)

            try {
                Users.insert(
                    UserDTO(
                        login = login,
                        password = null,
                        name = registerReceive.name,
                        surname = registerReceive.surname,
                        praname = registerReceive.praname,
                        birthday = registerReceive.birthday,
                        role = registerReceive.role,
                        moderation = registerReceive.moderation,
                        isParent = registerReceive.isParent,
                        avatarId = 0
                    )
                )

                call.respond(RegisterResponse(login))
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "admin.User already exists")
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.BadRequest, "Can't create user: ${e.localizedMessage}")
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchAllUsers(call: ApplicationCall) {
        val token = call.request.headers["Bearer-Authorization"]

        if (Tokens.getIsMember(token.toId())) {
            try {
                val users = Users.fetchAll()

                call.respond(FetchAllUsersResponse(users))
            }
            catch (e: Throwable) {
                call.respond(HttpStatusCode.BadRequest, "Can't fetch teachers: ${e.localizedMessage}")
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }
    suspend fun fetchAllUsersByClass(call: ApplicationCall) {
        val token = call.request.headers["Bearer-Authorization"]
        val classNum = call.receive<FetchAllStudentsByClassReceive>().classNum
        if (Tokens.getIsMember(token.toId())) {
            try {
                val students = Users.fetchStudentsByClass(classNum)

                call.respond(FetchAllStudentsByClassResponse(students))
            }
            catch (e: Throwable) {
                call.respond(HttpStatusCode.BadRequest, "Can't fetch teachers: ${e.localizedMessage}")
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun performEditUser(call: ApplicationCall) {
        val editUserReceive = call.receive<EditUserReceive>()
        val token = call.request.headers["Bearer-Authorization"]
        val moderation = Users.getModeration(Tokens.getLoginOfThisToken(token.toId()))

        if (moderation != Moderation.mentor && moderation != Moderation.nothing) {
            try {
                Users.update(
                    login = editUserReceive.login,
                    newName = editUserReceive.name,
                    newSurname = editUserReceive.surname,
                    newPraname = editUserReceive.praname,
                    newBirthday = editUserReceive.birthday,
                    newRole = editUserReceive.role,
                    newModeration = editUserReceive.moderation,
                    newIsParent = editUserReceive.isParent
                )
                call.respond(EditUserResponse(true))
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "SQL Conflict")
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.BadRequest, "Can't edit user: ${e.localizedMessage}")
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun clearUserPassword(call: ApplicationCall) {
        val editUserReceive = call.receive<ClearUserPasswordReceive>()
        val token = call.request.headers["Bearer-Authorization"]
        val moderation = Users.getModeration(Tokens.getLoginOfThisToken(token.toId()))

        if (moderation != Moderation.mentor && moderation != Moderation.nothing) {
            try {
                Users.clearPassword(editUserReceive.login)
                Tokens.deleteTokenByLogin(editUserReceive.login)
                call.respond(ClearUserPasswordResponse(true))
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "SQL Conflict")
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.BadRequest, "Can't -password user: ${e.localizedMessage}")
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }
}