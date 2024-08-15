package com.nevrozq.pansion.database.parents

import FIO
import PersonPlus
import com.nevrozq.pansion.database.users.Users
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
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

    fun fetchChildren(parentLogin: String): List<PersonPlus> {
        return transaction {
            Parents.select{(Parents.parentLogin eq parentLogin)}.map {
                val user = Users.fetchUser(it[studentLogin])!!
                PersonPlus(
                    login = it[studentLogin],
                    avatarId = user.avatarId,
                    fio = FIO(name = user.name, surname = user.surname, praname = user.praname),
                    isActive = user.isActive
                )
            }
        }
    }
}