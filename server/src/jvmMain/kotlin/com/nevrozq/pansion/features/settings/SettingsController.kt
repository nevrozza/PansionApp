package com.nevrozq.pansion.features.settings

import com.nevrozq.pansion.database.tokens.Tokens
import com.nevrozq.pansion.utils.UUIDSerializer
import com.nevrozq.pansion.utils.toId
import com.nevrozq.pansion.utils.token
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable
import java.util.UUID

class SettingsController {
    suspend fun logout(call: ApplicationCall) {
        try {
            if (call.token != null) {
                if (Tokens.isTokenValid(call.token.toId())) {
                    Tokens.deleteToken(call.token.toId())
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Token expired")
                }
            } else {
                call.respond(HttpStatusCode.Unauthorized, "There is nothing to delete")
            }
        } catch (e: Throwable) {
            println(e.message)
        }
    }
}

@kotlinx.serialization.Serializable
data class DeleteTokenReceive(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID
)