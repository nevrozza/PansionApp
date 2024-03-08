package com.nevrozq.pansion.features.auth.activation

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureActivationRouting() {
    routing {
        val authController = AuthController()
        post("server/auth/activate") {
//            val registerController = RegisterController(call)
            authController.activateUser(call)
        }

        post("server/auth/check") {
//            val registerController = RegisterController(call)
            authController.checkUserActivation(call)
        }
    }
}