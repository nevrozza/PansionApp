package com.nevrozq.pansion.features.reports

import RequestPaths
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureReportsRouting() {
    routing {
        val reportsController = ReportsController()

        post(RequestPaths.Main.ChangeToUv) {
            reportsController.changeToUv(call)
        }


        post(RequestPaths.Reports.FetchStudentReport) {
            reportsController.fetchStudentReport(call)
        }

        post(RequestPaths.HomeTasks.SaveReportHomeTasks) {
            reportsController.saveReportHomeTasks(call)
        }
        post(RequestPaths.Reports.FetchStudentLines) {
            reportsController.fetchClientStudentLines(call)
        }

        post(RequestPaths.HomeTasks.FetchReportHomeTasks) {
            reportsController.fetchReportHomeTasks(call)
        }
        post(RequestPaths.HomeTasks.FetchGroupHomeTasks) {
            reportsController.fetchGroupHomeTasks(call)
        }

//        post(RequestPaths.Lessons.FetchAllSubjects) {
//            reportsController.fetchAllSubjects(call)
//        }

        post(RequestPaths.Reports.FetchFullReportData) {
            reportsController.fetchFullReportData(call)
        }

        post(RequestPaths.Reports.FetchRecentGrades) {
            reportsController.fetchRecentGrades(call)
        }

        post(RequestPaths.Reports.FetchAllGroupMarks) {
            reportsController.fetchAllGroupMarks(call)
        }

        post(RequestPaths.Reports.FetchDetailedStups) {
            reportsController.fetchDetailedStups(call)
        }

        post(RequestPaths.Main.FetchMainAVG) {
            reportsController.fetchMainAVG(call)
        }

        post(RequestPaths.Reports.FetchSubjectQuarterMarks) {
            reportsController.fetchSubjectQuarterMarks(call)
        }


        post(RequestPaths.Reports.UpdateReport) {
            reportsController.updateReport(call)
        }
        post(RequestPaths.Reports.FetchReportHeaders) {
            reportsController.fetchReportHeaders(call)
        }
        post(RequestPaths.Reports.CreateReport) {
            reportsController.createReport(call)
        }

        post(RequestPaths.Reports.FetchReportData) {
            reportsController.fetchReportData(call)
        }

        post(RequestPaths.Reports.FetchReportStudents) {
            reportsController.fetchReportStudents(call)
        }

        post(RequestPaths.Reports.FetchDnevnikRuMarks) {
            reportsController.fetchDnevnikRuMarks(call)
        }

        post(RequestPaths.Reports.FetchIsQuarters) {
            reportsController.fetchIsQuarter(call)
        }

    }
}