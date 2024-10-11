package com.nevrozq.pansion.features.school

import RequestPaths
import io.ktor.server.application.Application
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun Application.configureSchoolRouting() {
    routing {
        val schoolController = SchoolController()

        post(RequestPaths.Main.FetchFormRating) {
            schoolController.fetchFormRating(call)
        }

        post(RequestPaths.Main.FetchFormsForFormRating) {
            schoolController.fetchFormsForFormRating(call)
        }

        post(RequestPaths.Main.FetchSchoolData) {
            schoolController.fetchLentaData(call)
        }
    }
}