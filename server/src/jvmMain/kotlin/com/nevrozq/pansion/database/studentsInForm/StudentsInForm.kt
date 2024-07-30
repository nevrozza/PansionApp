package com.nevrozq.pansion.database.studentsInForm

import com.nevrozq.pansion.database.formGroups.FormGroups
import com.nevrozq.pansion.database.forms.FormDTO
import com.nevrozq.pansion.database.studentGroups.StudentGroupDTO
import com.nevrozq.pansion.database.studentGroups.StudentGroups
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import server.Roles

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

    fun fetchStudentsLoginsByFormIds(ids: List<Int>): List<StudentInFormDTO> {
        return transaction {
            StudentsInForm.select { StudentsInForm.formId inList ids }.map {
                StudentInFormDTO(
                    login = it[login],
                    formId = it[formId]
                )
            }
        }
    }

    fun fetchAll(): List<StudentInFormDTO> {
        return try {
            transaction {
                StudentsInForm.selectAll().map {
                    StudentInFormDTO(
                        formId = it[formId],
                        login = it[login]
                    )
                }
            }

        } catch (e: Throwable) {
            listOf()
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

                StudentGroups.deleteWhere {
                    StudentGroups.studentLogin eq studentInFormDTO.login
                }
                FormGroups.getGroupsOfThisForm(studentInFormDTO.formId).forEach {
                    StudentGroups.insert(
                        StudentGroupDTO(
                            groupId = it.groupId,
                            subjectId = it.subjectId,
                            studentLogin = studentInFormDTO.login
                        )
                    )
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

    fun fetchFormIdOfLogin(studentLogin: String): Int {
        return transaction {
            StudentsInForm.select { StudentsInForm.login eq studentLogin }
                .first()[StudentsInForm.formId]
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