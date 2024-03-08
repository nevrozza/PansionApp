package com.nevrozq.pansion.features.user.manage

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRegisterRouting() {
    routing {
        val userManageController = UserManageController()
        post("server/user/register") {
//            val registerController = RegisterController(call)
            userManageController.registerNewUser(call)
        }

        post("server/user/fetchAllUsers") {
//            val registerController = RegisterController(call)
            userManageController.fetchAllUsers(call)
        }

        post("server/user/fetchAllUsersByClass") {
//            val registerController = RegisterController(call)
            userManageController.fetchAllUsersByClass(call)
        }

        post("server/user/clearPassword") {
//            val registerController = RegisterController(call)
            userManageController.clearUserPassword(call)
        }

        post("server/user/edit") {
//            val registerController = RegisterController(call)
            userManageController.performEditUser(call)
        }
    }
}