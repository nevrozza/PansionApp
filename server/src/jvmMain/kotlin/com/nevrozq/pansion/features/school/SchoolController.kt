package com.nevrozq.pansion.features.school

import FIO
import admin.groups.forms.CutedForm
import com.nevrozq.pansion.database.calendar.CalendarDTO
import com.nevrozq.pansion.database.forms.FormDTO
import com.nevrozq.pansion.database.forms.Forms
import com.nevrozq.pansion.database.ratingEntities.Marks
import com.nevrozq.pansion.database.ratingEntities.Stups
import com.nevrozq.pansion.database.ratingTable.RatingWeek0Table
import com.nevrozq.pansion.database.studentLines.StudentLines
import com.nevrozq.pansion.database.studentMinistry.StudentMinistry
import com.nevrozq.pansion.database.studentsInForm.StudentsInForm
import com.nevrozq.pansion.database.subjects.Subjects
import com.nevrozq.pansion.database.users.UserDTO
import com.nevrozq.pansion.database.users.Users
import com.nevrozq.pansion.utils.getModuleByDate
import com.nevrozq.pansion.utils.isMember
import com.nevrozq.pansion.utils.isMentor
import com.nevrozq.pansion.utils.isModer
import com.nevrozq.pansion.utils.isOnlyMentor
import com.nevrozq.pansion.utils.isStudent
import com.nevrozq.pansion.utils.isTeacher
import com.nevrozq.pansion.utils.login
import com.nevrozq.pansion.utils.moderation
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import main.RFetchSchoolDataReceive
import main.RFetchSchoolDataResponse
import main.school.*
import mentoring.MentorForms
import org.jetbrains.exposed.exceptions.ExposedSQLException
import rating.FormRatingStudent
import rating.FormRatingStup
import rating.RFetchFormRatingReceive
import rating.RFetchFormRatingResponse
import rating.RFetchFormsForFormResponse
import server.Ministries
import server.Moderation
import server.getCurrentDate
import server.getWeekDays

class SchoolController {

    suspend fun fetchMinistryList(call: ApplicationCall) {
        if (call.isMember) {
            try {
                val r = call.receive<RMinistryListReceive>()
                val availableKids: List<UserDTO>? = if (call.isStudent) {
                    val formId = StudentsInForm.fetchFormIdOfLogin(call.login)
                    val students = StudentsInForm.fetchStudentsLoginsByFormId(formId)
                    Users.fetchByLoginsActivated(students)
                } else if (call.isOnlyMentor) {
                    val formIds = Forms.fetchMentorForms(call.login).map { it.id }
                    val students = StudentsInForm.fetchStudentsLoginsByFormIds(formIds)
                    Users.fetchByLoginsActivated(students)
                } else if (call.isModer) {
                    Users.fetchAllStudents().filter { it.isActive }
                } else {
                    null
                }



                if (availableKids != null) {
                    val kids = mutableListOf<MinistryKid>()
                    val forms = Forms.getAllForms().filter { it.isActive }
                    val currentModule = getModuleByDate(getCurrentDate().second) ?: CalendarDTO(
                        num = 1,
                        start = "01.01.2000",
                        halfNum = 1
                    )
                    availableKids.forEach {
                        val formId = StudentsInForm.fetchFormIdOfLogin(it.login)
                        val form = forms.first { it.formId == formId }


                        val lessons: List<MinistryLesson> = if (r.ministryId == Ministries.MVD) {
                            StudentLines.fetchClientStudentLines(login = it.login, date = r.date).map {
                                val isUvNka = (if (it.attended == "0" || it.attended == null) {
                                    null
                                } else if (it.attended == "1") {
                                    false
                                } else {
                                    true
                                })
                                MinistryLesson(
                                    reportId = it.reportId,
                                    subjectName = it.subjectName,
                                    groupName = it.groupName,
                                    time = it.time,
                                    isUvNka = isUvNka,
                                    lateTime = it.lateTime,
                                    isLiked = it.isLiked
                                )
                            }
                        } else {
                            listOf()
                        }

                        val stups = Stups.fetchForUser(login = it.login).filter {
                            if (r.ministryId == Ministries.MVD) it.reason.subSequence(0, 3) == "!ds"
                            else if (r.ministryId == Ministries.DressCode) it.reason.subSequence(0, 3) == "!dc"
                            else false
                        }

                        val dayStups: List<MinistryStup> = stups.filter { it.date == r.date }.map {
                            MinistryStup(
                                reason = it.reason,
                                content = it.content
                            )
                        }

                        val weekStupsCount =
                            stups.filter { it.date in getWeekDays() }.sumOf { it.content.toIntOrNull() ?: 0 }
                        val moduleStupsCount = stups.filter { it.part == currentModule.num.toString() }
                            .sumOf { it.content.toIntOrNull() ?: 0 }
                        val yearStupsCount = stups.sumOf { it.content.toIntOrNull() ?: 0 }
                        kids.add(
                            MinistryKid(
                                login = it.login,
                                formId = formId,
                                formTitle = "${form.classNum}-${form.shortTitle}",
                                fio = FIO(
                                    name = it.name,
                                    surname = it.surname,
                                    praname = it.praname
                                ),
                                lessons = lessons,
                                dayStups = dayStups,
                                weekStupsCount = weekStupsCount,
                                moduleStupsCount = moduleStupsCount,
                                yearStupsCount = yearStupsCount
                            )
                        )
                    }

                    call.respond(
                        RMinistryListResponse(
                            kids = kids
                        )
                    )
                } else {
                    call.respond(HttpStatusCode.Conflict, "MINISTRY LIST AVAILABLE KIDS == NUll")
                }
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Conflict!")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch ministry list: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchMinistryHeaderInit(call: ApplicationCall) {
        if (call.isMember) {
            try {
                val pickedMinistry = StudentMinistry.fetchMinistryWithLogin(call.login) ?: "0"
                call.respond(
                    RFetchMinistryHeaderInitResponse(
                        isMultiMinistry = call.moderation != Moderation.nothing,
                        pickedMinistry = pickedMinistry
                    )
                )
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Conflict!")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch ministry header init: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun createMinistryStudent(call: ApplicationCall) {
        if (call.moderation in listOf(Moderation.mentor, Moderation.both)) {
            try {
                val r = call.receive<RCreateMinistryStudentReceive>()
                val fioParts = r.studentFIO.split(" ")
                val login = r.login ?: Users.getLoginWithFIO(
                    fio = FIO(
                        surname = fioParts[0],
                        name = fioParts[1],
                        praname = fioParts.getOrNull(2)
                    ), itShouldBeStudent = true
                )!!
                StudentMinistry.set(login = login, ministry = r.ministryId)

                fetchMinistrySettings(call, isChecked = true)
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Conflict!")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't create ministry student: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchMinistrySettings(call: ApplicationCall, isChecked: Boolean = false) {
        if (isChecked || (call.moderation in listOf(Moderation.mentor, Moderation.both))) {
            try {
                val forms = Forms.getAllForms().filter { it.mentorLogin == call.login }

                val studentsForMinistry: MutableList<MinistryStudent> = mutableListOf()

                forms.forEach { form ->
                    val studentLogins = StudentsInForm.fetchStudentsLoginsByFormId(form.formId)
                    studentLogins.forEach { login ->
                        val ministryId = StudentMinistry.fetchMinistryWithLogin(login)
                        if (ministryId != null) {
                            val user = Users.fetchUser(login)
                            if (user != null && user.isActive) {
                                studentsForMinistry.add(
                                    MinistryStudent(
                                        ministryId = ministryId,
                                        fio = FIO(
                                            name = user.name,
                                            surname = user.surname,
                                            praname = user.praname
                                        ),
                                        form = "${form.classNum}-${form.shortTitle}",
                                        login = login
                                    )
                                )
                            }
                        }
                    }
                }
                call.respond(
                    RFetchMinistrySettingsResponse(
                        studentsForMinistry
                    )
                )
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Conflict!")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch ministry settings: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

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