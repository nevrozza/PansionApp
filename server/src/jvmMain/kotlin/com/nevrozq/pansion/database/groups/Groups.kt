package com.nevrozq.pansion.database.groups

import admin.FormGroupOfSubject
import admin.SubjectGroup
import com.nevrozq.pansion.database.subjects.GSubjects
import journal.init.TeacherGroup
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object Groups : Table() {
    private val id = Groups.integer("id").autoIncrement().uniqueIndex()
    private val name = Groups.varchar("name", 50)
    private val teacherLogin = Groups.varchar("teacherLogin", 30)
    private val gSubjectId = Groups.integer("GSubjectId")
    private val difficult = Groups.varchar("difficult", 1)
    private val isActivated = Groups.bool("isActivated")

    fun insert(group: GroupsDTO) {
        try {
            transaction {
                Groups.insert {
                    it[name] = group.name
                    it[teacherLogin] = group.teacherLogin
                    it[gSubjectId] = group.gSubjectId
                    it[difficult] = group.difficult
                    it[isActivated] = true
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun getAllGroups(): List<Map<Int, GroupsDTO>> {
        return transaction {
            Groups.selectAll().map {
                mapOf(
                    it[Groups.id] to
                            GroupsDTO(
                                name = it[name],
                                teacherLogin = it[teacherLogin],
                                gSubjectId = it[gSubjectId],
                                difficult = it[difficult],
                                isActivated = it[isActivated]
                            )
                )
            }
        }
    }


    fun getGroupsOfGSubjectButFormGroup(gSubjectId: Int): List<FormGroupOfSubject> {
        return transaction {
            try {
                val groups =
                    Groups.select { Groups.gSubjectId eq gSubjectId }
                groups.map {
                    FormGroupOfSubject(
                        id = it[Groups.id],
                        name = it[name]
                    )
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }

    fun getName(groupId: Int): String {
        return transaction {
            try {
                val group =
                    Groups.select { Groups.id eq groupId }.first()
                group[name]
            } catch (e: Throwable) {
                println(e)
                "null"
            }
        }
    }

    fun getGroupsOfGSubject(gSubjectId: Int): List<SubjectGroup> {
        return transaction {
            try {
                val groups =
                    Groups.select { Groups.gSubjectId eq gSubjectId }
                groups.map {
                    SubjectGroup(
                        id = it[Groups.id],
                        name = it[name],
                        teacherLogin = it[teacherLogin],
                        gSubjectId = gSubjectId,
                        difficult = it[difficult],
                        isActivated = it[isActivated]
                    )
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }

    fun getGroupById(groupId: Int): SubjectGroup? {
        return transaction {
            try {
                val group =
                    Groups.select { Groups.id eq groupId }.first()

                SubjectGroup(
                    id = group[Groups.id],
                    name = group[name],
                    teacherLogin = group[teacherLogin],
                    gSubjectId = group[gSubjectId],
                    difficult = group[difficult],
                    isActivated = group[isActivated]
                )

            } catch (e: Throwable) {
                println(e)
                null
            }
        }
    }

    fun getGroupsOfTeacher(teacherLogin: String): List<TeacherGroup> {
        return transaction {
            val subjects = GSubjects.getSubjects()
            Groups.select { Groups.teacherLogin eq teacherLogin }.map { group ->
                TeacherGroup(
                    id = group[Groups.id],
                    name = "${subjects.find { it.id == group[gSubjectId]}?.name ?: "null"} ${group[name]}",
                    subjectNum = group[gSubjectId],
                    isActivated = group[isActivated]
                )
            }
        }
    }

    fun updateGroup(id: Int, groupsDTO: GroupsDTO) {
        try {
            transaction {
                Groups.update({ Groups.id eq id }) {
                    it[name] = groupsDTO.name
                    it[teacherLogin] = groupsDTO.teacherLogin
                    it[gSubjectId] = groupsDTO.gSubjectId
                    it[difficult] = groupsDTO.difficult
                    it[isActivated] = groupsDTO.isActivated
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

}