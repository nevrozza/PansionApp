package com.nevrozq.pansion.features.achievements

import achievements.RCreateAchievementReceive
import achievements.RFetchAchievementsForStudentReceive
import achievements.RFetchAchievementsResponse
import com.nevrozq.pansion.database.achievements.Achievements
import com.nevrozq.pansion.utils.isMember
import com.nevrozq.pansion.utils.isMentor
import com.nevrozq.pansion.utils.isModer
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond

class AchievementsController {

    suspend fun fetchForStudent(call: ApplicationCall) {
        if (call.isMember) {
            try {
                val r = call.receive<RFetchAchievementsForStudentReceive>()
                val achievements = Achievements.fetchAllByLogin(r.studentLogin)
                call.respond(
                    RFetchAchievementsResponse(achievements)
                )
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't create achievement: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }
    suspend fun fetchAllAchievements(call: ApplicationCall) {
        if (call.isMentor || call.isModer) {
            try {
                val achievements = Achievements.fetchAll()
                call.respond(
                    RFetchAchievementsResponse(achievements)
                )
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't create achievement: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun createAchievement(call: ApplicationCall) {
        if (call.isMentor || call.isModer) {
            try {
                val r = call.receive<RCreateAchievementReceive>()
                Achievements.insert(r.achievement)
                call.respond(HttpStatusCode.OK)
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't create achievement: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    //suspend fun createForm(call: ApplicationCall) {
    //        val r = call.receive<CreateFormReceive>()
    //        if (call.isModer) {
    //            try {
    //                Forms.insert(
    //                    FormDTO(
    //                        title = r.form.title,
    //                        classNum = r.form.classNum,
    //                        mentorLogin = r.form.mentorLogin,
    //                        shortTitle = r.form.shortTitle,
    //                        isActive = true
    //                    )
    //                )
    //
    //                call.respond(HttpStatusCode.OK)
    //            } catch (e: ExposedSQLException) {
    //                call.respond(HttpStatusCode.Conflict, "Form already exists")
    //            } catch (e: Throwable) {
    //                call.respond(
    //                    HttpStatusCode.BadRequest,
    //                    "Can't create group: ${e.localizedMessage}"
    //                )
    //            }
    //        } else {
    //            call.respond(HttpStatusCode.Forbidden, "No permission")
    //        }
    //    }
}