package com.nevrozq.pansion.database.holidays

import admin.calendar.Holiday
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction


//data class Holiday(
//    val id: Int,
//    val edYear: Int,
//    val start: String,
//    val end: String,
//    val isForAll: Boolean
//)


object Holidays : Table() {
    private val id = this.integer("id").uniqueIndex()
    private val edYear = this.integer("edYear")
    private val start = this.varchar("start", 10)
    private val end = this.varchar("end", 10)
    private val isForAll = this.bool("isForAll")


    fun insertList(holidays: List<Holiday>) {
        transaction {
            Holidays.deleteAll()
            holidays.forEach { h ->
                insert {
                    it[Holidays.id] = h.id
                    it[edYear] = h.edYear
                    it[start] = h.start
                    it[end] = h.end
                    it[isForAll] = h.isForAll
                }
            }
        }
    }


    fun fetch(edYear: Int) : List<Holiday> {
        return transaction {
            Holidays.select {  Holidays.edYear eq edYear }.map {
                Holiday(
                    id = it[Holidays.id],
                    edYear = it[Holidays.edYear],
                    start = it[start],
                    end = it[end],
                    isForAll = it[isForAll]
                )
            }
        }
    }

    fun fetchAll() : List<Holiday> {
        return transaction {
            Holidays.selectAll().map {
                Holiday(
                    id = it[Holidays.id],
                    edYear = it[edYear],
                    start = it[start],
                    end = it[end],
                    isForAll = it[isForAll]
                )
            }
        }
    }
//
//
//    fun getHalfOfModule(module: Int): Int {
//        return transaction {
//            Calendar.select { num eq module }.firstOrNull()?.get(halfNum) ?: 1
//        }
//    }
//    fun getModuleStartEnd(module: Int): Pair<String, String?>? {
//        return try {
//            transaction {
//                val first = Calendar.select { num eq module }.first()[start]
//                val second = Calendar.select { num eq module + 1 }.firstOrNull()?.get(start)
//                Pair(first, second)
//            }
//        } catch (e: Throwable) {
//            println(e)
//            null
//        }
//    }
//    fun getModuleStart(module: Int): String? {
//        return try {
//            transaction {
//                Calendar.select { num eq module }.first()[start]
//            }
//        } catch (e: Throwable) {
//            println(e)
//            null
//        }
//    }
//
//    fun getAllModulesOfHalfAsString(half: Int): String {
//        val modules = getAllModules().filter { it.halfNum == half }.map { it.num }.joinToString(separator = "").replace("[", "").replace("]", "")
//        return modules.ifEmpty { if(half == 1) "1" else "" }
//    }
//
//    fun getAllModulesOfHalf(half: Int): List<Int> {
//        return getAllModules().mapNotNull {
//            if(half == it.halfNum) it.num
//            else null
//        }
//    }
//
//    fun getAllModules(): List<CalendarDTO> {
//        return transaction {
//            Calendar.selectAll().map {
//                CalendarDTO(
//                    num = it[num],
//                    start = it[start],
//                    halfNum = it[halfNum]
//                )
//            }
//        }
//    }
}

