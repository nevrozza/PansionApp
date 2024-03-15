package com.nevrozq.pansion.database.formGroups

import admin.groups.forms.FormGroup
import com.nevrozq.pansion.database.groups.Groups
import org.jetbrains.exposed.sql.Table
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