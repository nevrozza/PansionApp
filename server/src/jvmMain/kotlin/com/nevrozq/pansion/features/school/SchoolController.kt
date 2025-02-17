package com.nevrozq.pansion.features.school

import FIO
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
import com.nevrozq.pansion.database.holidays.Holidays
import com.nevrozq.pansion.database.ratingEntities.Stups
import com.nevrozq.pansion.database.ratingTable.RatingCommonSchoolTable
import com.nevrozq.pansion.database.ratingTable.RatingHighSchoolTable
import com.nevrozq.pansion.database.ratingTable.RatingLowSchoolTable
import com.nevrozq.pansion.database.studentLines.StudentLines
import com.nevrozq.pansion.database.studentMinistry.StudentMinistry
import com.nevrozq.pansion.database.studentMinistry.StudentMinistryDTO
import com.nevrozq.pansion.database.studentsInForm.StudentsInForm
import com.nevrozq.pansion.database.subjects.Subjects
import com.nevrozq.pansion.database.users.UserDTO
import com.nevrozq.pansion.database.users.Users
import com.nevrozq.pansion.utils.dRes
import com.nevrozq.pansion.utils.done
import com.nevrozq.pansion.utils.getCurrentWeek
import com.nevrozq.pansion.utils.getModuleByDate
import com.nevrozq.pansion.utils.isMember
import com.nevrozq.pansion.utils.isMentor
import com.nevrozq.pansion.utils.isModer
import com.nevrozq.pansion.utils.isOnlyMentor
import com.nevrozq.pansion.utils.isStudent
import com.nevrozq.pansion.utils.isTeacher
import com.nevrozq.pansion.utils.login
import com.nevrozq.pansion.utils.moderation
import getWeeks
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import main.RFetchSchoolDataReceive
import main.RFetchSchoolDataResponse
import main.school.DutyKid
import main.school.MinistryKid
import main.school.MinistryLesson
import main.school.MinistrySettingsReason
import main.school.MinistryStudent
import main.school.MinistryStup
import main.school.RCreateMinistryStudentReceive
import main.school.RFetchDutyReceive
import main.school.RFetchDutyResponse
import main.school.RFetchMinistryHeaderInitResponse
import main.school.RFetchMinistrySettingsResponse
import main.school.RFetchMinistryStudentsReceive
import main.school.RMinistryListReceive
import main.school.RMinistryListResponse
import main.school.RStartNewDayDuty
import main.school.RUpdateTodayDuty
import main.school.RUploadMinistryStup
import mentoring.MentorForms
import rating.FormRatingStudent
import rating.FormRatingStup
import rating.PansionPeriod
import rating.RFetchFormRatingReceive
import rating.RFetchFormRatingResponse
import rating.RFetchFormsForFormResponse
import server.ExtraSubjectsId
import server.Ministries
import server.Moderation
import server.Roles
import server.getCurrentDate
import server.getCurrentEdYear
import server.getWeekDays

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
            val mentorLogin = if (user?.role == Roles.STUDENT) {
                val formId = StudentsInForm.fetchFormIdOfLogin(user.login)
                Forms.fetchById(formId).mentorLogin
            } else if (user?.moderation in listOf(Moderation.BOTH, Moderation.MENTOR)) {
                user?.login!!
            } else null!!

            var dutyList: MutableList<String> = Duty.fetchByMentorLogin(mentorLogin).toMutableList()

            val forms = Forms.fetchMentorForms(mentorLogin)
            val logins = StudentsInForm.fetchStudentsLoginsByFormIds(forms.map { it.id })
            val dutyListShouldBe =
                Users.fetchByLoginsActivated(logins).sortedWith(compareBy({ it.surname }, { it.name }))
                    .map { it.login }.toMutableList()

            val sortedSetShould = dutyListShouldBe.toSortedSet()
            val sortedSet = dutyList.toSortedSet()

            if (dutyList.isEmpty()) {
                dutyList = dutyListShouldBe
                Duty.enterList(
                    mentorLogin,
                    dutyList
                )
            } else if (sortedSetShould != sortedSet) {
                val toDelete = sortedSet - dutyListShouldBe
                val toAdd = dutyListShouldBe - sortedSet
                dutyList.removeAll(toDelete)
                dutyList.addAll(toAdd)
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
            Ministries.DRESS_CODE,
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
            val isShowFull = call.isModer || (ministry?.ministry == r.ministryId && ministry.lvl == "1")
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
                    else if (r.ministryId == Ministries.DRESS_CODE) it.reason.subSequence(0, 3) == "!zd"
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
                    isMultiMinistry = this.moderation != Moderation.NOTHING,
                    pickedMinistry = pickedMinistry
                )
            ).done
        }
    }

    suspend fun createMinistryStudent(call: ApplicationCall) {
        val perm = call.moderation in listOf(Moderation.MENTOR, Moderation.BOTH, Moderation.MODERATOR)
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
            val weeks = getWeeks(edYear = edYear, holidays = Holidays.fetch(edYear).filter { it.isForAll })

            formIds.forEach { formId ->
                val studentLogins = StudentsInForm.fetchStudentsLoginsByFormId(formId)
                studentLogins.forEach { login ->
                    val user = Users.fetchUser(login)
                    if (user != null) {

                        //ForAvg
                        //List<FormRatingStup>
                        //likes dislikes



                        val stups = when (r.period) {
                            is PansionPeriod.Week -> {
                                Stups.fetchForPeriod(
                                    login = login,
                                    period = weeks.first { x -> x.num == (r.period as PansionPeriod.Week).num }.dates
                                )
                            }

                            is PansionPeriod.Module -> {
                                Stups.fetchForUserQuarters(
                                    login,
                                    quartersNum = (r.period as PansionPeriod.Module).num.toString(),
                                    isQuarters = true, edYear
                                )
                            }

                            is PansionPeriod.Half -> {
                                Stups.fetchForUserQuarters(
                                    login,
                                    quartersNum = (r.period as PansionPeriod.Half).num.toString(),
                                    isQuarters = false, edYear
                                )
                            }
                            is PansionPeriod.Year -> Stups.fetchForUser(login, edYear)
                            else -> Stups.fetchForPeriod(
                                login = login,
                                period = weeks.last().dates
                            )
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
                        val rating = RatingCommonSchoolTable.fetchRatingOf(
                            login = login,
                            subjectId = ExtraSubjectsId.COMMON,
                            edYear = edYear,
                            period = r.period ?: PansionPeriod.Week(weeks.last().num)
                        )
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
                                avg = rating?.avg ?: "0.0",
                                edStups = edStups,
                                mvdStupsCount = dsStupsCount,
                                zdStupsCount = zdStupsCount,
                                avgAlg = rating?.avgAlg ?: 0f,
                                stupsAlg = rating?.stupsAlg ?: 0f
                            )
                        )
                    }
                }
            }
            call.respond(
                RFetchFormRatingResponse(
                    studentsForRating,
                    Subjects.fetchAllSubjectsAsMap(),
                    currentModule = module?.num ?: 1,
                    currentHalf = module?.halfNum ?: 1,
                    currentWeek = weeks.last().num
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
            if (role == Roles.STUDENT) {
                formId =
                    StudentsInForm.fetchFormIdOfLogin(r.login)
                val form = Forms.fetchById(formId)
                formNum = form.classNum
                if (formNum > 9) {
                    formName = formNum.toString()
                } else {
                    formName = "$formNum-${form.title.uppercase()}"
                }
                val table = when (formNum) {
                    1 -> RatingLowSchoolTable
                    2 -> RatingHighSchoolTable
                    else -> RatingCommonSchoolTable
                }
                top = table.fetchRatingOf(
                    r.login,
                    ExtraSubjectsId.COMMON,
                    edYear = getCurrentEdYear(),
                    period = PansionPeriod.Week(getCurrentWeek().num)
                )?.top
            } else if (role != Moderation.NOTHING) {
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