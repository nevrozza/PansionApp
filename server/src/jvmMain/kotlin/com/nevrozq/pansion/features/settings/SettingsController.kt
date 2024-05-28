package com.nevrozq.pansion.features.settings

import com.nevrozq.pansion.database.tokens.Tokens
import com.nevrozq.pansion.features.user.manageOld.models.DeleteTokenReceive
import com.nevrozq.pansion.utils.toId
import com.nevrozq.pansion.utils.token
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond

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