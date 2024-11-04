package com.nevrozq.pansion.database.scheduleConflicts

import com.nevrozq.pansion.utils.toList
import com.nevrozq.pansion.utils.toStr
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object ScheduleConflicts : Table() {
    //private val id = ScheduleConflicts.integer("id")//.autoIncrement().uniqueIndex()
    val date = ScheduleConflicts.varchar("date", 10) //12.45.78
    val lessonId = ScheduleConflicts.integer("lessonId")
    val logins = ScheduleConflicts.text("logins")

    fun insert(dto: ScheduleConflictsDTO) {
        try {
            transaction {
                ScheduleConflicts.insert {
                    it[date] = dto.date
                    it[lessonId] = dto.lessonIndex
                    it[logins] = dto.logins.toStr() ?: ""
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun insertList(list: List<ScheduleConflictsDTO>) {
        try {
            transaction {
                list.forEach {
                    insert(it)
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun fetchByDate(date: String): List<ScheduleConflictsDTO> {
        return transaction {
            ScheduleConflicts.select { (ScheduleConflicts.date eq date) }.mapNotNull {
                val logins = it[logins].toList() ?: listOf()
                if ((logins).isEmpty()) {
                    null
                } else {
                    ScheduleConflictsDTO(
                        date = it[ScheduleConflicts.date],
                        lessonIndex = it[lessonId],
                        logins = logins
                    )
                }
            }
        }
    }

    fun fetchByDateAndLessonId(date: String, lessonId: Int): ScheduleConflictsDTO? {
        return transaction {
            val it =
                ScheduleConflicts.select { (ScheduleConflicts.date eq date) and (ScheduleConflicts.lessonId eq lessonId) }
                    .firstOrNull()
            if (it != null) {
                ScheduleConflictsDTO(
                    date = it[ScheduleConflicts.date],
                    lessonIndex = it[ScheduleConflicts.lessonId],
                    logins = (it[ScheduleConflicts.logins]).toList() ?: listOf()
                )
            } else null
        }
    }

}