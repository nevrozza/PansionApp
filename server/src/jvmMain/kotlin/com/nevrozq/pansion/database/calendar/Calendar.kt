package com.nevrozq.pansion.database.calendar

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object Calendar : Table() {
    private val num = Calendar.integer("num")
    private val start = Calendar.varchar("start", 12)
    private val halfNum = Calendar.integer("halfNum")
    fun insertList(calendarDTOs: List<CalendarDTO>) {
        try {
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
        } catch (e: Throwable) {
            println(e)
        }
    }


    fun getHalfOfModule(module: Int): Int {
        return transaction {
            Calendar.select { num eq module }.firstOrNull()?.get(halfNum) ?: 1
        }
    }
    fun getModuleStartEnd(module: Int): Pair<String, String?> {
        return transaction {
            val first = Calendar.select { num eq module }.first()[start]
            val second = Calendar.select { num eq module+1 }.firstOrNull()?.get(start)
            Pair(first, second)
        }
    }

    fun getAllModulesOfHalfAsString(half: Int): String {
        val modules = getAllModules().filter { it.halfNum == half }
        return modules.map { it.num }.joinToString(separator = "").replace("[", "").replace("]", "")
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