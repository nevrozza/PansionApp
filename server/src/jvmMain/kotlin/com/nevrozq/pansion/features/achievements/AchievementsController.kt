package com.nevrozq.pansion.features.achievements

import FIO
import Person
import achievements.AchievementsDTO
import achievements.RCreateAchievementReceive
import achievements.RDeleteAchievementReceive
import achievements.REditAchievementReceive
import achievements.RFetchAchievementsForStudentReceive
import achievements.RFetchAchievementsResponse
import achievements.RUpdateGroupOfAchievementsReceive
import com.nevrozq.pansion.database.achievements.Achievements
import com.nevrozq.pansion.database.forms.Forms
import com.nevrozq.pansion.database.studentsInForm.StudentsInForm
import com.nevrozq.pansion.database.subjects.Subjects
import com.nevrozq.pansion.database.users.Users
import com.nevrozq.pansion.utils.*
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import org.jetbrains.exposed.sql.deleteWhere

class AchievementsController {

    suspend fun updateGroup(call: ApplicationCall) {
        if (call.isMentor || call.isModer) {
            try {
                val r = call.receive<RUpdateGroupOfAchievementsReceive>()
                Achievements.editGroup(
                    oldShowDate = r.oldShowDate,
                    oldDate = r.oldDate,
                    oldText = r.oldText,
                    newDate = r.newDate,
                    newText = r.newText,
                    newShowDate = r.newShowDate
                )
                call.respond(RFetchAchievementsResponse(
                    list = Achievements.fetchAll(),
                    students = null,
                    subjects = emptyMap()
                ))
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

    suspend fun updateAchievement(call: ApplicationCall) {
        if (call.isMentor || call.isModer) {
            try {
                val r = call.receive<REditAchievementReceive>()
                Achievements.edit(id = r.id, subjectId = r.subjectId, stups = r.stups, studentLogin = r.studentLogin)
                call.respond(RFetchAchievementsResponse(
                    list = Achievements.fetchAll(),
                    students = null,
                    subjects = emptyMap()
                ))
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
    suspend fun deleteAchievement(call: ApplicationCall) {
        if (call.isModer) {
            try {
                val r = call.receive<RDeleteAchievementReceive>()
                Achievements.delete(r.id)
                call.respond(RFetchAchievementsResponse(
                    list = Achievements.fetchAll(),
                    students = null,
                    subjects = emptyMap()
                ))
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

    suspend fun fetchForStudent(call: ApplicationCall) {
        if (call.isMember) {
            try {
                val r = call.receive<RFetchAchievementsForStudentReceive>()
                val achievements = Achievements.fetchAllByLogin(r.studentLogin)
                call.respond(
                    RFetchAchievementsResponse(achievements, students = null, subjects = Subjects.fetchAllSubjectsAsMap())
                )
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't create achievement: ${e.message}"
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
                val students = if (call.isOnlyMentor) {
                    val formIds = Forms.fetchMentorForms(call.login).map { it.id }
                    val students = StudentsInForm.fetchStudentsLoginsByFormIds(formIds)
                    Users.fetchByLoginsActivated(students)
                } else {
                    Users.fetchAllStudents().filter { it.isActive }
                }
                call.respond(
                    RFetchAchievementsResponse(
                        achievements,
                        students = students.map {
                            Person(
                                login = it.login,
                                fio = FIO(
                                    name = it.name,
                                    surname = it.surname,
                                    praname = it.praname
                                ),
                                isActive = it.isActive
                            )
                        },
                        subjects = Subjects.fetchAllSubjectsAsMap()
                        )
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
                Achievements.insert(r.achievement.copy(creatorLogin = call.login))
                call.respond(RFetchAchievementsResponse(
                    list = Achievements.fetchAll(),
                    students = null,
                    subjects = emptyMap()
                ))
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