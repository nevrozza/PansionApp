package com.nevrozq.pansion.database.statsSettings

import auth.StatsSettingsDTO
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object StatsSettings : Table() {
    val login = this.varchar("login", 30).uniqueIndex()
    val isOpened = this.bool("isOpened")

    fun fetch(login: String): Boolean {
        return transaction {
            StatsSettings.select { (StatsSettings.login eq login) }.firstOrNull()?.get(isOpened) ?: false
        }
    }

    fun insert(dto: StatsSettingsDTO) {
        transaction {
            StatsSettings.deleteWhere{(StatsSettings.login eq dto.login)}
            StatsSettings.insert {
                it[login] = dto.login
                it[isOpened] = dto.isOpened
            }
        }

    }
}