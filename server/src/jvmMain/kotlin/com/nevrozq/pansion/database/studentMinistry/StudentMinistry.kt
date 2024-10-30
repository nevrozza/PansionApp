package com.nevrozq.pansion.database.studentMinistry

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object StudentMinistry : Table() {
    val login = this.varchar("login", 30).uniqueIndex()
    val ministry = this.varchar("min", 1)

    fun set(login: String, ministry: String) {
        transaction {
            StudentMinistry.deleteWhere { StudentMinistry.login eq login }
            if (ministry.isNotBlank() || login.isNotBlank()) {
                StudentMinistry.insert {
                    it[this.login] = login
                    it[this.ministry] = ministry
                }
            }
        }
    }

//    fun fetchAllNewLogins() : List<String> {
//        return transaction {
//            SecondLogins.selectAll().map {
//                it[newLogin]
//            }
//        }
//    }

    fun fetchMinistryWithLogin(login: String): String? {
        return transaction {
            StudentMinistry.select { StudentMinistry.login eq login }.map {
                it[StudentMinistry.ministry]
            }.firstOrNull()
        }
    }

    fun fetchLoginsOfMinistry(ministry: String): List<String> {
        return transaction {
            StudentMinistry.select { StudentMinistry.ministry eq ministry }.map {
                it[StudentMinistry.login]
            }
        }
    }
}