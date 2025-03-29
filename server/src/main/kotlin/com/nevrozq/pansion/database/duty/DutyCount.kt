package com.nevrozq.pansion.database.duty

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object DutyCount : Table() {
    private val studentLogin = this.varchar("studentLogin", 30).uniqueIndex()
    private val dutyCount = this.integer("count")

    fun plusOne(login: String) {
        transaction {
            val prev = DutyCount.select { (DutyCount.studentLogin eq login) }.firstOrNull()
            val prevValue = prev?.get(dutyCount) ?: 0
            if (prev == null) {
                DutyCount.insert {
                    it[studentLogin] = login
                    it[dutyCount] = 1
                }
            } else {
                DutyCount.update({ studentLogin eq login}) {
                    it[dutyCount] = prevValue+1
                }
            }
        }
    }
    fun fetchByLogin(login: String): Int {
        return transaction {
            DutyCount.select { (DutyCount.studentLogin eq login) }.firstOrNull()?.get(dutyCount) ?: 0
        }
    }

//    fun insert(dto: DutySettingsDTO) {
//        transaction {
//            DutySettings.insert {
//                it[peopleCount] = dto.peopleCount
//                it[mentorLogin] = dto.mentorLogin
//            }
//        }
//    }

//    fun fetchByLogin(login: String): DutySettingsDTO? {
//        return transaction {
//            val x = DutySettings.select { (DutySettings.mentorLogin eq login) }.firstOrNull()
//            if (x != null) {
//                DutySettingsDTO(
//                    mentorLogin = x[DutySettings.mentorLogin],
//                    peopleCount = x[DutySettings.peopleCount]
//                )
//            } else null
//        }
//    }

}