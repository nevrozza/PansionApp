package com.nevrozq.pansion.database.duty

import com.nevrozq.pansion.database.calendar.CalendarDTO
import com.nevrozq.pansion.database.tokens.TokenDTO
import com.nevrozq.pansion.database.tokens.Tokens
import com.nevrozq.pansion.database.tokens.Tokens.deleteTokenByIdAndLogin
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object DutySettings : Table() {
    private val mentorLogin = this.varchar("mentorLogin", 30).uniqueIndex()
    private val peopleCount = this.integer("peopleCount")

    fun insert(dto: DutySettingsDTO) {
        transaction {
            DutySettings.deleteWhere { (mentorLogin eq dto.mentorLogin) }
            DutySettings.insert {
                it[peopleCount] = dto.peopleCount
                it[mentorLogin] = dto.mentorLogin
            }
        }
    }

    fun fetchByLogin(login: String): DutySettingsDTO? {
        return transaction {
            val x = DutySettings.select { (DutySettings.mentorLogin eq login) }.firstOrNull()
            if (x != null) {
                DutySettingsDTO(
                    mentorLogin = x[DutySettings.mentorLogin],
                    peopleCount = x[DutySettings.peopleCount]
                )
            } else null
        }
    }

}