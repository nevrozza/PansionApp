package com.nevrozq.pansion.database.ratingEntities

import com.nevrozq.pansion.database.calendar.Calendar
import com.nevrozq.pansion.database.reportHeaders.ReportHeaders
import com.nevrozq.pansion.database.studentLines.StudentLines
import com.nevrozq.pansion.database.studentLines.StudentLinesDTO
import com.nevrozq.pansion.database.subjects.Subjects
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import server.daysShift
import server.getPreviousWeekDays
import server.getWeekDays
import server.twoNums

open class RatingEntity : Table() {
    val groupId = this.integer("groupId").nullable()
    val subjectId = this.integer("subjectId").nullable()
    val reportId = this.integer("reportId").nullable()
    val login = this.varchar("login", 30)
    val content = this.varchar("content", 5)
    val reason = this.varchar("reason", 5)
    val id = this.integer("id")
    val edYear = this.integer("edYear")
    val part = this.varchar("part", 1)
    val date = this.varchar("date", 10)
    val isGoToAvg = this.bool("isGoToAvg")
    val deployDate = this.varchar("deployDate", 10)
    val deployLogin = this.varchar("deployLogin", 30)
    val deployTime = this.varchar("deployTime", 5)
    val custom = this.text("custom").nullable()

    fun delete(id: Int, reportId: Int) {
        try {
            transaction {
                this@RatingEntity.deleteWhere {
                    (this@RatingEntity.reportId eq reportId) and (this@RatingEntity.id eq id)
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun insert(r: RatingEntityDTO, isDelete: Boolean) {
        try {
            if (isDelete) {
                if (r.reportId != null) {
                    this.delete(id = r.id, reportId = r.reportId)
                }
            }
            transaction {
                this@RatingEntity.insert {
                    it[groupId] = r.groupId
                    it[reportId] = r.reportId
                    it[login] = r.login
                    it[content] = r.content
                    it[reason] = r.reason
                    it[id] = r.id
                    it[part] = r.part
                    it[isGoToAvg] = r.isGoToAvg
                    it[subjectId] = r.subjectId
                    it[date] = r.date
                    it[deployDate] = r.deployDate
                    it[deployTime] = r.deployTime
                    it[deployLogin] = r.deployLogin
                    it[custom] = r.custom
                    it[edYear] = r.edYear
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun fetchForAWeek(login: String) : List<RatingEntityDTO> {
        val dayStrings = getWeekDays()
        return fetchForPeriod(login, dayStrings)
    }
    fun fetchForAPreviousWeek(login: String) : List<RatingEntityDTO> {
        val dayStrings = getPreviousWeekDays()
        return fetchForPeriod(login, dayStrings)
    }

    fun fetchForPeriod(login: String, period: List<String>) : List<RatingEntityDTO>  {
        return transaction {
            try {
                val ratingEntities =
                    this@RatingEntity.select { (this@RatingEntity.login eq login) and (this@RatingEntity.date inList period) }
                ratingEntities.map {
                    RatingEntityDTO(
                        groupId = it[groupId],
                        reportId = it[this@RatingEntity.reportId],
                        login = it[this@RatingEntity.login],
                        content = it[content],
                        reason = it[reason],
                        id = it[this@RatingEntity.id],
                        part = it[part],
                        isGoToAvg = it[isGoToAvg],
                        subjectId = it[subjectId],
                        date = it[date],
                        deployDate = it[deployDate],
                        deployTime = it[deployTime],
                        deployLogin = it[deployLogin],
                        custom = it[custom],
                        edYear = it[edYear]
                    )
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }

    fun fetchForReportQuarters(reportId: Int, quartersNum: String, edYear: Int) : List<RatingEntityDTO> {
        return transaction {
            try {
                val ratingEntities =
                    this@RatingEntity.select {
                        (this@RatingEntity.reportId eq reportId) and
                                (this@RatingEntity.part inList quartersNum.map { it.toString() }) and
                                (this@RatingEntity.edYear eq edYear)
                    }
                ratingEntities.map {
                    RatingEntityDTO(
                        groupId = it[groupId],
                        reportId = it[this@RatingEntity.reportId],
                        login = it[this@RatingEntity.login],
                        content = it[content],
                        reason = it[reason],
                        id = it[this@RatingEntity.id],
                        part = it[part],
                        isGoToAvg = it[isGoToAvg],
                        subjectId = it[subjectId],
                        date = it[date],
                        deployLogin = it[deployLogin],
                        deployTime = it[deployTime],
                        deployDate = it[deployDate],
                        custom = it[custom],
                        edYear = it[this@RatingEntity.edYear]
                    )
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }

    fun fetchForReport(reportId: Int) : List<RatingEntityDTO> {
        return transaction {
            try {
                val ratingEntities =
                    this@RatingEntity.select { this@RatingEntity.reportId eq reportId }
                ratingEntities.map {
                    RatingEntityDTO(
                        groupId = it[groupId],
                        reportId = it[this@RatingEntity.reportId],
                        login = it[login],
                        content = it[content],
                        reason = it[reason],
                        id = it[this@RatingEntity.id],
                        part = it[part],
                        isGoToAvg = it[isGoToAvg],
                        subjectId = it[subjectId],
                        date = it[date],
                        deployDate = it[deployDate],
                        deployTime = it[deployTime],
                        deployLogin = it[deployLogin],
                        custom = it[custom],
                        edYear = it[edYear]
                    )
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }
    fun fetchForUserReport(sLogin: String, reportId: Int) : List<RatingEntityDTO> {
        return transaction {
            try {
                val ratingEntities =
                    this@RatingEntity.select { (this@RatingEntity.reportId eq reportId) and (this@RatingEntity.login eq sLogin) }
                ratingEntities.map {
                    RatingEntityDTO(
                        groupId = it[groupId],
                        reportId = it[this@RatingEntity.reportId],
                        login = it[login],
                        content = it[content],
                        reason = it[reason],
                        id = it[this@RatingEntity.id],
                        part = it[part],
                        isGoToAvg = it[isGoToAvg],
                        subjectId = it[subjectId],
                        date = it[date],
                        deployDate = it[deployDate],
                        deployTime = it[deployTime],
                        deployLogin = it[deployLogin],
                        custom = it[custom],
                        edYear = it[edYear]
                    )
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }

    fun fetchUserByDate(login: String, date: String) : List<RatingEntityDTO> {
        return transaction {
            try {

                val ratingEntities =
                    this@RatingEntity.select {
                        (this@RatingEntity.login eq login) and (this@RatingEntity.isGoToAvg eq true) and (this@RatingEntity.date eq date) }.reversed()
                ratingEntities.map {
                    RatingEntityDTO(
                        groupId = it[groupId],
                        reportId = it[this@RatingEntity.reportId],
                        login = it[this@RatingEntity.login],
                        content = it[content],
                        reason = it[reason],
                        id = it[this@RatingEntity.id],
                        part = it[part],
                        isGoToAvg = it[isGoToAvg],
                        subjectId = it[subjectId],
                        date = it[this@RatingEntity.date],
                        deployDate = it[deployDate],
                        deployLogin = it[deployLogin],
                        deployTime = it[deployTime],
                        custom = it[custom],
                        edYear = it[edYear]
                    )
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }


    fun fetchRecentForUser(login: String, edYear: Int) : List<RatingEntityDTO> {
        return transaction {
            try {

                val ratingEntities =
                    this@RatingEntity.select {
                        (this@RatingEntity.login eq login) and
                                (this@RatingEntity.isGoToAvg eq true) and
                                (this@RatingEntity.edYear eq edYear)
                    }.reversed()


                ratingEntities.map {
                    RatingEntityDTO(
                        groupId = it[groupId],
                        reportId = it[this@RatingEntity.reportId],
                        login = it[this@RatingEntity.login],
                        content = it[content],
                        reason = it[reason],
                        id = it[this@RatingEntity.id],
                        part = it[part],
                        isGoToAvg = it[isGoToAvg],
                        subjectId = it[subjectId],
                        date = it[date],
                        deployTime = it[deployTime],
                        deployLogin = it[deployLogin],
                        deployDate = it[deployDate],
                        custom = it[custom],
                        edYear = it[this@RatingEntity.edYear]
                    )
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }

    fun fetchForUser(login: String, edYear: Int) : List<RatingEntityDTO> {
        return transaction {
            try {
                val ratingEntities =
                    this@RatingEntity.select {
                        (this@RatingEntity.login eq login) and
                                (this@RatingEntity.edYear eq edYear)
                    }
                ratingEntities.map {
                    RatingEntityDTO(
                        groupId = it[groupId],
                        reportId = it[this@RatingEntity.reportId],
                        login = it[this@RatingEntity.login],
                        content = it[content],
                        reason = it[reason],
                        id = it[this@RatingEntity.id],
                        part = it[part],
                        isGoToAvg = it[isGoToAvg],
                        subjectId = it[subjectId],
                        date = it[date],
                        deployDate = it[deployDate],
                        deployLogin = it[deployLogin],
                        deployTime = it[deployTime],
                        custom = it[custom],
                        edYear = it[this@RatingEntity.edYear]
                    )
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }

    fun fetchForUserQuarters(login: String, quartersNum: String, isQuarters: Boolean, edYear: Int) : List<RatingEntityDTO> {
        val x = if(isQuarters) quartersNum else Calendar.getAllModulesOfHalfAsString(quartersNum.toInt())
        val quartersList = x.map { it.toString() }
        return transaction {
            try {
                val ratingEntities =
                    this@RatingEntity.select {
                        (this@RatingEntity.login eq login) and
                                (this@RatingEntity.part inList quartersList) and
                                (this@RatingEntity.edYear eq edYear)
                    }
                ratingEntities.map {
                    RatingEntityDTO(
                        groupId = it[groupId],
                        reportId = it[this@RatingEntity.reportId],
                        login = it[this@RatingEntity.login],
                        content = it[content],
                        reason = it[reason],
                        id = it[this@RatingEntity.id],
                        part = it[part],
                        isGoToAvg = it[isGoToAvg],
                        subjectId = it[subjectId],
                        date = it[date],
                        deployTime = it[deployTime],
                        deployLogin = it[deployLogin],
                        deployDate = it[deployDate],
                        custom = it[custom],
                        edYear = it[this@RatingEntity.edYear]
                    )
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }

    fun fetchForUserSubjectQuarter(login: String, subjectId: Int, quartersNum: String, edYear: Int) : List<RatingEntityDTO> {
        return transaction {
            try {
                val ratingEntities =
                    this@RatingEntity.select {
                        (this@RatingEntity.login eq login) and
                                (this@RatingEntity.subjectId eq subjectId)  and
                                (this@RatingEntity.part inList quartersNum.map { it.toString() }) and
                                (this@RatingEntity.edYear eq edYear)
                    }
                ratingEntities.map {
                    RatingEntityDTO(
                        groupId = it[groupId],
                        reportId = it[this@RatingEntity.reportId],
                        login = it[this@RatingEntity.login],
                        content = it[content],
                        reason = it[reason],
                        id = it[this@RatingEntity.id],
                        part = it[part],
                        isGoToAvg = it[isGoToAvg],
                        subjectId = it[this@RatingEntity.subjectId],
                        date = it[date],
                        deployDate = it[deployDate],
                        deployLogin = it[deployLogin],
                        deployTime = it[deployTime],
                        custom = it[custom],
                        edYear = it[this@RatingEntity.edYear]
                    )
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }

    fun fetchForUserSubject(login: String, subjectId: Int, edYear: Int) : List<RatingEntityDTO> {
        return transaction {
            try {
                val ratingEntities =
                    this@RatingEntity.select {
                        (this@RatingEntity.login eq login) and
                                (this@RatingEntity.subjectId eq subjectId) and
                                (this@RatingEntity.edYear eq edYear)
                    }
                ratingEntities.map {
                    RatingEntityDTO(
                        groupId = it[groupId],
                        reportId = it[this@RatingEntity.reportId],
                        login = it[this@RatingEntity.login],
                        content = it[content],
                        reason = it[reason],
                        id = it[this@RatingEntity.id],
                        part = it[part],
                        isGoToAvg = it[isGoToAvg],
                        subjectId = it[this@RatingEntity.subjectId],
                        date = it[date],
                        deployTime = it[deployTime],
                        deployLogin = it[deployLogin],
                        deployDate = it[deployDate],
                        custom = it[custom],
                        edYear = it[this@RatingEntity.edYear]
                    )
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }
    fun fetchForUserGroup(login: String, groupId: Int, edYear: Int) : List<RatingEntityDTO> {
        return transaction {
            try {
                val ratingEntities =
                    this@RatingEntity.select { (this@RatingEntity.login eq login) and (this@RatingEntity.groupId eq groupId) and
                                (this@RatingEntity.edYear eq edYear) }
                ratingEntities.map {
                    RatingEntityDTO(
                        groupId = it[this@RatingEntity.groupId],
                        reportId = it[this@RatingEntity.reportId],
                        login = it[this@RatingEntity.login],
                        content = it[content],
                        reason = it[reason],
                        id = it[this@RatingEntity.id],
                        part = it[part],
                        isGoToAvg = it[isGoToAvg],
                        subjectId = it[this@RatingEntity.subjectId],
                        date = it[date],
                        deployTime = it[deployTime],
                        deployLogin = it[deployLogin],
                        deployDate = it[deployDate],
                        custom = it[custom],
                        edYear = it[this@RatingEntity.edYear]
                    )
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }



}