package com.nevrozq.pansion.features.user.manage

import RequestPaths
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRegisterRouting() {
    routing {
        val userManageController = UserManageController()
        post(RequestPaths.UserManage.CreateUser) {
//            val registerController = RegisterController(call)
            userManageController.createUser(call)
        }

        post(RequestPaths.UserManage.FetchAllUsers) {
//            val registerController = RegisterController(call)
            userManageController.fetchAllUsers(call)
        }

//        post("server/user/fetchAllUsersByClass") {
////            val registerController = RegisterController(call)
//            userManageController.fetchAllUsersByClass(call)
//        }

        post(RequestPaths.UserManage.ClearPasswordAdmin) {
//            val registerController = RegisterController(call)
            userManageController.clearUserPassword(call)
        }

        post(RequestPaths.UserManage.EditUser) {
//            val registerController = RegisterController(call)
            userManageController.performEditUser(call)
        }
    }
}