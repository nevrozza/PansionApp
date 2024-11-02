package com.nevrozq.pansion.features.achievements

import RequestPaths
import io.ktor.server.application.Application
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun Application.configureAchievementsRouting() {
    routing {
        val achievementsController = AchievementsController()
        post(RequestPaths.Achievements.Create) {
            achievementsController.createAchievement(call)
        }

        post(RequestPaths.Achievements.Delete) {
            achievementsController.deleteAchievement(call)
        }


        post(RequestPaths.Achievements.Edit) {
            achievementsController.updateAchievement(call)
        }

        post(RequestPaths.Achievements.UpdateGroup) {
            achievementsController.updateGroup(call)
        }

        post(RequestPaths.Achievements.FetchAll) {
            achievementsController.fetchAllAchievements(call)
        }

        post(RequestPaths.Achievements.FetchForStudent) {
            achievementsController.fetchForStudent(call)
        }

    }
}