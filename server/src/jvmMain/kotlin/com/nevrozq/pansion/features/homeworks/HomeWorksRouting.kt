package com.nevrozq.pansion.features.homeworks

import RequestPaths
import com.nevrozq.pansion.features.lessons.LessonsController
import io.ktor.server.application.Application
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun Application.configureHomeworksRouting() {
    routing {
        val homeWorksController = HomeWorksController()


        post(RequestPaths.HomeTasks.CheckTask) {
            homeWorksController.checkHomeTask(call)
        }

        post(RequestPaths.HomeTasks.FetchHomeTasks) {
            homeWorksController.fetchHomeTasks(call)
        }

        post(RequestPaths.HomeTasks.FetchHomeTasksInit) {
            homeWorksController.fetchHomeTasksInit(call)
        }
    }
}