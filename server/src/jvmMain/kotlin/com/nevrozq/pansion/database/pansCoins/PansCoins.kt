package com.nevrozq.pansion.database.pansCoins

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object PansCoins : Table() {
    private val login = this.varchar("login", 30).uniqueIndex()
    private val count = this.integer("count")

    fun add(login: String, plus: Int) {
        transaction {
            val previousCount = PansCoins.select{(PansCoins.login eq login)}.firstOrNull()?.get(PansCoins.count)
            if (previousCount == null) {
                PansCoins.insert {
                    it[PansCoins.login] = login
                    it[PansCoins.count] = 20
                }
            } else {
                PansCoins.update({PansCoins.login eq login}) {
                    it[PansCoins.count] = previousCount + plus
                }
            }
        }
    }

    fun fetchCount(login: String): Int {
        return transaction {
            PansCoins.select{(PansCoins.login eq login)}.firstOrNull()?.get(PansCoins.count) ?: 0
        }
    }
}