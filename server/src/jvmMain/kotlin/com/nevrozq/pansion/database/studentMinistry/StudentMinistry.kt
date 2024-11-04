package com.nevrozq.pansion.database.studentMinistry

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object StudentMinistry : Table() {
    val login = this.varchar("login", 30).uniqueIndex()
    val ministry = this.varchar("min", 1)
    val level = this.varchar("lvl", 1)

    fun set(dto : StudentMinistryDTO) {
        transaction {
            StudentMinistry.deleteWhere { StudentMinistry.login eq dto.login }
            if (dto.ministry.isNotBlank() || dto.login.isNotBlank()) {
                StudentMinistry.insert {
                    it[this.login] = dto.login
                    it[this.ministry] = dto.ministry
                    it[this.level] = dto.lvl
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

    fun fetchMinistryWithLogin(login: String): StudentMinistryDTO? {
        return transaction {
            StudentMinistry.select { StudentMinistry.login eq login }.map {
                StudentMinistryDTO(
                    ministry = it[StudentMinistry.ministry],
                    login = it[StudentMinistry.login],
                    lvl = it[level]
                )
            }.firstOrNull()
        }
    }

    fun fetchOfMinistry(ministry: String): List<StudentMinistryDTO> {
        return transaction {
            StudentMinistry.select { StudentMinistry.ministry eq ministry }.map {
                StudentMinistryDTO(
                    ministry = it[StudentMinistry.ministry],
                    login = it[StudentMinistry.login],
                    lvl = it[level]
                )
            }
        }
    }
    fun fetchAll(): List<StudentMinistryDTO> {
        return transaction {
            StudentMinistry.selectAll().map {
                StudentMinistryDTO(
                    ministry = it[StudentMinistry.ministry],
                    login = it[StudentMinistry.login],
                    lvl = it[level]
                )
            }
        }
    }
}