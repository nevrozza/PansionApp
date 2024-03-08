package com.nevrozq.pansion.database.forms

import admin.Form
import com.nevrozq.pansion.database.groups.Groups
import com.nevrozq.pansion.database.groups.Groups.autoIncrement
import com.nevrozq.pansion.database.groups.Groups.uniqueIndex
import com.nevrozq.pansion.database.groups.GroupsDTO
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object Forms : Table() {
    private val id = Forms.integer("id").autoIncrement().uniqueIndex()
    private val name = Forms.varchar("name", 50)
    private val shortName = Forms.varchar("shortName", 11)
    private val mentorLogin = Forms.varchar("teacherLogin", 30)
    private val classNum = Forms.integer("classNum")
    private val isActivated = Forms.bool("isActivated")

    fun insert(form: FormsDTO) {
        try {
            transaction {
                Forms.insert {
                    it[classNum] = form.classNum
                    it[name] = form.name
                    it[shortName] = form.shortName
                    it[mentorLogin] = form.mentorLogin
                    it[isActivated] = form.isActivated
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun getAllForms(): List<Form> {
        return transaction {
            Forms.selectAll().map {

                Form(
                    id = it[Forms.id],
                    name = it[name],
                    mentorLogin = it[mentorLogin],
                    classNum = it[classNum],
                    shortName = it[shortName]
                )

            }
        }
    }
}