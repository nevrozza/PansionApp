package com.nevrozq.pansion.database.subjects

import admin.GSubject
import com.nevrozq.pansion.database.groups.Groups
import com.nevrozq.pansion.database.users.UserDTO
import com.nevrozq.pansion.database.users.Users
import com.nevrozq.pansion.database.users.Users.nullable
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Table.Dual.autoIncrement
import org.jetbrains.exposed.sql.Table.Dual.index
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object GSubjects : Table() {
    private val id = GSubjects.integer("id").autoIncrement().uniqueIndex()
    private val name = GSubjects.varchar("name", 50)
    private val isActivated = GSubjects.bool("isActivated")

    fun insert(gSubjectsDTO: GSubjectsDTO) {
        try {
            transaction {
                GSubjects.insert {
                    it[name] = gSubjectsDTO.name
                    it[isActivated] = true
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun getSubjects(): List<GSubject> {
        return transaction {
            GSubjects.selectAll().map {
                GSubject(name = it[name], id = it[GSubjects.id], isActivated = it[isActivated])
            }
        }
    }

    fun deleteSubject(id: Int) {
        try {
            transaction {
                GSubjects.update({GSubjects.id eq id}) {
                    it[isActivated] = false
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }


    fun updateSubject(id: Int, gSubjectsDTO: GSubjectsDTO) {
        try {
            transaction {
                GSubjects.update({ GSubjects.id eq id }) {
                    it[GSubjects.name] = gSubjectsDTO.name
                    it[isActivated] = gSubjectsDTO.isActivated
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }
}