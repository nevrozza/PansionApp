package com.nevrozq.pansion.features.auth.login

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureLoginRouting() {
    routing {
        val loginController = LoginController()
        post("server/auth/login") {
//            val registerController = RegisterController(call)
            loginController.performLogin(call)
        }
    }
}