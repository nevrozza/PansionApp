package com.nevrozq.pansion.features.auth

import RequestPaths
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureActivationRouting() {
    routing {
        val authController = AuthController()
        post(RequestPaths.Auth.ActivateProfile) {
//            val registerController = RegisterController(call)
            authController.activateUser(call)
        }

        post(RequestPaths.Auth.CheckActivation) {
//            val registerController = RegisterController(call)
            authController.checkUserActivation(call)
        }

        post(RequestPaths.Auth.PerformLogin) {
//            val registerController = RegisterController(call)
            authController.performLogin(call)
        }
    }
}