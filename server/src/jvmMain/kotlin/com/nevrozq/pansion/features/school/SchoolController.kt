package com.nevrozq.pansion.features.school

import FIO
import PersonPlus
import admin.groups.forms.CutedForm
import admin.groups.forms.Form
import admin.groups.forms.FormInit
import com.nevrozq.pansion.database.calendar.CalendarDTO
import com.nevrozq.pansion.database.duty.Duty
import com.nevrozq.pansion.database.duty.DutyCount
import com.nevrozq.pansion.database.duty.DutySettings
import com.nevrozq.pansion.database.duty.DutySettingsDTO
import com.nevrozq.pansion.database.forms.FormDTO
import com.nevrozq.pansion.database.forms.Forms
import com.nevrozq.pansion.database.ratingEntities.Marks
import com.nevrozq.pansion.database.ratingEntities.Stups
import com.nevrozq.pansion.database.ratingTable.RatingCommonSchoolTable
import com.nevrozq.pansion.database.ratingTable.RatingHighSchoolTable
import com.nevrozq.pansion.database.ratingTable.RatingLowSchoolTable
import com.nevrozq.pansion.database.ratingTable.RatingWeek0Table
import com.nevrozq.pansion.database.studentLines.StudentLines
import com.nevrozq.pansion.database.studentMinistry.StudentMinistry
import com.nevrozq.pansion.database.studentMinistry.StudentMinistryDTO
import com.nevrozq.pansion.database.studentsInForm.StudentsInForm
import com.nevrozq.pansion.database.subjects.Subjects
import com.nevrozq.pansion.database.users.UserDTO
import com.nevrozq.pansion.database.users.Users
import com.nevrozq.pansion.utils.*
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
import server.*

class SchoolController {

    suspend fun editTodayDuty(call: ApplicationCall) {
        val perm = call.isMentor
        call.dRes(perm, "Can't edit today's duty") {
            val r = this.receive<RUpdateTodayDuty>()
            val oldCount = DutySettings.fetchByLogin(this.login)?.peopleCount ?: 2

            if (oldCount != r.newDutiesCount) {
                DutySettings.insert(
                    DutySettingsDTO(
                        mentorLogin = this.login,
                        peopleCount = r.newDutiesCount
                    )
                )
            }
            Duty.enterList(
                mentorLogin = this.login,
                list = r.kids
            )

            this.respond(
                HttpStatusCode.OK
            ).done
        }
    }

    suspend fun startNewDayDuty(call: ApplicationCall) {
        val perm = call.isMentor
        val login = call.login
        call.dRes(perm, "Can't start new day...") {
            val r = this.receive<RStartNewDayDuty>()
            val oldDutyList = Duty.fetchByMentorLogin(login)
            val oldCount = DutySettings.fetchByLogin(login)?.peopleCount ?: 2
            val oldKids = oldDutyList.slice(0..<oldCount)
            val newKids = oldDutyList.slice(oldCount..<oldDutyList.size)
            oldKids.forEach {
                DutyCount.plusOne(it)
            }
            if (oldCount != r.newDutiesCount) {
                DutySettings.insert(
                    DutySettingsDTO(
                        mentorLogin = login,
                        peopleCount = r.newDutiesCount
                    )
                )
            }
            val newDutyList = newKids + oldKids
            Duty.enterList(
                mentorLogin = login,
                list = newDutyList
            )

            this.respond(
                HttpStatusCode.OK
            ).done
        }
    }


    suspend fun fetchDuty(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch duty") {
            val r = this.receive<RFetchDutyReceive>()
            val user = Users.fetchUser(r.login)
            val mentorLogin = if (user?.role == Roles.student) {
                val formId = StudentsInForm.fetchFormIdOfLogin(user?.login!!)
                Forms.fetchById(formId).mentorLogin
            } else if (user?.moderation in listOf(Moderation.both, Moderation.mentor)) {
                user?.login!!
            } else null!!

            var dutyList = Duty.fetchByMentorLogin(mentorLogin)
            if (dutyList.isEmpty()) {
                val forms = Forms.fetchMentorForms(mentorLogin)

                val logins = StudentsInForm.fetchStudentsLoginsByFormIds(forms.map { it.id })
                dutyList = Users.fetchByLoginsActivated(logins).sortedWith(compareBy({ it.surname }, { it.name }))
                    .map { it.login }
                Duty.enterList(
                    mentorLogin,
                    dutyList
                )
            }
            val peopleCount = DutySettings.fetchByLogin(mentorLogin)?.peopleCount ?: 2
            val users = Users.fetchByLoginsActivated(dutyList).map {
                DutyKid(
                    login = it.login,
                    avatarId = it.avatarId,
                    fio = FIO(
                        name = it.name,
                        praname = it.praname,
                        surname = it.surname
                    ),
                    dutyCount = DutyCount.fetchByLogin(it.login)
                )
            }
            this.respond(
                RFetchDutyResponse(
                    list = dutyList.mapNotNull { login ->
                        users.firstOrNull { it.login == login }
                    },
                    peopleCount = peopleCount
                )
            ).done
        }
    }

    suspend fun uploadMinistryStups(call: ApplicationCall) {
        val login = call.login
        val perm = call.isMember && (StudentMinistry.fetchMinistryWithLogin(login)?.ministry in listOf(
            Ministries.DressCode,
            Ministries.MVD
        ) || call.isMentor)
        call.dRes(perm, "Can't upload min stups") {
            Stups.uploadMinistryStup(this.receive<RUploadMinistryStup>(), login)

            this.respond(
                HttpStatusCode.OK
            ).done
        }
    }

    suspend fun fetchMinistryList(call: ApplicationCall) {
        val perm = call.isMember
        val edYear = getCurrentEdYear()
        call.dRes(perm, "Can't fetch ministry list") {
            val r = call.receive<RMinistryListReceive>()
            val ministry = StudentMinistry.fetchMinistryWithLogin(call.login)
            val isShowFull = call.isModer || (ministry?.ministry == r.ministryId && ministry?.lvl == "1")
            val availableKids: MutableList<UserDTO>? = if (r.login != null) {
                val user = Users.fetchUser(r.login!!)
                if (user != null) {
                    mutableListOf(user)
                } else null
            } else {
                if (call.isStudent) {
                    val formId = StudentsInForm.fetchFormIdOfLogin(call.login)
                    val students = StudentsInForm.fetchStudentsLoginsByFormId(formId)
                    Users.fetchByLoginsActivated(students).toMutableList()
                } else if (r.formId != null) {
                    val students = StudentsInForm.fetchStudentsLoginsByFormId(r.formId!!)
                    Users.fetchByLoginsActivated(students).toMutableList()
                    //                        if (isShowFull) {
                    //                            Users.fetchAllStudents().filter { it.isActive }
                    //                        } else if (call.isStudent) {
                    //                            val formId = StudentsInForm.fetchFormIdOfLogin(call.login)
                    //                            val students = StudentsInForm.fetchStudentsLoginsByFormId(formId)
                    //                            Users.fetchByLoginsActivated(students)
                    //                        } else if (call.isOnlyMentor) {
                    //                            val formIds = Forms.fetchMentorForms(call.login).map { it.id }
                    //                            val students = StudentsInForm.fetchStudentsLoginsByFormIds(formIds)
                    //                            Users.fetchByLoginsActivated(students)
                    //                        } else {
                    //                            null
                    //                        }

                } else null
            }


            if (r.login == null) {
                val selfKid = availableKids?.firstOrNull { it.login == call.login }
                availableKids?.remove(selfKid)
            }


            val kids = mutableListOf<MinistryKid>()
            val forms = if (isShowFull) Forms.getAllForms().filter { it.isActive }
            else if (call.isStudent) {
                val formId = StudentsInForm.fetchFormIdOfLogin(call.login)
                listOf(Forms.fetchById(formId)).filter { it.isActive }
            } else if (call.isOnlyMentor) {
                val formIds = Forms.fetchMentorForms(call.login).map { it.id }
                Forms.fetchByIds(formIds).filter { it.isActive }
            } else {
                listOf()
            }
            val currentModule = getModuleByDate(getCurrentDate().second) ?: CalendarDTO(
                num = 1,
                start = "01.01.2000",
                halfNum = 1
            )
            availableKids?.forEach {
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

                val stups = Stups.fetchForUser(login = it.login, edYear = edYear).filter {
                    if (r.ministryId == Ministries.MVD) it.reason.subSequence(0, 3) == "!ds"
                    else if (r.ministryId == Ministries.DressCode) it.reason.subSequence(0, 3) == "!zd"
                    else false
                }

                val dayStups: List<MinistryStup> = stups.filter { it.date == r.date }.map {
                    MinistryStup(
                        reason = it.reason,
                        content = it.content,
                        reportId = it.reportId,
                        custom = it.custom
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

            this.respond(
                RMinistryListResponse(
                    kids = kids,
                    forms = if (r.formId == null) {
                        forms.map {
                            Form(
                                id = it.formId,
                                form = FormInit(
                                    title = it.title,
                                    shortTitle = it.shortTitle,
                                    mentorLogin = it.mentorLogin,
                                    classNum = it.classNum
                                ),
                                isActive = true
                            )
                        }
                    } else null
                )
            )
                .done
        }
    }

    suspend fun fetchMinistryHeaderInit(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch ministry header init") {
            val pickedMinistry = StudentMinistry.fetchMinistryWithLogin(this.login)?.ministry ?: "0"
            this.respond(
                RFetchMinistryHeaderInitResponse(
                    isMultiMinistry = this.moderation != Moderation.nothing,
                    pickedMinistry = pickedMinistry
                )
            ).done
        }
    }

    suspend fun createMinistryStudent(call: ApplicationCall) {
        val perm = call.moderation in listOf(Moderation.mentor, Moderation.both, Moderation.moderator)
        call.dRes(perm, "Can't create min student") {
            val r = this.receive<RCreateMinistryStudentReceive>()
            val fioParts = r.studentFIO.split(" ")
            val login = r.login ?: Users.getLoginWithFIO(
                fio = FIO(
                    surname = fioParts[0],
                    name = fioParts[1],
                    praname = fioParts.getOrNull(2)
                ), itShouldBeStudent = r.reason == MinistrySettingsReason.Form
            )!!
            StudentMinistry.set(
                StudentMinistryDTO(
                    login = login, ministry = r.ministryId,
                    lvl = r.lvl
                )
            )

            fetchMinistrySettings(this, reason = r.reason).done
        }
    }

    suspend fun fetchMinistrySettings(call: ApplicationCall, reason: MinistrySettingsReason? = null) {
        val perm = reason != null || (call.isMember)
        call.dRes(perm, "Can't fetch ministry settings") {
            val tReason = reason ?: call.receive<RFetchMinistryStudentsReceive>().reason
            val studentsForMinistry: MutableList<MinistryStudent> = mutableListOf()
            if (tReason == MinistrySettingsReason.Form) {
                val forms = Forms.getAllForms().filter { it.mentorLogin == call.login }
                forms.forEach { form ->
                    val studentLogins = StudentsInForm.fetchStudentsLoginsByFormId(form.formId)
                    studentLogins.forEach { login ->
                        val ministry = StudentMinistry.fetchMinistryWithLogin(login)
                        if (ministry != null) {
                            val user = Users.fetchUser(login)
                            if (user != null && user.isActive) {
                                studentsForMinistry.add(
                                    MinistryStudent(
                                        ministryId = ministry.ministry,
                                        fio = FIO(
                                            name = user.name,
                                            surname = user.surname,
                                            praname = user.praname
                                        ),
                                        form = "${form.classNum}-${form.shortTitle}",
                                        login = login,
                                        lvl = ministry.lvl
                                    )
                                )
                            }
                        }
                    }
                }
            } else {
                val dtos = StudentMinistry.fetchAll()
                dtos.forEach { dto ->
                    val user = Users.fetchUser(dto.login)
                    val formId = StudentsInForm.fetchFormIdOfLoginNullable(dto.login)
                    val formTitle = if (formId != null) {
                        val form = Forms.fetchById(formId)
                        "${form.classNum}-${form.shortTitle}"
                    } else ""
                    if (user != null && user.isActive) {
                        studentsForMinistry.add(
                            MinistryStudent(
                                ministryId = dto.ministry,
                                fio = FIO(
                                    name = user.name,
                                    surname = user.surname,
                                    praname = user.praname
                                ),
                                form = formTitle,
                                login = dto.login,
                                lvl = dto.lvl
                            )
                        )
                    }
                }
            }
            this.respond(
                RFetchMinistrySettingsResponse(
                    studentsForMinistry.filter {
                        it.lvl == "1" || tReason != MinistrySettingsReason.School
                    }
                )
            ).done
        }
    }

    suspend fun fetchFormRating(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch form's rating") {
            val r = call.receive<RFetchFormRatingReceive>()

            val module = getModuleByDate(getCurrentDate().second)

            val forms = if (r.formNum > 9) {
                Forms.getAllForms()
            } else emptyList<FormDTO>()
            val formIds = if (r.formNum > 9) {
                forms.filter { it.classNum == r.formNum }.map { it.formId }
            } else listOf(r.formId)

            val studentsForRating: MutableList<FormRatingStudent> = mutableListOf()
            val edYear = getCurrentEdYear()
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
                            2 -> Marks.fetchModuleAVG(login, module!!.num.toString(), edYear)
                            3 -> Marks.fetchHalfYearAVG(login, module!!.halfNum, edYear)
                            else -> Marks.fetchYearAVG(login, edYear)
                        }

                        val stups = when (r.period) {
                            0 -> Stups.fetchForAWeek(login)
                            1 -> Stups.fetchForAPreviousWeek(login)
                            2 -> Stups.fetchForUserQuarters(
                                login,
                                quartersNum = module!!.num.toString(),
                                isQuarters = true, edYear
                            )

                            3 -> Stups.fetchForUserQuarters(
                                login,
                                quartersNum = module!!.halfNum.toString(),
                                isQuarters = false, edYear
                            )

                            else -> Stups.fetchForUser(login, edYear)
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
                        val dsStupsCount = stups.filter {
                            it.reason.subSequence(0, 3) == "!ds"
                        }.sumOf { it.content.toIntOrNull() ?: 0 }
                        val zdStupsCount = stups.filter {
                            it.reason.subSequence(0, 3) == "!zd"
                        }.sumOf { it.content.toIntOrNull() ?: 0 }
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
                                mvdStupsCount = dsStupsCount,
                                zdStupsCount = zdStupsCount
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
            ).done
        }
    }


    suspend fun fetchFormsForFormRating(call: ApplicationCall) {
        val perm = call.isModer || call.isMentor || call.isTeacher
        call.dRes(perm, "Can't fetch forms (formRating)") {
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
            ).done
        }
    }

    suspend fun fetchLentaData(call: ApplicationCall) {
        val perm = call.isMember
        val r = call.receive<RFetchSchoolDataReceive>()

        call.dRes(perm, "Can't fetch lentaData") {
            val role = Users.getRole(r.login)
            var formName: String? = null
            var formNum: Int? = null
            var formId: Int? = null
            var top: Int? = null
            if (role == Roles.student) {
                formId =
                    StudentsInForm.fetchFormIdOfLogin(r.login)
                val form = Forms.fetchById(formId)
                formNum = form.classNum
                if (formNum > 9) {
                    formName = formNum.toString()
                } else {
                    formName = "$formNum-${form.title.uppercase()}"
                }
                val table = when(formNum) {
                    1 -> RatingLowSchoolTable
                    2 -> RatingHighSchoolTable
                    else -> RatingCommonSchoolTable
                }
                top = table.fetchRatingOf(
                    r.login,
                    ExtraSubjectsId.common,
                    edYear = getCurrentEdYear(),
                    period = rating.PansionPeriod.Week(getCurrentWeek().num)
                )?.top
            } else if (role != Moderation.nothing) {
                val form = Forms.fetchMentorForms(r.login).firstOrNull()
                if (form != null) {
                    formName = form.num.toString()
                    formNum = form.num
                    formId = form.id
                }
            }

            val ministryId = StudentMinistry.fetchMinistryWithLogin(r.login)?.ministry ?: "0"
            val weekStups = Stups.fetchForAWeek(login = r.login) //, date = getCurrentDate().second

            call.respond(
                RFetchSchoolDataResponse(
                    formId = formId,
                    formName = formName,
                    formNum = formNum,
                    top = top,
                    ministryId = ministryId,
                    mvdStups = weekStups.filter { it.reason.subSequence(0, 3) == "!ds" && it.content.contains("-") }
                        .sumOf { it.content.toIntOrNull() ?: 0 },
                    zdStups = weekStups.filter { it.reason.subSequence(0, 3) == "!zd" && it.content.contains("-") }
                        .sumOf { it.content.toIntOrNull() ?: 0 }
                )
            ).done
        }
    }
}