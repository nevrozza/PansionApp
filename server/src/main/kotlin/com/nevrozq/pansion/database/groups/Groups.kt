package com.nevrozq.pansion.database.groups

import admin.groups.subjects.REditGroupReceive
import com.nevrozq.pansion.database.subjects.SubjectDTO
import com.nevrozq.pansion.database.subjects.Subjects
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object Groups : Table() {
    val id = Groups.integer("id").autoIncrement().uniqueIndex()
    private val name = Groups.varchar("name", 50)
    private val teacherLogin = Groups.varchar("teacherLogin", 30)
    private val subjectId = Groups.integer("subjectId")
    private val difficult = Groups.varchar("difficult", 1)
    private val isActive = Groups.bool("isActive")

    fun update(id: Int, r: REditGroupReceive) {
        transaction {
            Groups.update({Groups.id eq id}) {
                it[name] = r.name
                it[teacherLogin] = r.mentorLogin
                it[difficult] = r.difficult
                it[isActive] = r.isActive
            }
        }
    }

    fun insert(group: GroupDTO) {
        try {
            transaction {
                Groups.insert {
                    it[name] = group.name
                    it[teacherLogin] = group.teacherLogin
                    it[subjectId] = group.subjectId
                    it[difficult] = group.difficult
                    it[isActive] = true
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun getAllGroups(): List<GroupDTO> {
        return transaction {
            Groups.selectAll().map {
                GroupDTO(
                    id = it[Groups.id],
                    name = it[name],
                    teacherLogin = it[teacherLogin],
                    subjectId = it[subjectId],
                    difficult = it[difficult],
                    isActive = it[isActive]
                )

            }
        }
    }


    fun fetchGroupOfSubject(subjectId: Int): List<GroupDTO> {
        return transaction {
            try {
                val groups =
                    Groups.select { Groups.subjectId eq subjectId }
                groups.map {
                    GroupDTO(
                        id = it[Groups.id],
                        name = it[name],
                        teacherLogin = it[teacherLogin],
                        subjectId = it[Groups.subjectId],
                        difficult = it[difficult],
                        isActive = it[isActive]
                    )
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }

    fun fetchSubjectIdOfGroup(groupId: Int): Int {
        return transaction {
            try {
                val group =
                    Groups.select { Groups.id eq groupId }.first()
                group[subjectId]
            } catch (e: Throwable) {
                println(e)
                -1
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

//    fun getGroupsOfGSubject(gSubjectId: Int): List<SubjectGroup> {
//        return transaction {
//            try {
//                val groups =
//                    Groups.select { Groups.subjectId eq gSubjectId }
//                groups.map {
//                    SubjectGroup(
//                        id = it[Groups.id],
//                        name = it[name],
//                        teacherLogin = it[teacherLogin],
//                        gSubjectId = gSubjectId,
//                        difficult = it[difficult],
//                        isActivated = it[isActive]
//                    )
//                }
//            } catch (e: Throwable) {
//                println(e)
//                listOf()
//            }
//        }
//    }

    fun getGroupById(groupId: Int): GroupDTO? {
        return transaction {
            try {
                val group =
                    Groups.select { Groups.id eq groupId }.first()

                GroupDTO(
                    id = group[Groups.id],
                    name = group[name],
                    teacherLogin = group[teacherLogin],
                    subjectId = group[subjectId],
                    difficult = group[difficult],
                    isActive = group[isActive]
                )

            } catch (e: Throwable) {
                println(e)
                null
            }
        }
    }

    // val subjects = GSubjects.getSubjects()
    //"${subjects.find { it.id == group[subjectId] }?.name ?: "null"} ${group[name]}"
    fun getGroupsOfTeacher(teacherLogin: String): List<GroupDTO> {
        return transaction {
            Groups.select { Groups.teacherLogin eq teacherLogin }.map { group ->
                GroupDTO(
                    id = group[Groups.id],
                    name = group[name],
                    teacherLogin = group[Groups.teacherLogin],
                    subjectId = group[subjectId],
                    difficult = group[difficult],
                    isActive = group[isActive]
                )
            }
        }
    }

    fun getTeacherLogin(id: Int): String {
        return transaction {
            Groups.select { Groups.id eq id }.first()[teacherLogin]
        }
    }

//    fun updateGroup(id: Int, groupDTO: GroupDTO) {
//        try {
//            transaction {
//                Groups.update({ Groups.id eq id }) {
//                    it[name] = groupDTO.name
//                    it[teacherLogin] = groupDTO.teacherLogin
//                    it[subjectId] = groupDTO.subjectId
//                    it[difficult] = groupDTO.difficult
//                    it[isActive] = groupDTO.isActive
//                }
//            }
//        } catch (e: Throwable) {
//            println(e)
//        }
//    }

}