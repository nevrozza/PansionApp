package com.nevrozq.pansion.features.settings

import com.nevrozq.pansion.features.user.manage.UserManageController
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun Application.configureSettingsRouting() {
    routing {
        val settingsController = SettingsController()
        post(RequestPaths.Tokens.logout) {
//            val registerController = RegisterController(call)
            settingsController.logout(call)
        }
    }
}