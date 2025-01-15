package com.nevrozq.pansion.features.auth

import RequestPaths
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureActivationRouting() {
    routing {
        val authController = AuthController()

        post(RequestPaths.WebLoad.FetchUserData) {
            authController.fetchUserData(call)
        }

        post(RequestPaths.Auth.ChangeStatsSettings) {
            authController.changeStatsSettincs(call)
        }

        post(RequestPaths.Auth.ChangeLogin) {
            authController.changeSecondLogin(call)
        }

        post(RequestPaths.Auth.PollQRToken) {
            authController.QRTokenStartPolling(call)
        }

        post(RequestPaths.Auth.ActivateQRTokenAtAll) {
            authController.ActivateQRTokenAtAll(call)
        }
        post(RequestPaths.Auth.ActivateQRToken) {
            authController.ActivateQRToken(call)
        }

        post(RequestPaths.Auth.FetchQRToken) {
            authController.fetchQRToken(call)
        }

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