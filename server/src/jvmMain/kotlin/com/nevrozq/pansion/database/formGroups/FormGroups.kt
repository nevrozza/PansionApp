package com.nevrozq.pansion.database.formGroups

import admin.groups.forms.FormGroup
import com.nevrozq.pansion.database.groups.Groups
import com.nevrozq.pansion.database.studentGroups.StudentGroupDTO
import com.nevrozq.pansion.database.studentGroups.StudentGroups
import com.nevrozq.pansion.database.studentsInForm.StudentsInForm
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object FormGroups : Table() {
    private val formId = FormGroups.integer("formId")
    private val groupId = FormGroups.integer("groupId")
    private val subjectId = FormGroups.integer("subjectId")

    fun insert(formGroupsDTO: FormGroupDTO) {
        try {
            transaction {
                insert {
                    it[formId] = formGroupsDTO.formId
                    it[groupId] = formGroupsDTO.groupId
                    it[subjectId] = formGroupsDTO.subjectId
                }
                StudentsInForm.fetchStudentLoginsInForm(formGroupsDTO.formId).forEach { studentLogin ->
                    StudentGroups.insert(
                        StudentGroupDTO(
                            groupId = formGroupsDTO.groupId,
                            subjectId = formGroupsDTO.subjectId,
                            studentLogin = studentLogin
                        )
                    )
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun delete(formGroupsDTO: FormGroupDTO) {
        try {
            transaction {
                FormGroups.deleteWhere {
                    (groupId eq formGroupsDTO.groupId) and
                            (subjectId eq formGroupsDTO.subjectId) and
                            (formId eq formGroupsDTO.formId)
                }
                StudentsInForm.fetchStudentLoginsInForm(formGroupsDTO.formId).forEach { studentLogin ->
                    StudentGroups.deleteWhere {
                        (this.groupId eq formGroupsDTO.groupId) and
                                (this.subjectId eq formGroupsDTO.subjectId) and
                                (this.studentLogin eq studentLogin)
                    }
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun getGroupsOfThisForm(formId: Int): List<FormGroupDTO> {
        return transaction {
            try {
                val groups =
                    FormGroups.select { FormGroups.formId eq formId }
                groups.map {
                    FormGroupDTO(
                        formId = it[FormGroups.formId],
                        subjectId = it[subjectId],
                        groupId = it[groupId]
                    )
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }

    //DELETE
}