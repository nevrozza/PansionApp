package com.nevrozq.pansion.database.ratingTable

import ForAvg
import achievements.AchievementsDTO
import com.nevrozq.pansion.database.achievements.Achievements
import com.nevrozq.pansion.database.calendar.Calendar
import com.nevrozq.pansion.database.forms.Forms
import com.nevrozq.pansion.database.groups.Groups
import com.nevrozq.pansion.database.holidays.Holidays
import com.nevrozq.pansion.database.ratingEntities.Marks
import com.nevrozq.pansion.database.ratingEntities.RatingEntityDTO
import com.nevrozq.pansion.database.ratingEntities.Stups
import com.nevrozq.pansion.database.studentGroups.StudentGroups
import com.nevrozq.pansion.database.studentsInForm.StudentsInForm
import com.nevrozq.pansion.database.subjects.Subjects
import com.nevrozq.pansion.database.users.UserDTO
import com.nevrozq.pansion.database.users.Users
import com.nevrozq.pansion.utils.getModuleByDate
import getWeeks
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import rating.PansionPeriod
import rating.toStr
import server.*

fun getModuleDays(moduleDay: String): Pair<String, String?> {
    val module = Calendar.getModuleStartEnd(moduleDay.toIntOrNull() ?: 0)
    return module ?: Pair("01.01.2000", null)
}

//private data class RatingPeriodsOutput(
//    val m: Int?,
//    val w: Int?,
//    val h: Int?,
//)
//
//private fun getRatingPeriods(
//    weeks: List<String>,
//    date: String
//) : RatingPeriodsOutput {
//    val mm = getModuleByDate(date)
//    val m = mm?.num
//    val w = weeks.firstOrNull { date in it.dates }?.num
//    val h = mm?.halfNum
//    return
//}

data class RatingItem(
    val subjectId: Int,
    val avg: String,
    val stups: Int,
    val period: PansionPeriod
)

private fun getAvgSafely(sum: Int, count: Int): String {
//    ((yearMarks.sumOf { it.content.toIntOrNull() ?: 0 } / 1f) / yearMarks.size).roundTo(2)
    return if (count > 0) ((sum / 1f) / count).roundTo(2) else "0.0"
}



private fun initItems(login: String, studentSubjects: List<Int>): List<RatingItem> {
    val output = mutableListOf<RatingItem>()


    val edYear = getCurrentEdYear()

    val rawModule = getModuleByDate(getCurrentDate().second)
    val module = getModuleByDate(getCurrentDate().second)?.num ?: 1

    val modules = Calendar.getAllModules().filter { it.num <= module }
    val weeks = getWeeks(
        edYear = edYear,
        holidays = Holidays.fetchAll().filter { it.isForAll }
    )

    val half1Modules = Calendar.getAllModulesOfHalf(1)
    val halfs = 1..(rawModule?.halfNum ?: 1)


    val yearAchievements = Achievements.fetchAllByLogin(login, edYear)
    val weekAchievements: MutableMap<Int, List<AchievementsDTO>> = mutableMapOf()
    val moduleAchievements: MutableMap<Int, List<AchievementsDTO>> = mutableMapOf()
    val halfAchievements: MutableMap<Int, List<AchievementsDTO>> = mutableMapOf()

    yearAchievements.forEach { x ->
        val date = x.showDate ?: x.date
        val mm = getModuleByDate(date)
        val m = mm?.num
        val w = weeks.firstOrNull { date in it.dates }?.num
        val h = mm?.halfNum
        listOf(m to moduleAchievements, w to weekAchievements, h to halfAchievements).forEach { (key, map) ->
            map.updateSafe(key, x)
        }
    }

    val yearStups = Stups.fetchForUser(login, edYear)
    val weekStups: MutableMap<Int, List<RatingEntityDTO>> = mutableMapOf()
    val moduleStups: MutableMap<Int, List<RatingEntityDTO>> = mutableMapOf()
    val halfStups: MutableMap<Int, List<RatingEntityDTO>> = mutableMapOf()

    val yearMarks = Marks.fetchForUser(login, edYear)
    val weekMarks: MutableMap<Int, List<RatingEntityDTO>> = mutableMapOf()
    val moduleMarks: MutableMap<Int, List<RatingEntityDTO>> = mutableMapOf()
    val halfMarks: MutableMap<Int, List<RatingEntityDTO>> = mutableMapOf()

    listOf(
        yearStups to listOf(weekStups, moduleStups, halfStups),
        yearMarks to listOf(weekMarks, moduleMarks, halfMarks)
    ).forEach { (year, other) ->
        val weekL = other[0]
        val moduleL = other[1]
        val halfL = other[2]
        year.forEach { x ->
            val date = x.date
            val m = x.part.toInt()
            val w = weeks.firstOrNull { date in it.dates }?.num
            val h = if (m in half1Modules) 1 else 2
            listOf(m to moduleL, w to weekL, h to halfL).forEach { (key, map) ->
                map.updateSafe(key, x)
            }
        }
    }

    // YEAR COMMON ExtraSubjectsId.common
    val yearCommonAvg = getAvgSafely(yearMarks.sumOf { it.content.toInt() }, yearMarks.size)
    output.add(
        RatingItem(
            subjectId = ExtraSubjectsId.common,
            avg = yearCommonAvg,
            stups =
                yearAchievements.filter {
                    it.subjectId !in listOf(
                        ExtraSubjectsId.creative,
                        ExtraSubjectsId.mvd,
                        ExtraSubjectsId.social
                    )
                }.sumOf { it.stups }
                        + yearStups.filter { it.reason.st == "!st" }.sumOf { it.content.toInt() },
            period = PansionPeriod.Year
        )
    )

    // Year Subjects
    studentSubjects.forEach { subjectId ->
        val achievements = yearAchievements.filter { it.subjectId == subjectId }.sumOf { it.stups }
        val stups = yearStups.filter { it.reason.st == "!st" && it.subjectId == subjectId }.sumOf { it.content.toInt() }
        val marks = yearMarks.filter { it.subjectId == subjectId }
        val avg = getAvgSafely(sum = marks.sumOf { it.content.toInt() }, count = marks.size)
        output.add(
            RatingItem(
                subjectId = subjectId,
                avg = avg,
                stups = stups + achievements,
                period = PansionPeriod.Year
            )
        )
    }

    listOf(
        weeks.map { it.num } to ("w" to Triple(weekAchievements, weekStups, weekMarks)),
        modules.map { it.num } to ("m" to Triple(moduleAchievements, moduleStups, moduleMarks)),
        halfs.map { it } to ("h" to Triple(halfAchievements, halfStups, halfMarks))
    ).forEach {
        val periods = it.first
        val type = it.second.first
        val maps = it.second.second
        val r_achievements = maps.first
        val r_stups = maps.second
        val r_marks = maps.third
        periods.forEach { num ->
            val period = when (type) {
                "w" -> PansionPeriod.Week(num)
                "m" -> PansionPeriod.Module(num)
                "h" -> PansionPeriod.Half(num)
                else -> PansionPeriod.Half(num)
            }
            val l_achievements = (r_achievements[num] ?: listOf())
            val l_stups = (r_stups[num] ?: listOf())
            val l_ed_stups = (r_stups[num] ?: listOf()).filter { it.reason.st == "!st" }
            val l_marks = (r_marks[num] ?: listOf())
            // COMMON ExtraSubjectsId.common
            run {
                val achievements = l_achievements
                    .filter {
                        it.subjectId !in listOf(
                            ExtraSubjectsId.creative,
                            ExtraSubjectsId.mvd,
                            ExtraSubjectsId.social
                        )
                    }.sumOf { it.stups }
                val stups = l_ed_stups
                    .sumOf { it.content.toInt() }
                val marks = l_marks
                val avg = getAvgSafely(sum = marks.sumOf { it.content.toInt() }, count = marks.size)

                output.add(
                    RatingItem(
                        subjectId = ExtraSubjectsId.common,
                        avg = avg,
                        stups = achievements + stups,
                        period = period
                    )
                )
            }


            // Subjects
            studentSubjects.forEach { subjectId ->
                val achievements = l_achievements.filter { it.subjectId == subjectId }.sumOf { it.stups }
                val stups = l_ed_stups.filter { it.subjectId == subjectId }.sumOf { it.content.toInt() }
                val marks = l_marks.filter { it.subjectId == subjectId }
                val avg = getAvgSafely(sum = marks.sumOf { it.content.toInt() }, count = marks.size)
                output.add(
                    RatingItem(
                        subjectId = subjectId,
                        avg = avg,
                        stups = stups + achievements,
                        period = period
                    )
                )
            }
        }

    }



    return output
}

fun updateRatings(edYear: Int) {
    println("wazap-start")
    val forms = Forms.getAllForms().sortedBy { it.isActive }
    val groups = Groups.getAllGroups().sortedBy { it.isActive }
    val subjects = Subjects.fetchAllSubjects().sortedBy { it.isActive }
    val studentsInGroup = StudentGroups.fetchAll()
    val studentsInForm = StudentsInForm.fetchAll()
    val students = Users.fetchAllStudents()
        .filter { s ->
            s.isActive &&
                    studentsInForm.firstOrNull { it.login == s.login } != null &&
                    studentsInGroup.firstOrNull { it.studentLogin == s.login } != null
        }

    val commonRating = mutableListOf<RatingTableDTO>()
    val highRating = mutableListOf<RatingTableDTO>()
    val lowRating = mutableListOf<RatingTableDTO>()

    transaction {
        listOf(RatingCommonSchoolTable, RatingHighSchoolTable, RatingLowSchoolTable).forEach { table ->
            table.deleteWhere { table.edYear eq edYear }
        }
    }
    students.forEach { s ->
        val form = forms.first { it.formId == studentsInForm.first { it.login == s.login }.formId }
        val isLow = form.classNum <= 8//in (5..8)
        val secondTable = if (isLow) lowRating else highRating
        val studentGroups = studentsInGroup.filter { it.studentLogin == s.login }
        val items = initItems(
            login = s.login,
            studentSubjects = studentGroups.map { it.subjectId }
        )

        listOf(commonRating, secondTable).forEach { table ->
            table.addAll(
                items.map { item ->
//                    val subject = subjects.firstOrNull { it.id == item.subjectId }
                    val group =
                        groups.firstOrNull { it.subjectId == item.subjectId && it.id in studentGroups.map { it.groupId } }
                    RatingTableDTO(
                        login = s.login,
                        name = s.name,
                        surname = s.surname,
                        praname = s.praname,
                        avatarId = s.avatarId,
                        stups = item.stups,
                        avg = item.avg,
                        top = 0,
                        groupName = group?.name ?: "?",
                        formNum = form.classNum,
                        formShortTitle = form.shortTitle,
                        subjectId = item.subjectId,
                        period = item.period,
                        edYear = edYear
                    )
                }
            )
        }
    }
    println("wazap-count ${commonRating.size}")
    listOf(
        RatingCommonSchoolTable to commonRating,
        RatingHighSchoolTable to highRating,
        RatingLowSchoolTable to lowRating
    ).forEach { (table, dto) ->
        var top = 0
        var previousSubjectId = -111
        var previousPeriod = ""
        var previousEdYear = -111
        var previousStups = -111
        var previousAvg = ""
        val items = dto.sortedWith( // .filter { it.stups > 0 && it.avg.toFloat() >= 4 }
            compareBy(
                { it.edYear },
                { it.period.toStr() },
                { it.subjectId },
                { it.stups },
                { it.avg.toFloat() }
            )
        ).reversed().map { x ->
            if (
                (previousEdYear != x.edYear) ||
                (previousPeriod != x.period.toStr()) ||
                (previousSubjectId != x.subjectId)
            ) {
                previousSubjectId = x.subjectId
                previousPeriod = x.period.toStr()
                previousEdYear = x.edYear
                previousAvg = ""
                previousStups = -1
                top = 0
            }
            if (previousAvg != x.avg || previousStups != x.stups) top++
            previousAvg = x.avg
            previousStups = x.stups
            x.copy(top = top)
        }
        transaction {
            table.saveRatings(items)
        }
    }
}


object RatingHighSchoolTable : RatingTable()
object RatingLowSchoolTable : RatingTable()
object RatingCommonSchoolTable : RatingTable()