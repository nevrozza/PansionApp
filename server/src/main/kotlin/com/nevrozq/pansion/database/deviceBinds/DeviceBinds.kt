package com.nevrozq.pansion.database.deviceBinds

import admin.groups.subjects.REditGroupReceive
import com.nevrozq.pansion.database.forms.Forms
import com.nevrozq.pansion.database.groups.Groups
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.UUID

object DeviceBinds : Table() {
    val deviceId = DeviceBinds.uuid("deviceId")
    val registeredLogin = this.varchar("registeredLogin", 30)

    fun add(id: UUID, login : String) {
        transaction {
            DeviceBinds.insert {
                it[deviceId] = id
                it[registeredLogin] = login
            }
        }
    }

    fun selectAll(id: UUID) : List<String> {
        return transaction {
            DeviceBinds.select { deviceId eq id }.mapNotNull {
                it[registeredLogin]
            }
        }
    }
}