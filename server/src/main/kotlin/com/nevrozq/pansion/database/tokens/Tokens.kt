package com.nevrozq.pansion.database.tokens

import com.nevrozq.pansion.database.users.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID
import kotlin.Exception

object Tokens : Table() {
    private val deviceId = Tokens.uuid("deviceId")
    private val login = Tokens.varchar("login", 30)
    private val token = Tokens.uuid("token").uniqueIndex()
    private val deviceName = Tokens.varchar("deviceName", 20).nullable()
    private val deviceType = Tokens.varchar("deviceType", 10)
    private val time = Tokens.varchar("time", 16)

    fun insert(tokenDTO: TokenDTO) {

        deleteTokenByIdAndLogin(tokenDTO.deviceId, tokenDTO.login)
        transaction {
            Tokens.insert {
                it[deviceId] = tokenDTO.deviceId
                it[login] = tokenDTO.login
                it[token] = tokenDTO.token
                it[deviceName] = tokenDTO.deviceName
                it[deviceType] = tokenDTO.deviceType
                it[time] = tokenDTO.time
            }
        }
    }

    fun isTokenValid(token: UUID): Boolean {
        return try {
            transaction {
                val x = Tokens.select(Tokens.token eq token).count()
                x > 0
            }
        } catch (e: Throwable) {
            false
        }
    }

    fun getIsMember(token: UUID): Boolean {
        return try {
            transaction {
                Tokens.select { (Tokens.token eq token) }.first()
                true
            }
        } catch (e: Throwable) {
            println(e)
            false
        }
    }

    fun getLoginOfThisToken(token: UUID): String {
        return try {
            transaction {
                Tokens.select(Tokens.token eq token).first()[login]
            }
        } catch (e: Throwable) {
            println(e)
            ""
        }
    }

    fun deleteTokenByIdAndLogin(id: UUID, login: String) {
        try {
            transaction {
                Tokens.deleteWhere {
                    (deviceId eq id) and (Tokens.login eq login)
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun deleteToken(token: UUID) {
        try {
            transaction {
                Tokens.deleteWhere { Tokens.token eq token }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun deleteTokenByLogin(login: String) {
        try {
            transaction {
                Tokens.deleteWhere { Tokens.login eq login }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }


    fun getTokensOfThisLogin(thisLogin: String): List<TokenDTO> {
        return try {
            transaction {
                Tokens.select(login eq thisLogin).map {
                    TokenDTO(
                        it[deviceId],
                        it[login],
                        it[token],
                        it[deviceName],
                        it[deviceType],
                        it[time]
                    )
                }
            }
        } catch (e: Throwable) {
           emptyList()
        }
    }
}