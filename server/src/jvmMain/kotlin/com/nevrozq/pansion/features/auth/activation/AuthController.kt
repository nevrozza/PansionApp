package com.nevrozq.pansion.features.auth.activation

import auth.ActivationReceive
import auth.ActivationResponse
import auth.CheckActivationReceive
import auth.CheckActivationResponse
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
import java.util.UUID

class AuthController {

    suspend fun activateUser(call: ApplicationCall) {
        val authReceive = call.receive<ActivationReceive>()

        val loginUser = Users.fetchUser(authReceive.login)
        if (authReceive.deviceId.toId() != nullUUID) {
            if (loginUser != null) {
                if (loginUser.password != null) {
                    call.respond(HttpStatusCode.Conflict, "admin.User already authorized")
                } else {
                    val token = UUID.randomUUID()

                    try {
                        Users.activate(
                            authReceive.login,
                            authReceive.password
                        )
                    } catch (e: ExposedSQLException) {
                        call.respond(HttpStatusCode.Conflict, "admin.User already exists")
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
                            time = Clock.System.now().toLocalDateTime(TimeZone.of("Europe/Moscow")).toString()
                                .cut(16)
                        )
                    )

                    call.respond(ActivationResponse(token.toString(), loginUser.name, loginUser.surname, loginUser.praname, loginUser.role, loginUser.moderation))

                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "No admin.User found")
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
}