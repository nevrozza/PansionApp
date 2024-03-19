package com.nevrozq.pansion.database.studentsInForm

import com.nevrozq.pansion.database.tokens.Tokens
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object StudentsInForm : Table() {
    private val formId = StudentsInForm.integer("formId")
    private val login = StudentsInForm.varchar("login", 30).uniqueIndex()


    private fun deleteStudentInFormByLogin(login: String) {
        try {
            transaction {
                StudentsInForm.deleteWhere {
                    (StudentsInForm.login eq login)
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun insert(studentInFormDTO: StudentInFormDTO) {
        try {
            deleteStudentInFormByLogin(studentInFormDTO.login)
            transaction {
                StudentsInForm.insert {
                    it[formId] = studentInFormDTO.formId
                    it[login] = studentInFormDTO.login
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun fetchStudentLoginsInForm(formId: Int): List<String> {
        return transaction {
            try {
                val userForms = StudentsInForm.select { StudentsInForm.formId eq formId }
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

                val userForms = StudentsInForm.selectAll()
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