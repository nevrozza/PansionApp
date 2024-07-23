package com.nevrozq.pansion.database.homework

import com.nevrozq.pansion.database.subjects.Subjects
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object HomeTasks : Table() {
    val id = HomeTasks.integer("id").autoIncrement().uniqueIndex()
    private val date = this.varchar("date", 10)
    private val time = this.varchar("time", 5)
    private val type = this.varchar("type", 5)
    private val subjectId = this.integer("subjectId")
    private val groupId = this.integer("groupId")
    private val studentLogin = this.varchar("studentLogin", 30).nullable()
    private val teacherLogin = this.varchar("teacherLogin", 30)
    private val stups = this.integer("stups")
    private val text = this.text("text")
    private val filesId = this.text("filesId").nullable()
    private val reportId = this.integer("reportId")

    fun insert(ht: HomeTasksDTO) {
        try {
            transaction {
                HomeTasks.insert {
                    it[date] = ht.date
                    it[time] = ht.time
                    it[type] = ht.type
                    it[subjectId] = ht.subjectId
                    it[groupId] = ht.groupId
                    it[studentLogin] = ht.studentLogin
                    it[teacherLogin] = ht.teacherLogin
                    it[stups] = ht.stups
                    it[text] = ht.text
                    it[filesId] = ht.filesId
                    it[reportId] = ht.reportId
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun getAllHomeTasks(): List<HomeTasksDTO> {
        return transaction {
            HomeTasks.selectAll().map {
                HomeTasksDTO(
                    id = it[HomeTasks.id],
                    date = it[date],
                    time = it[time],
                    type = it[time],
                    subjectId = it[subjectId],
                    groupId = it[groupId],
                    reportId = it[reportId],
                    studentLogin = it[HomeTasks.studentLogin],
                    teacherLogin = it[teacherLogin],
                    stups = it[stups],
                    text = it[text],
                    filesId = it[HomeTasks.filesId]
                )
            }
        }
    }
    fun getAllHomeTasksByReportId(reportId: Int): List<HomeTasksDTO> {
        return transaction {
            HomeTasks.select { HomeTasks.reportId eq reportId }.map {
                HomeTasksDTO(
                    id = it[HomeTasks.id],
                    date = it[date],
                    time = it[time],
                    type = it[time],
                    subjectId = it[subjectId],
                    groupId = it[groupId],
                    reportId = it[HomeTasks.reportId],
                    studentLogin = it[HomeTasks.studentLogin],
                    teacherLogin = it[teacherLogin],
                    stups = it[stups],
                    text = it[text],
                    filesId = it[HomeTasks.filesId]
                )
            }
        }
    }

//    fun getAllHomeTasksGroups(): List<HomeTasksDTO> {
//        return transaction {
//            HomeTasks.selectAll().map {
//                HomeTasksDTO(
//                    id = it[HomeTasks.id],
//                    date = it[date],
//                    time = it[time],
//                    type = it[time],
//                    subjectId = it[subjectId],
//                    groupId = it[groupId],
//                    reportId = it[reportId],
//                    studentLogin = it[HomeTasks.studentLogin],
//                    teacherLogin = it[teacherLogin],
//                    stups = it[stups],
//                    text = it[text],
//                    filesId = it[HomeTasks.filesId]
//                )
//            }
//        }
//    }
//
//    fun getGroupById(groupId: Int): GroupDTO? {
//        return transaction {
//            try {
//                val group =
//                    Groups.select { Groups.id eq groupId }.first()
//
//                GroupDTO(
//                    id = group[Groups.id],
//                    name = group[name],
//                    teacherLogin = group[teacherLogin],
//                    subjectId = group[subjectId],
//                    difficult = group[difficult],
//                    isActive = group[isActive]
//                )
//
//            } catch (e: Throwable) {
//                println(e)
//                null
//            }
//        }
//    }

    // val subjects = GSubjects.getSubjects()
    //"${subjects.find { it.id == group[subjectId] }?.name ?: "null"} ${group[name]}"
//    fun getGroupsOfTeacher(teacherLogin: String): List<GroupDTO> {
//        return transaction {
//            Groups.select { Groups.teacherLogin eq teacherLogin }.map { group ->
//                GroupDTO(
//                    id = group[Groups.id],
//                    name = group[name],
//                    teacherLogin = group[Groups.teacherLogin],
//                    subjectId = group[subjectId],
//                    difficult = group[difficult],
//                    isActive = group[isActive]
//                )
//            }
//        }
//    }
//
//    fun getTeacherLogin(id: Int): String {
//        return transaction {
//            Groups.select { Groups.id eq id }.first()[teacherLogin]
//        }
//    }

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