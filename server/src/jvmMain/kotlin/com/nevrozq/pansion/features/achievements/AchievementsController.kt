package com.nevrozq.pansion.features.achievements

import FIO
import Person
import achievements.RCreateAchievementReceive
import achievements.RDeleteAchievementReceive
import achievements.REditAchievementReceive
import achievements.RFetchAchievementsForStudentReceive
import achievements.RFetchAchievementsResponse
import achievements.RUpdateGroupOfAchievementsReceive
import com.nevrozq.pansion.database.achievements.Achievements
import com.nevrozq.pansion.database.forms.Forms
import com.nevrozq.pansion.database.studentMinistry.StudentMinistry
import com.nevrozq.pansion.database.studentsInForm.StudentsInForm
import com.nevrozq.pansion.database.subjects.Subjects
import com.nevrozq.pansion.database.users.Users
import com.nevrozq.pansion.utils.*
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import server.Ministries

class AchievementsController {

    suspend fun updateGroup(call: ApplicationCall) {
        val perm = call.isMentor || call.isModer
        call.dRes(
            permission = perm,
            errorText = "Can't update group"
        ) {
            val r = this.receive<RUpdateGroupOfAchievementsReceive>()
            Achievements.editGroup(
                oldShowDate = r.oldShowDate,
                oldDate = r.oldDate,
                oldText = r.oldText,
                newDate = r.newDate,
                newText = r.newText,
                newShowDate = r.newShowDate
            )
            this.respond(
                RFetchAchievementsResponse(
                    list = Achievements.fetchAll(),
                    students = null,
                    subjects = emptyMap()
                )
            ).done
        }
    }

    suspend fun updateAchievement(call: ApplicationCall) {
        val perm = call.isMentor || call.isModer
        call.dRes(
            permission = perm,
            errorText = "Can't update achievement"
        ) {
            val r = this.receive<REditAchievementReceive>()
            Achievements.edit(id = r.id, subjectId = r.subjectId, stups = r.stups, studentLogin = r.studentLogin)
            this.respond(
                RFetchAchievementsResponse(
                    list = Achievements.fetchAll(),
                    students = null,
                    subjects = emptyMap()
                )
            ).done
        }
    }

    suspend fun deleteAchievement(call: ApplicationCall) {
        val perm = call.isMentor || call.isModer
        call.dRes(perm, "Can't delete achievement") {
            val r = this.receive<RDeleteAchievementReceive>()
            Achievements.delete(r.id)
            this.respond(
                RFetchAchievementsResponse(
                    list = Achievements.fetchAll(),
                    students = null,
                    subjects = emptyMap()
                )
            ).done
        }
    }

    suspend fun fetchForStudent(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch student's achievements") {
            val r = this.receive<RFetchAchievementsForStudentReceive>()
            val achievements = Achievements.fetchAllByLogin(r.studentLogin)
            this.respond(
                RFetchAchievementsResponse(
                    achievements,
                    students = null,
                    subjects = Subjects.fetchAllSubjectsAsMap()
                )
            ).done
        }
    }

    suspend fun fetchAllAchievements(call: ApplicationCall) {
        val minDTO = StudentMinistry.fetchMinistryWithLogin(call.login)
        val perm = call.isMentor || call.isModer || minDTO?.ministry == Ministries.CULTURE

        call.dRes(perm, "Can't fetch all achievements") {
            val achievements = Achievements.fetchAll()
            val students = if (this.isOnlyMentor || (this.isStudent && minDTO?.lvl == "0")) {
                val mentorLogin = if (this.isStudent) {
                    val formId = StudentsInForm.fetchFormIdOfLogin(this.login)
                    val form = Forms.fetchById(formId)
                    form.mentorLogin
                } else this.login
                val formIds = Forms.fetchMentorForms(mentorLogin).map { it.id }
                val students = StudentsInForm.fetchStudentsLoginsByFormIds(formIds)
                Users.fetchByLoginsActivated(students)
            } else {
                Users.fetchAllStudents().filter { it.isActive }
            }
            this.respond(
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
            ).done
        }
    }

    suspend fun createAchievement(call: ApplicationCall) {
        val minId = StudentMinistry.fetchMinistryWithLogin(call.login)?.ministry

        val perm = call.isMentor || call.isModer || minId == Ministries.CULTURE

        call.dRes(perm, "Can't create achievement") {
            val r = this.receive<RCreateAchievementReceive>()
            Achievements.insert(r.achievement.copy(creatorLogin = this.login))
            this.respond(
                RFetchAchievementsResponse(
                    list = Achievements.fetchAll(),
                    students = null,
                    subjects = emptyMap()
                )
            ).done
        }
    }
}