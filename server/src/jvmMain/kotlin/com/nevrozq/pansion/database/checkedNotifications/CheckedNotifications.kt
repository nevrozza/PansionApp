package com.nevrozq.pansion.database.checkedNotifications

import com.nevrozq.pansion.database.calendar.CalendarDTO
import com.nevrozq.pansion.database.tokens.TokenDTO
import com.nevrozq.pansion.database.tokens.Tokens
import com.nevrozq.pansion.database.tokens.Tokens.deleteTokenByIdAndLogin
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object CheckedNotifications : Table() {
    private val studentLogin = this.varchar("studentLogin", 30)
    private val key = this.varchar("key", 80)

    fun insert(dto: CheckedNotificationsDTO) {
        transaction {
            CheckedNotifications.insert {
                it[studentLogin] = dto.studentLogin
                it[key] = dto.key
            }
        }
    }

    fun fetchByLogin(login: String): List<String> {
        return transaction {
            CheckedNotifications.select { (CheckedNotifications.studentLogin eq login) }.map {
                it[key]
            }
        }
    }

}