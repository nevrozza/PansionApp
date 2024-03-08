package com.nevrozq.pansion.database.defaultGroupsForms

import admin.FormGroup
import admin.FormGroupOfSubject
import com.nevrozq.pansion.database.groups.Groups
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object DefaultGroupsForms : Table() {
    private val formId = DefaultGroupsForms.integer("formId")
    private val groupId = DefaultGroupsForms.integer("groupId")
    private val subjectId = DefaultGroupsForms.integer("subjectId")

    fun insert(dGroupFormsDTO: DefaultGroupFormDTO) {
        try {
            transaction {
                DefaultGroupsForms.insert {
                    it[formId] = dGroupFormsDTO.formId
                    it[groupId] = dGroupFormsDTO.groupId
                    it[subjectId] = dGroupFormsDTO.subjectId
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun getGroupsOfThisForm(formId: Int): List<FormGroup> {
        return transaction {
            try {
                val groups =
                    DefaultGroupsForms.select { DefaultGroupsForms.formId eq formId }
                groups.map {
                    FormGroup(
                        id = it[groupId],
                        name = Groups.getName(it[groupId]),
                        gSubjectId = it[subjectId]
                    )
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }
}