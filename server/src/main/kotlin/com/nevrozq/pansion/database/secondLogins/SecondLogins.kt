package com.nevrozq.pansion.database.secondLogins

import admin.groups.subjects.REditGroupReceive
import com.nevrozq.pansion.database.forms.Forms
import com.nevrozq.pansion.database.groups.Groups
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.UUID

object SecondLogins : Table() {
    val oldLogin = this.varchar("oldLogin", 30).uniqueIndex()
    val newLogin = this.varchar("newLogin", 30)

    fun change(oldLogin: String, newLogin: String) {
        transaction {
            SecondLogins.deleteWhere { SecondLogins.oldLogin eq oldLogin }
            if (newLogin.isNotBlank()) {
                SecondLogins.insert {
                    it[this.oldLogin] = oldLogin
                    it[this.newLogin] = newLogin
                }
            }
        }
    }

    fun fetchAllNewLogins() : List<String> {
        return transaction {
            SecondLogins.selectAll().map {
                it[newLogin]
            }
        }
    }

    fun fetchSecondLogin(oldLogin: String): String? {
        return transaction {
            SecondLogins.select { SecondLogins.oldLogin eq oldLogin }.map {
                it[SecondLogins.newLogin]
            }.firstOrNull()
        }
    }

    fun fetchOldLogin(newLogin: String): String? {
        return transaction {
            SecondLogins.select { SecondLogins.newLogin eq newLogin }.map {
                it[SecondLogins.oldLogin]
            }.firstOrNull()
        }
    }
}