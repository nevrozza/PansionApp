package com.nevrozq.pansion.database.forms

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object Forms : Table() {
    private val id = Forms.integer("id").autoIncrement().uniqueIndex()
    private val title = Forms.varchar("title", 50)
    private val shortTitle = Forms.varchar("shortTitle", 11)
    private val mentorLogin = Forms.varchar("teacherLogin", 30)
    private val classNum = Forms.integer("classNum")
    private val isActive = Forms.bool("isActive")

    fun insert(form: FormDTO) {
        try {
            transaction {
                Forms.insert {
                    it[classNum] = form.classNum
                    it[title] = form.title
                    it[shortTitle] = form.shortTitle
                    it[mentorLogin] = form.mentorLogin
                    it[isActive] = true
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun getAllForms(): List<FormDTO> {
        return transaction {
            Forms.selectAll().map {
                FormDTO(
                    formId = it[Forms.id],
                    classNum = it[classNum],
                    title = it[title],
                    shortTitle = it[shortTitle],
                    mentorLogin = it[mentorLogin],
                    isActive = it[isActive]
                )
            }
        }
    }
}