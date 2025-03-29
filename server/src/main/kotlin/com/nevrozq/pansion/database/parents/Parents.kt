package com.nevrozq.pansion.database.parents

import FIO
import PersonPlus
import admin.parents.ParentLine
import com.nevrozq.pansion.database.subjects.Subjects
import com.nevrozq.pansion.database.users.Users
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

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
    fun delete(id: Int) {
        transaction {
            Parents.deleteWhere { Parents.id eq id }
        }
    }

    fun update(id: Int, parentLogin: String) {
        transaction {
            Parents.update({ Parents.id eq id}) {
                it[Parents.parentLogin] = parentLogin
            }
        }
    }

    fun fetchChildren(parentLogin: String): List<PersonPlus> {
        return transaction {
            Parents.select{(Parents.parentLogin eq parentLogin)}.mapNotNull {
                val user = Users.fetchUser(it[studentLogin])
                if (user != null) {
                    PersonPlus(
                        login = it[studentLogin],
                        avatarId = user.avatarId,
                        fio = FIO(name = user.name, surname = user.surname, praname = user.praname),
                        isActive = user.isActive
                    )
                } else null
            }
        }
    }
    fun fetchAll(): List<ParentLine> {
        return transaction {
            Parents.selectAll().map {
                ParentLine(
                    parentLogin = it[parentLogin],
                    id = it[Parents.id],
                    studentLogin = it[studentLogin]
                )
            }
        }
    }
}