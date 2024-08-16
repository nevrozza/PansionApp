package com.nevrozq.pansion.features.mentoring

import RequestPaths
import com.nevrozq.pansion.features.lessons.LessonsController
import io.ktor.server.application.Application
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun Application.configureMentoringRouting() {
    routing {
        val mentoringController = MentoringController()

        post(RequestPaths.Main.FetchMentorGroupIds) {
            mentoringController.fetchMentorGroupIds(call)
        }

        post(RequestPaths.Mentoring.SavePreAttendanceDay) {
            mentoringController.savePreAttendanceDay(call)
        }

        post(RequestPaths.Mentoring.FetchPreAttendanceDay) {
            mentoringController.fetchPreAttendanceDay(call)
        }

        post(RequestPaths.Mentoring.FetchMentoringStudents) {
            mentoringController.fetchStudents(call)
        }
    }
}