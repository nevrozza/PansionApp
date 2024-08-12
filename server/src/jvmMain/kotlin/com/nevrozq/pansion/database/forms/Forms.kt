package com.nevrozq.pansion.database.forms

import admin.groups.forms.outside.REditFormReceive
import mentoring.MentorForms
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object Forms : Table() {
    private val id = Forms.integer("id").autoIncrement().uniqueIndex()
    private val title = Forms.varchar("title", 50)
    private val shortTitle = Forms.varchar("shortTitle", 11)
    private val mentorLogin = Forms.varchar("teacherLogin", 30)
    private val classNum = Forms.integer("classNum")
    private val isActive = Forms.bool("isActive")


    fun update(r: REditFormReceive) {
        transaction {
            Forms.update({ Forms.id eq r.id }) {
                it[title] = r.form.title
                it[shortTitle] = r.form.shortTitle
                it[mentorLogin] = r.form.mentorLogin
                it[classNum] = r.form.classNum
            }
        }
    }

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

    fun fetchMentorForms(mentorLogin: String) : List<MentorForms> {
        return transaction {
            Forms.select { Forms.mentorLogin eq mentorLogin }.mapNotNull {
                if(it[isActive]) MentorForms(
                    id = it[Forms.id],
                    num = it[Forms.classNum],
                    title = it[Forms.title]
                )
                else null
            }
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

    fun fetchById(formId: Int): FormDTO {
        return transaction {
            val it = Forms.select { Forms.id eq formId }.first()
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