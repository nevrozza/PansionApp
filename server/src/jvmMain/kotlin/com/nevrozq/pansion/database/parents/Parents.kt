package com.nevrozq.pansion.database.parents

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object Parents : Table() {
    val id = this.integer("id").autoIncrement().uniqueIndex()
    private val studentLogin = this.varchar("studentLogin", 30)
    private val parentLogin = this.varchar("parentLogin", 30)

    fun insert(dto: ParentsDTO) {
        transaction {
            Parents.insert {
                it[studentLogin] = dto.studentLogin
                it[parentLogin] = dto.parentLogin
            }
        }
    }
}