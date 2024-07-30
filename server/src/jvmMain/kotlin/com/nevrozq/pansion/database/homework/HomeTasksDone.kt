package com.nevrozq.pansion.database.homework


import com.nevrozq.pansion.database.homework.HomeTasks.type
import com.nevrozq.pansion.database.subjects.Subjects
import com.nevrozq.pansion.database.users.Users
import com.nevrozq.pansion.utils.toList
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import server.DataLength
import server.cut
import javax.management.Query.and

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


    fun checkTask(login: String, homeWorkId: Int, id: Int?, isDone: Boolean) {
        transaction {
            if(id != null || HomeTasksDone.select { (HomeTasksDone.studentLogin eq login) and (HomeTasksDone.homeWorkId eq homeWorkId) }
                    .map { it[HomeTasksDone.id] }.isNotEmpty()) {
                HomeTasksDone.update({ (HomeTasksDone.studentLogin eq login) and (HomeTasksDone.homeWorkId eq homeWorkId) }) {
                    it[HomeTasksDone.isDone] = isDone
                }
            } else {
                insert(
                    HomeTasksDoneDTO(
                        id = 0,
                        studentLogin = login,
                        isDone = isDone,
                        homeWorkId = homeWorkId,
                        seconds = 0,
                        zabil = false
                    )
                )
            }
        }
    }


    fun getByLogin(login: String): List<HomeTasksDoneDTO> {
        return transaction {
            HomeTasksDone.select {HomeTasksDone.studentLogin eq login}.map {
                HomeTasksDoneDTO(
                    id = it[HomeTasksDone.id],
                    studentLogin = it[HomeTasksDone.studentLogin],
                    isDone = it[isDone],
                    homeWorkId = it[homeWorkId],
                    seconds = it[seconds],
                    zabil = it[zabil]
                )
            }
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