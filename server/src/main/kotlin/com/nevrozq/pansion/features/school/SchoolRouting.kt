package com.nevrozq.pansion.features.school

import RequestPaths
import io.ktor.server.application.Application
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun Application.configureSchoolRouting() {
    routing {
        val schoolController = SchoolController()

        post(RequestPaths.Main.StartNewDayDuty) {
            schoolController.startNewDayDuty(call)
        }

        post(RequestPaths.Main.UpdateDuty) {
            schoolController.editTodayDuty(call)
        }

        post(RequestPaths.Main.FetchDuty) {
            schoolController.fetchDuty(call)
        }

        post(RequestPaths.Main.UploadMinistryStup) {
            schoolController.uploadMinistryStups(call)
        }

        post(RequestPaths.Main.FetchMinistryList) {
            schoolController.fetchMinistryList(call)
        }

        post(RequestPaths.Main.FetchMinistryHeaderInit) {
            schoolController.fetchMinistryHeaderInit(call)
        }

        post(RequestPaths.Main.CreateMinistryStudent) {
            schoolController.createMinistryStudent(call)
        }

        post(RequestPaths.Main.FetchMinistrySettings) {
            schoolController.fetchMinistrySettings(call)
        }

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