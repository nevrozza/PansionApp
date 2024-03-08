package com.nevrozq.pansion.features.auth.login

import auth.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import com.nevrozq.pansion.database.tokens.TokenDTO
import com.nevrozq.pansion.database.tokens.Tokens
import com.nevrozq.pansion.database.users.Users
import com.nevrozq.pansion.utils.cut
import com.nevrozq.pansion.utils.nullUUID
import com.nevrozq.pansion.utils.toId
import server.DataLength
import java.util.UUID

class LoginController {

    suspend fun performLogin(call: ApplicationCall) {
        val receive = call.receive<LoginReceive>()
        val userDTO = Users.fetchUser(receive.login)

        if (userDTO == null) {
            call.respond(HttpStatusCode.BadRequest, "admin.User not found")
        } else {
            if (receive.deviceId.toId() != nullUUID) {
                when (userDTO.password?:"".cut(DataLength.passwordLength)) {
                    receive.password -> {
                        val token = UUID.randomUUID()
                        Tokens.insert(
                            TokenDTO(
                                deviceId = receive.deviceId.toId(),
                                login = userDTO.login,
                                token = token,
                                deviceName = receive.deviceName,
                                deviceType = receive.deviceType,
                                time = Clock.System.now().toLocalDateTime(TimeZone.of("Europe/Moscow")).toString().cut(16)
                            )
                        )
                        call.respond(LoginResponse(token.toString(), userDTO.name, userDTO.surname, userDTO.praname, userDTO.role, userDTO.moderation))
                    }

                    null -> {
                        call.respond(HttpStatusCode.Unauthorized, "admin.User wasn't authorized")
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