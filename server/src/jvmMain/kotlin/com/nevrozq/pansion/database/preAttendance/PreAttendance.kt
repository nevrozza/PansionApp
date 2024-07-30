package com.nevrozq.pansion.database.preAttendance

import com.nevrozq.pansion.database.studentsInForm.StudentsInForm
import mentoring.preAttendance.ClientPreAttendance
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object PreAttendance : Table() {
    private val id = this.integer("id").autoIncrement().uniqueIndex()
    private val studentLogin = this.varchar("studentLogin", 30)
    private val date = this.varchar("date", 10)
    private val start = this.varchar("start", 5)
    private val end = this.varchar("end", 5)
    private val reason = this.text("reason")
    private val isGood = this.bool("isGood")

    fun savePreAttendance(date: String, login: String, preAttendance: ClientPreAttendance) {
        transaction {
            try {
                PreAttendance.deleteWhere {
                    (PreAttendance.date eq date) and (PreAttendance.studentLogin eq login)
                }
            } catch (e: Throwable) {
                println(e)
            }
            PreAttendance.insert {
                it[studentLogin] = login
                it[PreAttendance.date] = date
                it[start] = preAttendance.start
                it[end] = preAttendance.end
                it[reason] = preAttendance.reason
                it[isGood] = preAttendance.isGood
            }
        }
    }

    fun fetchPreAttendanceByDateAndLogin(date: String, login: String): PreAttendanceDTO? {
        return transaction {
            try {
                val a = PreAttendance.select { (PreAttendance.date eq date) and (studentLogin eq login) }
                    .first()
                PreAttendanceDTO(
                    id = a[PreAttendance.id],
                    studentLogin = a[studentLogin],
                    date = a[PreAttendance.date],
                    start = a[PreAttendance.start],
                    end = a[PreAttendance.end],
                    reason = a[reason],
                    isGood = a[isGood]
                )
            } catch (e: Throwable) {
                println(e)
                null
            }
        }
    }
}