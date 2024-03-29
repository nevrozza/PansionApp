package com.nevrozq.pansion.features.auth

import FIO
import admin.users.UserInit
import auth.ActivationReceive
import auth.ActivationResponse
import auth.CheckActivationReceive
import auth.CheckActivationResponse
import auth.LoginReceive
import auth.LoginResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.exceptions.ExposedSQLException
import com.nevrozq.pansion.database.tokens.TokenDTO
import com.nevrozq.pansion.database.tokens.Tokens
import com.nevrozq.pansion.database.users.Users
import com.nevrozq.pansion.utils.cut
import com.nevrozq.pansion.utils.nullUUID
import com.nevrozq.pansion.utils.toId
import server.DataLength
import java.util.UUID

class AuthController {

    suspend fun activateUser(call: ApplicationCall) {
        val authReceive = call.receive<ActivationReceive>()

        val loginUser = Users.fetchUser(authReceive.login)
        if (authReceive.deviceId.toId() != nullUUID) {
            if (loginUser != null) {
                if (loginUser.password != null) {
                    call.respond(HttpStatusCode.Conflict, "admin.users.User already authorized")
                } else {
                    val token = UUID.randomUUID()

                    try {
                        Users.activate(
                            authReceive.login,
                            authReceive.password
                        )
                    } catch (e: ExposedSQLException) {
                        call.respond(HttpStatusCode.Conflict, "admin.users.User already exists")
                    } catch (e: Throwable) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            "Can't create user ${e.localizedMessage}"
                        )
                    }

                    Tokens.insert(
                        TokenDTO(
                            deviceId = authReceive.deviceId.toId(),
                            login = authReceive.login,
                            token = token,
                            deviceName = authReceive.deviceName,
                            deviceType = authReceive.deviceType,
                            time = Clock.System.now().toLocalDateTime(TimeZone.of("Europe/Moscow"))
                                .toString()
                                .cut(16)
                        )
                    )

                    call.respond(
                        ActivationResponse(
                            token = token.toString(),
                            user = UserInit(
                                fio = FIO(
                                    name = loginUser.name,
                                    surname = loginUser.surname,
                                    praname = loginUser.praname
                                ),
                                role = loginUser.role,
                                moderation = loginUser.moderation,
                                birthday = loginUser.birthday,
                                isParent = loginUser.isParent
                            )
                        )
                    )

                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "No admin.users.User found")
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "NullableUID")
        }


    }

    suspend fun checkUserActivation(call: ApplicationCall) {
        val authReceive = call.receive<CheckActivationReceive>()

        val loginUser = Users.fetchUser(authReceive.login)

        if (loginUser != null) {
            if (loginUser.password != null) {
                call.respond(CheckActivationResponse(loginUser.name, true))
            } else {
                call.respond(CheckActivationResponse(loginUser.name, false))

            }
        } else {
            call.respond(CheckActivationResponse(null, false))
        }

    }

    suspend fun performLogin(call: ApplicationCall) {
        val receive = call.receive<LoginReceive>()
        val userDTO = Users.fetchUser(receive.login)

        if (userDTO == null) {
            call.respond(HttpStatusCode.BadRequest, "admin.users.User not found")
        } else {
            if (receive.deviceId.toId() != nullUUID) {
                when (userDTO.password ?: "".cut(DataLength.passwordLength)) {
                    receive.password -> {
                        val token = UUID.randomUUID()
                        Tokens.insert(
                            TokenDTO(
                                deviceId = receive.deviceId.toId(),
                                login = userDTO.login,
                                token = token,
                                deviceName = receive.deviceName,
                                deviceType = receive.deviceType,
                                time = Clock.System.now()
                                    .toLocalDateTime(TimeZone.of("Europe/Moscow")).toString()
                                    .cut(16)
                            )
                        )
                        if (userDTO.isActive) {
                            call.respond(
                                LoginResponse(
                                    activation = ActivationResponse(
                                        token = token.toString(),
                                        user = UserInit(
                                            fio = FIO(
                                                name = userDTO.name,
                                                surname = userDTO.surname,
                                                praname = userDTO.praname
                                            ),
                                            birthday = userDTO.birthday,
                                            role = userDTO.role,
                                            moderation = userDTO.moderation,
                                            isParent = userDTO.isParent
                                        )
                                    ),
                                    avatarId = userDTO.avatarId
                                )
                            )
                        } else {
                            call.respond(
                                HttpStatusCode.Forbidden,
                                "Your account has been deactivated"
                            )
                        }
                    }

                    null -> {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            "admin.users.User wasn't authorized"
                        )
                    }

                    else -> {
                        call.respond(HttpStatusCode.BadRequest, "Invalid password")
                    }
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "NullableUUID")
            }
        }
    }
}