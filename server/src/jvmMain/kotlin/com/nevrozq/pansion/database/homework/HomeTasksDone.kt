package com.nevrozq.pansion.database.homework

import com.nevrozq.pansion.database.subjects.Subjects
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object HomeTasksDone : Table() {
    val id = HomeTasksDone.integer("id").autoIncrement().uniqueIndex()
    private val studentLogin = this.varchar("studentLogin", 30)
    private val isDone = this.bool("isDone")
    private val homeWorkId = this.integer("homeWorkId")
    private val seconds = this.integer("seconds")
    private val zabil = this.bool("zabil")

    fun insert(htd: HomeTasksDoneDTO) {
        try {
            transaction {
                HomeTasksDone.insert {
                    it[studentLogin] = htd.studentLogin
                    it[isDone] = htd.isDone
                    it[homeWorkId] = htd.homeWorkId
                    it[seconds] = htd.seconds
                    it[zabil] = htd.zabil
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

//    fun getAllHomeTasks(): List<HomeTasksDTO> {
//        return transaction {
//            HomeTasksDone.selectAll().map {
//                HomeTasksDTO(
//                    id = it[HomeTasksDone.id],
//                    date = it[date],
//                    time = it[time],
//                    type = it[time],
//                    subjectId = it[subjectId],
//                    groupId = it[groupId],
//                    reportId = it[reportId],
//                    studentLogin = it[HomeTasksDone.studentLogin],
//                    teacherLogin = it[teacherLogin],
//                    stups = it[stups],
//                    text = it[text],
//                    filesId = it[HomeTasksDone.filesId]
//                )
//            }
//        }
//    }
//    fun getAllHomeTasksByReportId(reportId: Int): List<HomeTasksDTO> {
//        return transaction {
//            HomeTasksDone.select { HomeTasksDone.reportId eq reportId }.map {
//                HomeTasksDTO(
//                    id = it[HomeTasksDone.id],
//                    date = it[date],
//                    time = it[time],
//                    type = it[time],
//                    subjectId = it[subjectId],
//                    groupId = it[groupId],
//                    reportId = it[HomeTasksDone.reportId],
//                    studentLogin = it[HomeTasksDone.studentLogin],
//                    teacherLogin = it[teacherLogin],
//                    stups = it[stups],
//                    text = it[text],
//                    filesId = it[HomeTasksDone.filesId]
//                )
//            }
//        }
//    }

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