package com.nevrozq.pansion.features.user.manageOld

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureUserManageRouting() {
    routing {
        val userManageOldController = UserManageOldController()
        get("server/auth/manage/tokens/fetch") {
            userManageOldController.performSearchTokens(call)
        }
        post("server/auth/manage/tokens/delete") {
            userManageOldController.deleteToken(call)
        }
    }
}