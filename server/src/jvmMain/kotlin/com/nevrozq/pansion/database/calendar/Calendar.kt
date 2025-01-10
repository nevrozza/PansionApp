package com.nevrozq.pansion.database.calendar

import com.nevrozq.pansion.database.holidays.Holidays
import getWeeks
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import server.getCurrentEdYear

object Calendar : Table() {
    private val num = Calendar.integer("num")
    private val start = Calendar.varchar("start", 12)
    private val halfNum = Calendar.integer("halfNum")
    fun insertList(calendarDTOs: List<CalendarDTO>) {

            transaction {
                Calendar.deleteAll()
                calendarDTOs.forEach { calendarDTO ->
//                    deleteLogin(cabinetDTO.login)
                    insert {
                        it[num] = calendarDTO.num
                        it[start] = calendarDTO.start
                        it[halfNum] = calendarDTO.halfNum
                    }
                }
            }

    }


    fun getHalfOfModule(module: Int): Int {
        return transaction {
            Calendar.select { num eq module }.firstOrNull()?.get(halfNum) ?: 1
        }
    }
    fun getModuleStartEnd(module: Int): Pair<String, String?>? {
        return try {
            transaction {
                val first = Calendar.select { num eq module }.first()[start]
                val second = Calendar.select { num eq module + 1 }.firstOrNull()?.get(start)
                Pair(first, second)
            }
        } catch (e: Throwable) {
            println(e)
            null
        }
    }
    fun getModuleStart(module: Int): String? {
        return try {
            transaction {
                Calendar.select { num eq module }.first()[start]
            }
        } catch (e: Throwable) {
            println(e)
            null
        }
    }

    fun getAllModulesOfHalfAsString(half: Int): String {
        val modules = getAllModules().filter { it.halfNum == half }.map { it.num }.joinToString(separator = "").replace("[", "").replace("]", "")
        return modules.ifEmpty { if(half == 1) "1" else "" }
    }

    fun getAllModulesOfHalf(half: Int): List<Int> {
        return getAllModules().mapNotNull {
            if(half == it.halfNum) it.num
            else null
        }
    }

    fun getAllModules(): List<CalendarDTO> {
        return transaction {
            Calendar.selectAll().map {
                CalendarDTO(
                    num = it[num],
                    start = it[start],
                    halfNum = it[halfNum]
                )
            }
        }
    }
}