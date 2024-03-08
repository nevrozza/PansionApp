package com.nevrozq.pansion.database.userForms

import admin.Student
import admin.SubjectGroup
import com.nevrozq.pansion.database.forms.Forms
import com.nevrozq.pansion.database.forms.Forms.autoIncrement
import com.nevrozq.pansion.database.forms.Forms.uniqueIndex
import com.nevrozq.pansion.database.forms.FormsDTO
import com.nevrozq.pansion.database.groups.Groups
import com.nevrozq.pansion.database.studentLessons.StudentGroups
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object UserForms : Table() {
    private val formId = UserForms.integer("formId")
    private val login = UserForms.varchar("login", 30).uniqueIndex()


    fun insert(userForm: UserFormsDTO) {
        try {
            transaction {
                UserForms.insert {
                    it[formId] = userForm.formId
                    it[login] = userForm.login
                }
            }
        } catch (e: Throwable) {
            println(e)
            transaction {
                UserForms.deleteWhere { UserForms.login eq userForm.login }
                UserForms.insert {
                    it[formId] = userForm.formId
                    it[login] = userForm.login
                }
            }
        }
    }

    fun getStudentLoginsInForm(formId: Int): List<String> {
        return transaction {
            try {

                val userForms = UserForms.select { UserForms.formId eq formId}
                userForms.mapNotNull {
                    it[login]
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }

    fun fetchAllStudentsLogins(): List<String> {
        return transaction {
            try {

                val userForms = UserForms.selectAll()
                userForms.map {
                    it[login]
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }
}