package com.nevrozq.pansion.features.school

import FIO
import admin.groups.forms.CutedForm
import com.nevrozq.pansion.database.achievements.Achievements
import com.nevrozq.pansion.database.calendar.Calendar
import com.nevrozq.pansion.database.forms.FormDTO
import com.nevrozq.pansion.database.forms.Forms
import com.nevrozq.pansion.database.ratingEntities.Marks
import com.nevrozq.pansion.database.ratingEntities.Stups
import com.nevrozq.pansion.database.ratingTable.RatingWeek0Table
import com.nevrozq.pansion.database.studentLines.StudentLines
import com.nevrozq.pansion.database.studentsInForm.StudentsInForm
import com.nevrozq.pansion.database.subjects.Subjects
import com.nevrozq.pansion.database.users.Users
import com.nevrozq.pansion.utils.getModuleByDate
import com.nevrozq.pansion.utils.isMember
import com.nevrozq.pansion.utils.isMentor
import com.nevrozq.pansion.utils.isModer
import com.nevrozq.pansion.utils.isOnlyMentor
import com.nevrozq.pansion.utils.isStudent
import com.nevrozq.pansion.utils.isTeacher
import com.nevrozq.pansion.utils.login
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import main.RFetchSchoolDataReceive
import main.RFetchSchoolDataResponse
import mentoring.MentorForms
import org.jetbrains.exposed.exceptions.ExposedSQLException
import rating.FormRatingStudent
import rating.FormRatingStup
import rating.RFetchFormRatingReceive
import rating.RFetchFormRatingResponse
import rating.RFetchFormsForFormResponse
import server.getCurrentDate
import kotlin.math.log

class SchoolController {

    suspend fun fetchFormRating(call: ApplicationCall) {
        if (call.isMember) {
            try {
                val r = call.receive<RFetchFormRatingReceive>()

                val module = getModuleByDate(getCurrentDate().second)

                val forms = if (r.formNum > 9) {
                    Forms.getAllForms()
                } else emptyList<FormDTO>()
                val formIds = if (r.formNum > 9) {
                    forms.filter { it.classNum == r.formNum }.map { it.formId }
                } else listOf(r.formId)

                val studentsForRating: MutableList<FormRatingStudent> = mutableListOf()

                formIds.forEach { formId ->
                    val studentLogins = StudentsInForm.fetchStudentsLoginsByFormId(formId)
                    studentLogins.forEach { login ->
                        val user = Users.fetchUser(login)
                        if (user != null) {

                            //ForAvg
                            //List<FormRatingStup>
                            //likes dislikes


                            val avg = when (r.period) {
                                0 -> Marks.fetchWeekAVG(login)
                                1 -> Marks.fetchPreviousWeekAVG(login)
                                2 -> Marks.fetchModuleAVG(login, module!!.num.toString())
                                3 -> Marks.fetchHalfYearAVG(login, module!!.halfNum)
                                else -> Marks.fetchYearAVG(login)
                            }

                            val stups = when (r.period) {
                                0 -> Stups.fetchForAWeek(login)
                                1 -> Stups.fetchForAPreviousWeek(login)
                                2 -> Stups.fetchForUserQuarters(
                                    login,
                                    quartersNum = module!!.num.toString(),
                                    isQuarters = true
                                )

                                3 -> Stups.fetchForUserQuarters(
                                    login,
                                    quartersNum = module!!.halfNum.toString(),
                                    isQuarters = false
                                )

                                else -> Stups.fetchForUser(login)
                            }.map {
                                FormRatingStup(
                                    subjectId = it.subjectId,
                                    reason = it.reason,
                                    content = it.content,
                                    date = it.date
                                )
                            }

                            val edStups = stups.filter {
                                it.reason.subSequence(0, 3) in listOf("!st")
                            }
//                            val studentLines = StudentLines.
//                            val achievements = Achievements.fetchAllByLogin()

                            studentsForRating.add(
                                FormRatingStudent(
                                    login = login,
                                    fio = FIO(
                                        name = user.name,
                                        praname = user.praname,
                                        surname = user.surname
                                    ),
                                    avatarId = user.avatarId,
                                    formTitle = if (r.formNum > 9) forms.first { it.formId == formId }.shortTitle else null,
                                    avg = avg,
                                    edStups = edStups,
                                )
                            )
                        }
                    }
                }
                call.respond(
                    RFetchFormRatingResponse(
                        studentsForRating,
                        Subjects.fetchAllSubjectsAsMap()
                    )
                )
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Conflict!")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch form rating: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }


    suspend fun fetchFormsForFormRating(call: ApplicationCall) {
        if (call.isModer || call.isMentor || call.isTeacher) {
            try {
                val forms =
                    (
                            if (call.isOnlyMentor) {
                                Forms.fetchMentorForms(call.login)
                            } else {
                                Forms.getAllForms()
                                    .map {
                                        MentorForms(
                                            id = it.formId,
                                            num = it.classNum,
                                            title = it.title,
                                            isQrActive = false
                                        )
                                    }
                            }
                            ).map {
                            CutedForm(
                                id = if (it.num > 9) it.num else it.id,
                                title = if (it.num > 9) it.num.toString() else it.num.toString() + "-" + it.title.uppercase(),
                                classNum = it.num
                            )
                        }.toSet().toList()
                call.respond(
                    RFetchFormsForFormResponse(
                        forms
                    )
                )
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Conflict!")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't update report: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchLentaData(call: ApplicationCall) {
        val r = call.receive<RFetchSchoolDataReceive>()
        if (call.isMember) {
            try {
                var formName: String? = null
                var formNum: Int? = null
                var formId: Int? = null
                var top: Int? = null
                if (call.isStudent) {
                    formId =
                        StudentsInForm.fetchFormIdOfLogin(r.login)
                    val form = Forms.fetchById(formId)
                    formNum = form.classNum
                    if (formNum > 9) {
                        formName = formNum.toString()
                    } else {
                        formName = "$formNum-${form.title.uppercase()}"
                    }
                    top = RatingWeek0Table.fetchRatingOf(r.login, -1)?.top
                } else if (call.isMentor) {
                    val form = Forms.fetchMentorForms(r.login).firstOrNull()
                    println("TESTIRUEM: ${r.login} ${form}")
                    if (form != null) {
                        formName = form.num.toString()
                        formNum = form.num
                        formId = form.id
                    }
                }

                call.respond(
                    RFetchSchoolDataResponse(
                        formId = formId,
                        formName = formName,
                        formNum = formNum,
                        top = top
                    )
                )
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Conflict!")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't update report: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }
}