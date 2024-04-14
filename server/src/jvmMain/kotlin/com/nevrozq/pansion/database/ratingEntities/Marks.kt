package com.nevrozq.pansion.database.ratingEntities

import com.nevrozq.pansion.database.studentLines.StudentLines
import com.nevrozq.pansion.features.reports.isQuarter
import org.jetbrains.exposed.sql.transactions.transaction
import report.RIsQuartersReceive

object Marks : RatingEntity() {
    fun fetchAVG(login: String, subjectId: Int): ForAvg {
        return transaction {
            val isQuarter = isQuarter(RIsQuartersReceive(login))
            val marks = Marks.fetchForUserSubjectQuarter(login, subjectId, if(isQuarter) "4" else "34")
            ForAvg(
                count = marks.size,
                sum = marks.sumOf { it.content.toInt() }
            )
        }
    }

    fun fetchWeekAVG(login: String): ForAvg {
        return transaction {
            val marks = fetchForAWeek(login)

            println("avg: $marks")
            ForAvg(
                count = marks.size,
                sum = marks.sumOf { it.content.toInt() }
            )
        }
    }
}