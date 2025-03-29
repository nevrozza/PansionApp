package com.nevrozq.pansion.database.pickedGIA

import FIO
import PersonPlus
import com.nevrozq.pansion.database.users.Users
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object PickedGIA : Table() {
    private val studentLogin = this.varchar("studentLogin", 30)
    private val subjectId = this.integer("subjectId")

    fun insert(dto: PickedGIADTO) {
        transaction {
            PickedGIA.insert {
                it[studentLogin] = dto.studentLogin
                it[subjectId] = dto.subjectGIAId
            }
        }
    }

    fun delete(dto: PickedGIADTO) {
        transaction {
            PickedGIA.deleteWhere { (PickedGIA.studentLogin eq dto.studentLogin) and (PickedGIA.subjectId eq dto.subjectGIAId) }
        }
    }

    fun fetchByStudent(login: String): List<Int> {
        return transaction {
            PickedGIA.select{(PickedGIA.studentLogin eq login)}.map {
                it[subjectId]
            }
        }
    }
}