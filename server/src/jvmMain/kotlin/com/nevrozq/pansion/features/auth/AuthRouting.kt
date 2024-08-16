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

        post(RequestPaths.Auth.CheckGIASubject) {
            authController.checkGIASubject(call)
        }

        post(RequestPaths.Auth.FetchAllDevices) {
            authController.fetchAllDevices(call)
        }
        post(RequestPaths.Auth.TerminateDevice) {
            authController.terminateDevice(call)
        }

        post(RequestPaths.Auth.FetchAboutMe) {
            authController.fetchAboutMe(call)
        }

        post(RequestPaths.Auth.ChangeAvatarId) {
            authController.updateAvatarId(call)
        }

        post(RequestPaths.Auth.CheckConnection) {
            authController.checkConnection(call)
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