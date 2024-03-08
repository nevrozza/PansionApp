package com.nevrozq.pansion.features.user.manageOld

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import com.nevrozq.pansion.database.tokens.Tokens
import com.nevrozq.pansion.database.tokens.toFetchTokensResponse
import com.nevrozq.pansion.features.user.manageOld.models.DeleteTokenReceive
import com.nevrozq.pansion.features.user.manageOld.models.FetchTokensResponse
import com.nevrozq.pansion.utils.toId

class UserManageOldController() {
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
            if(Tokens.getTokensOfThisLogin(login).any { it.deviceId == deleteTokenReceive.id }) {
                Tokens.deleteTokenByIdAndLogin(deleteTokenReceive.id, login)
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid id")
            }
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Token expired")
        }
    }
}