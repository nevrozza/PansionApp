package com.nevrozq.pansion.database.ratingTable

import ForAvg
import achievements.AchievementsDTO
import com.nevrozq.pansion.database.achievements.Achievements
import com.nevrozq.pansion.database.calendar.Calendar
import com.nevrozq.pansion.database.forms.Forms
import com.nevrozq.pansion.database.groups.Groups
import com.nevrozq.pansion.database.ratingEntities.Marks
import com.nevrozq.pansion.database.ratingEntities.RatingEntityDTO
import com.nevrozq.pansion.database.ratingEntities.Stups
import com.nevrozq.pansion.database.studentGroups.StudentGroups
import com.nevrozq.pansion.database.studentsInForm.StudentsInForm
import com.nevrozq.pansion.database.subjects.Subjects
import com.nevrozq.pansion.database.users.UserDTO
import com.nevrozq.pansion.database.users.Users
import com.nevrozq.pansion.utils.getModuleByDate
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import server.cut
import server.getCurrentDate
import server.getLocalDate
import server.getPreviousWeekDays
import server.getWeekDays
import server.roundTo


private data class AddItem(
    val subjectId: Int,
    val stups: Int,
    val avg: String,
    val table: RatingTable
)

private data class RTables(
    val weekT: RatingTable,
    val previousWeekT: RatingTable,
    val moduleT: RatingTable,
    val yearT: RatingTable
)

private data class IterationsMode(
    val students: List<UserDTO>,
    val r: RTables
)

private fun MutableList<AddItem>.pAdd(
    subjectId: Int,
    table: RatingTable,
    stups: List<RatingEntityDTO>,
    avg: ForAvg,
    achievementCount: Int? = null
) {
    if (stups.sumOf { it.content.toInt() } + (achievementCount ?: 0) > 0 && avg.count > 0) {
        this.add(
            AddItem(
                subjectId = subjectId,
                stups = stups.sumOf { it.content.toInt() } + (achievementCount ?: 0),
                avg = (avg.sum / avg.count.toFloat()).roundTo(2).toString(),
                table = table
            )
        )
    }
}

fun getModuleDays(moduleDay: String): Pair<String, String?> {
    val module = Calendar.getModuleStartEnd(moduleDay.toIntOrNull() ?: 0)
    return module ?: Pair("01.01.2000", null)
}

private fun initItems(login: String, r: RTables): MutableList<AddItem> {
    val module = getModuleByDate(date = getCurrentDate().second)?.num.toString()
    val items: MutableList<AddItem> = mutableListOf()

    val yearAchievements = Achievements.fetchAllByLoginInit(login)
    val weekAchievements: MutableList<AchievementsDTO> = mutableListOf()
    val previousWeekAchievements: MutableList<AchievementsDTO> = mutableListOf()
    val moduleAchievements: MutableList<AchievementsDTO> = mutableListOf()
    yearAchievements.forEach {
        val date = (if (((it.showDate)?.length ?: 0) > 5) it.showDate ?: it.date else it.date)
        val epoch = getLocalDate(date).toEpochDays()
        val pair = getModuleDays(module)
        val start = getLocalDate(pair.first)
        val end = if (pair.second != null) getLocalDate(pair.second!!) else null

        if (date in getWeekDays()) {
            weekAchievements.add(it)
        } else if (date in getPreviousWeekDays()) {
            previousWeekAchievements.add(it)
        }
        if (epoch >= start.toEpochDays() && (end == null || epoch < (end.toEpochDays() ?: 0))) {
            moduleAchievements.add(it)
        }
    }


    val stupsWeekCount = Stups.fetchForAWeek(login)
    val stupsPreviousWeekCount = Stups.fetchForAPreviousWeek(login)
    val stupsYearCount = Stups.fetchForUser(login)
    val stupsModuleCount = stupsYearCount.filter {
        it.part == module
    }


    // -1 ALL STUPS
    val allStupsWeekCount = stupsWeekCount.filter {
        it.reason.subSequence(0, 3) == "!st"
    }
    val allStupsPreviousWeekCount = stupsPreviousWeekCount.filter {
        it.reason.subSequence(0, 3) == "!st"
    }
    val allStupsYearCount = stupsYearCount.filter {
        it.reason.subSequence(0, 3) == "!st"
    }
    val allStupsModuleCount = stupsModuleCount.filter {
        it.reason.subSequence(0, 3) == "!st"
    }
    // -2 MVD STUPS
    val dsStupsWeekCount = stupsWeekCount.filter {
        it.reason.subSequence(0, 3) == "!ds"
    }
    val dsStupsPreviousWeekCount = stupsPreviousWeekCount.filter {
        it.reason.subSequence(0, 3) == "!ds"
    }
    val dsStupsYearCount = stupsYearCount.filter {
        it.reason.subSequence(0, 3) == "!ds"
    }

    val dsPreviousWeekAchievementsCount = previousWeekAchievements.filter {
        it.subjectId == -2
    }.sumOf { it.stups }
    val dsWeekAchievementsCount = weekAchievements.filter {
        it.subjectId == -2
    }.sumOf { it.stups }
    val dsYearAchievementsCount = yearAchievements.filter {
        it.subjectId == -2
    }.sumOf { it.stups }
    val dsStupsModuleCount = stupsModuleCount.filter {
        it.reason.subSequence(0, 3) == "!ds"
    }
    val dsModuleAchievementsCount = moduleAchievements.filter {
        it.subjectId == -2
    }.sumOf { it.stups }


    val allWeekAvg = Marks.fetchWeekAVG(login)
    val allPreviousWeekAvg = Marks.fetchPreviousWeekAVG(login)
    val allYearAvg = Marks.fetchYearAVG(login)
    val allModuleAvg = Marks.fetchModuleAVG(login, module = module)

    val subjectsYearAchievements = Achievements.fetchAllByLoginNoInit(login)
    val subjectsWeekAchievements: MutableList<AchievementsDTO> = mutableListOf()
    val subjectsPreviousWeekAchievements: MutableList<AchievementsDTO> = mutableListOf()
    val subjectsModuleAchievements: MutableList<AchievementsDTO> = mutableListOf()
    subjectsYearAchievements.forEach {
        val date = (if (((it.showDate)?.length ?: 0) > 5) it.showDate ?: it.date else it.date)
        val epoch = getLocalDate(date).toEpochDays()
        val pair = getModuleDays(module)
        val start = getLocalDate(pair.first)
        val end = if (pair.second != null) getLocalDate(pair.second!!) else null

        if (date in getWeekDays()) {
            subjectsWeekAchievements.add(it)
        } else if (date in getPreviousWeekDays()) {
            subjectsPreviousWeekAchievements.add(it)
        }
        if (epoch >= start.toEpochDays() && (end == null || epoch < (end.toEpochDays() ?: 0))) {
            subjectsModuleAchievements.add(it)
        }
    }

    items.pAdd(subjectId = -1, stups = allStupsWeekCount, avg = allWeekAvg, table = r.weekT,
            achievementCount = subjectsWeekAchievements.sumOf { it.stups }
        )
    items.pAdd(subjectId = -1, stups = allStupsPreviousWeekCount, avg = allPreviousWeekAvg, table = r.previousWeekT,
            achievementCount = subjectsPreviousWeekAchievements.sumOf { it.stups }
        )
    items.pAdd(subjectId = -1, stups = allStupsYearCount, avg = allYearAvg, table = r.yearT,
        achievementCount = subjectsYearAchievements.sumOf { it.stups }
    )
    items.pAdd(subjectId = -1, stups = allStupsModuleCount, avg = allModuleAvg, table = r.moduleT,
        achievementCount = subjectsModuleAchievements.sumOf { it.stups }
    )

    items.pAdd(
        subjectId = -2,
        stups = dsStupsWeekCount,
        avg = allWeekAvg,
        table = r.weekT,
        achievementCount = dsWeekAchievementsCount
    )
    items.pAdd(
        subjectId = -2,
        stups = dsStupsPreviousWeekCount,
        avg = allPreviousWeekAvg,
        table = r.previousWeekT,
        achievementCount = dsPreviousWeekAchievementsCount
    )
    items.pAdd(
        subjectId = -2,
        stups = dsStupsYearCount,
        avg = allYearAvg,
        table = r.yearT,
        achievementCount = dsYearAchievementsCount
    )
    items.pAdd(
        subjectId = -2,
        stups = dsStupsModuleCount,
        avg = allModuleAvg,
        table = r.moduleT,
        achievementCount = dsModuleAchievementsCount
    )


    val socialWeekAchievementsCount = weekAchievements.filter {
        it.subjectId == -3
    }.sumOf { it.stups }
    val socialPreviousWeekAchievementsCount = previousWeekAchievements.filter {
        it.subjectId == -3
    }.sumOf { it.stups }
    val socialYearAchievementsCount = yearAchievements.filter {
        it.subjectId == -3
    }.sumOf { it.stups }
    val socialModuleAchievementsCount = moduleAchievements.filter {
        it.subjectId == -3
    }.sumOf { it.stups }

    items.pAdd(
        subjectId = -3,
        stups = listOf(),
        avg = allWeekAvg,
        table = r.weekT,
        achievementCount = socialWeekAchievementsCount
    )
    items.pAdd(
        subjectId = -3,
        stups = listOf(),
        avg = allPreviousWeekAvg,
        table = r.previousWeekT,
        achievementCount = socialPreviousWeekAchievementsCount
    )
    items.pAdd(
        subjectId = -3,
        stups = listOf(),
        avg = allYearAvg,
        table = r.yearT,
        achievementCount = socialYearAchievementsCount
    )
    items.pAdd(
        subjectId = -3,
        stups = listOf(),
        avg = allModuleAvg,
        table = r.moduleT,
        achievementCount = socialModuleAchievementsCount
    )

    val creatorWeekAchievementsCount = weekAchievements.filter {
        it.subjectId == -4
    }.sumOf { it.stups }
    val creatorPreviousWeekAchievementsCount = previousWeekAchievements.filter {
        it.subjectId == -4
    }.sumOf { it.stups }
    val creatorYearAchievementsCount = yearAchievements.filter {
        it.subjectId == -4
    }.sumOf { it.stups }
    val creatorModuleAchievementsCount = moduleAchievements.filter {
        it.subjectId == -4
    }.sumOf { it.stups }

    items.pAdd(
        subjectId = -4,
        stups = listOf(),
        avg = allWeekAvg,
        table = r.weekT,
        achievementCount = creatorWeekAchievementsCount
    )
    items.pAdd(
        subjectId = -4,
        stups = listOf(),
        avg = allPreviousWeekAvg,
        table = r.previousWeekT,
        achievementCount = creatorPreviousWeekAchievementsCount
    )
    items.pAdd(
        subjectId = -4,
        stups = listOf(),
        avg = allYearAvg,
        table = r.yearT,
        achievementCount = creatorYearAchievementsCount
    )
    items.pAdd(
        subjectId = -4,
        stups = listOf(),
        avg = allModuleAvg,
        table = r.moduleT,
        achievementCount = creatorModuleAchievementsCount
    )
    return items
}

fun updateRatings() {

    val module = getModuleByDate(getCurrentDate().second)?.num.toString()
    transaction {
        for (i in listOf(
            RatingWeek0Table,
            RatingWeek1Table,
            RatingWeek2Table,
            RatingPreviousWeek0Table,
            RatingPreviousWeek1Table,
            RatingPreviousWeek2Table,
            RatingModule0Table,
            RatingModule1Table,
            RatingModule2Table,
            RatingYear0Table,
            RatingYear1Table,
            RatingYear2Table,
        )) {
            i.deleteAll()
        }
    }


    val forms = Forms.getAllForms().sortedBy { it.isActive }
    val groups = Groups.getAllGroups().sortedBy { it.isActive }
    val subjects = Subjects.fetchAllSubjects().sortedBy { it.isActive }
    val studentsInGroup = StudentGroups.fetchAll()
    val studentsInForm = StudentsInForm.fetchAll()
    val students0 = Users.fetchAllStudents()
        .filter { s ->
            s.isActive &&
                    studentsInForm.firstOrNull { it.login == s.login } != null &&
                    studentsInGroup.firstOrNull { it.studentLogin == s.login } != null
        }

    val students1 = students0.filter { s ->
        forms.first { it.formId == studentsInForm.first { it.login == s.login }.formId }.classNum in (5..8)
    }

    val students2 = students0.filter { s ->
        forms.first { it.formId == studentsInForm.first { it.login == s.login }.formId }.classNum in (9..11)
    }

    val iterationsMode = listOf(
        IterationsMode(
            students = students0,
            r = RTables(
                weekT = RatingWeek0Table,
                previousWeekT = RatingPreviousWeek0Table,
                moduleT = RatingModule0Table,
                yearT = RatingYear0Table
            )
        ),
        IterationsMode(
            students = students1,
            r = RTables(
                weekT = RatingWeek1Table,
                previousWeekT = RatingPreviousWeek1Table,
                moduleT = RatingModule1Table,
                yearT = RatingYear1Table
            )
        ),
        IterationsMode(
            students = students2,
            r = RTables(
                weekT = RatingWeek2Table,
                previousWeekT = RatingPreviousWeek2Table,
                moduleT = RatingModule2Table,
                yearT = RatingYear2Table
            )
        ),
    )
    iterationsMode.forEach { x ->
        for (s in x.students) {
            val achievements = Achievements.fetchAllByLoginNoInit(s.login)


            val groupList = studentsInGroup.filter { it.studentLogin == s.login }
            val subjectList = subjects.filter {
                it.id in groupList.map { l -> l.subjectId }
            }
            val form =
                forms.first { studentsInForm.first { it.login == s.login }.formId == it.formId }

            val items = initItems(s.login, x.r)
            val weekStups = Stups.fetchForAWeek(login = s.login).filter {
                it.reason.subSequence(0, 3) == "!st"
            }
            val previousWeekStups = Stups.fetchForAPreviousWeek(login = s.login).filter {
                it.reason.subSequence(0, 3) == "!st"
            }
            val yearStups = Stups.fetchForUser(login = s.login).filter {
                it.reason.subSequence(0, 3) == "!st"
            }
            for (i in subjectList) {
                val yearSubjectAchievements = achievements.filter {
                    it.subjectId == i.id
                }

                val stupsWeekCount = weekStups
                    .filter { it.subjectId == i.id }
                val stupsPreviousWeekCount = previousWeekStups
                    .filter { it.subjectId == i.id }

                val stupsYearCount =
                    yearStups.filter { it.subjectId == i.id }
                val stupsModuleCount = yearStups.filter {
                    it.subjectId == i.id &&
                            it.part == module
                }

                val weekAvg = Marks.fetchWeekSubjectAVG(login = s.login, subjectId = i.id)
                val previousWeekAvg = Marks.fetchPreviousWeekSubjectAVG(login = s.login, subjectId = i.id)
                val yearAvg = Marks.fetchYearSubjectAVG(login = s.login, subjectId = i.id)
                val moduleAvg =
                    Marks.fetchModuleSubjectAVG(login = s.login, subjectId = i.id, module = module)

                val weekAchievements: MutableList<AchievementsDTO> = mutableListOf()
                val previousWeekAchievements: MutableList<AchievementsDTO> = mutableListOf()
                val moduleAchievements: MutableList<AchievementsDTO> = mutableListOf()
                yearSubjectAchievements.forEach {
                    val date =
                        (if (((it.showDate)?.length ?: 0) > 5) it.showDate ?: it.date else it.date)
                    val epoch = getLocalDate(date).toEpochDays()
                    val pair = getModuleDays(module)
                    val start = getLocalDate(pair.first)
                    val end = if (pair.second != null) getLocalDate(pair.second!!) else null

                    if (date in getWeekDays()) {
                        weekAchievements.add(it)
                    } else if (date in getPreviousWeekDays()) {
                        previousWeekAchievements.add(it)
                    }
                    if (epoch >= start.toEpochDays() && (end == null || epoch < (end.toEpochDays()
                            ?: 0))
                    ) {
                        moduleAchievements.add(it)
                    }
                }

                val weekCount = weekAchievements.sumOf { it.stups }
                val previousWeekCount = previousWeekAchievements.sumOf { it.stups }
                val moduleCount = moduleAchievements.sumOf { it.stups }
                val yearCount = yearSubjectAchievements.sumOf { it.stups }

                items.pAdd(
                    subjectId = i.id,
                    stups = stupsWeekCount,
                    avg = weekAvg,
                    table = x.r.weekT,
                    achievementCount = weekCount
                )
                items.pAdd(
                    subjectId = i.id,
                    stups = stupsPreviousWeekCount,
                    avg = previousWeekAvg,
                    table = x.r.previousWeekT,
                    achievementCount = previousWeekCount
                )
                items.pAdd(
                    subjectId = i.id,
                    stups = stupsModuleCount,
                    avg = moduleAvg,
                    table = x.r.moduleT,
                    achievementCount = moduleCount
                )
                items.pAdd(
                    subjectId = i.id,
                    stups = stupsYearCount,
                    avg = yearAvg,
                    table = x.r.yearT,
                    achievementCount = yearCount
                )
            }

            for (y in items) {
                y.table.insert(
                    RatingTableDTO(
                        login = s.login,
                        name = s.name,
                        surname = s.surname,
                        praname = s.praname,
                        avatarId = s.avatarId,
                        stups = y.stups,
                        avg = y.avg,
                        top = 0,
                        groupName = if (y.subjectId > 0) groups.first { it.id == groupList.first { it.subjectId == y.subjectId }.groupId }.name else if (y.subjectId == -1) "Общий рейтинг" else "Дисциплина",
                        formNum = form.classNum,
                        subjectId = y.subjectId,
                        formShortTitle = form.shortTitle
                    )
                )
            }

        }
    }

    sortRatings()
}

private fun sortRatings() {
    for (i in listOf(
        RatingWeek0Table,
        RatingWeek1Table,
        RatingWeek2Table,
        RatingPreviousWeek0Table,
        RatingPreviousWeek1Table,
        RatingPreviousWeek2Table,
        RatingModule0Table,
        RatingModule1Table,
        RatingModule2Table,
        RatingYear0Table,
        RatingYear1Table,
        RatingYear2Table,
    )) {
        var top = 0
        var previousSubjectId = 0
        val items = i.fetchAllRatings().filter { it.stups > 0 && it.avg.toFloat() >= 4 }.sortedWith(
            compareBy({ it.subjectId }, { it.stups }, {it.avg.toFloat()})
        ).reversed().map { x ->
            if (previousSubjectId != x.subjectId) {
                previousSubjectId = x.subjectId
                top = 0
            }
            top++
            x.copy(top = top)

        }
        transaction {
            i.deleteAll()
            i.saveRatings(items)

        }
    }
}


// It's easier....
object RatingWeek0Table : RatingTable()
object RatingWeek1Table : RatingTable()
object RatingWeek2Table : RatingTable()

object RatingPreviousWeek0Table : RatingTable()
object RatingPreviousWeek1Table : RatingTable()
object RatingPreviousWeek2Table : RatingTable()

object RatingModule0Table : RatingTable()
object RatingModule1Table : RatingTable()
object RatingModule2Table : RatingTable()

object RatingYear0Table : RatingTable()
object RatingYear1Table : RatingTable()
object RatingYear2Table : RatingTable()