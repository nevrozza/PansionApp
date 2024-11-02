package com.nevrozq.pansion.database.duty

import com.nevrozq.pansion.database.calendar.CalendarDTO
import com.nevrozq.pansion.database.tokens.TokenDTO
import com.nevrozq.pansion.database.tokens.Tokens
import com.nevrozq.pansion.database.tokens.Tokens.deleteTokenByIdAndLogin
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object Duty : Table() {
    private val mentorLogin = this.varchar("mentorLogin", 30)
    private val studentLogin = this.varchar("studentLogin", 30)

    fun insert(dto: DutyDTO) {
        transaction {
            Duty.insert {
                it[studentLogin] = dto.studentLogin
                it[mentorLogin] = dto.mentorLogin
            }
        }
    }

    fun enterList(mentorLogin: String, list: List<String>) {
        transaction {
            Duty.deleteWhere { (Duty.mentorLogin eq mentorLogin) }
            list.forEach { login ->
                Duty.insert(
                    DutyDTO(
                        mentorLogin = mentorLogin,
                        studentLogin = login
                    )
                )
            }
        }
    }

    fun fetchByMentorLogin(mentorLogin: String): List<String> {
        return transaction {
            Duty.select { (Duty.mentorLogin eq mentorLogin) }.map {
                it[studentLogin]
            }
        }
    }

}