package com.nevrozq.pansion.database.homework

import com.nevrozq.pansion.database.reportHeaders.ReportHeaders
import com.nevrozq.pansion.utils.toList
import com.nevrozq.pansion.utils.toStr
import homework.ClientHomeworkItem
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object HomeTasks : Table() {
    val id = HomeTasks.integer("id").autoIncrement().uniqueIndex()
    private val date = this.varchar("date", 10)
    private val time = this.varchar("time", 5)
    val type = this.varchar("type", 5)
    private val subjectId = this.integer("subjectId")
    private val groupId = this.integer("groupId")
    val studentLogins = this.text("studentLogins").nullable()
    private val teacherLogin = this.varchar("teacherLogin", 30)
    val stups = this.integer("stups")
    val text = this.text("text")
    val filesId = this.text("filesId").nullable()
    val isNec = this.bool("isNec")
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
                    it[studentLogins] = ht.studentLogins.toStr()
                    it[teacherLogin] = ht.teacherLogin
                    it[stups] = ht.stups
                    it[text] = ht.text
                    it[filesId] = ht.filesId?.map { it.toString() }.toStr()
                    it[reportId] = ht.reportId
                    it[isNec] = ht.isNecessary
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun fetchPreviousHomeTasks(reportId: Int, groupId: Int): List<String> {
        return transaction {
            val previousReportId = ReportHeaders.fetchPreviousReportId(currentReportId = reportId, groupId = groupId) ?: 0

            (HomeTasks.select { (HomeTasks.groupId eq groupId) and (HomeTasks.reportId eq previousReportId) }.mapNotNull {
                it[type]
            }.toSet() + "!cl5").toList()
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
                    studentLogins = it[HomeTasks.studentLogins].toList(),
                    teacherLogin = it[teacherLogin],
                    stups = it[stups],
                    text = it[text],
                    filesId = it[HomeTasks.filesId].toList()?.map { it.toInt() },
                    isNecessary = it[isNec]
                )
            }
        }
    }

    private fun getClientHomeTasksWithDate(groupIds: List<Int>, login: String, date: String, homeTasksDone: List<HomeTasksDoneDTO>): List<ClientHomeworkItem> {
        return transaction {
            HomeTasks.select { (HomeTasks.groupId inList groupIds) and (HomeTasks.date eq date) }.mapNotNull {
                if( it[studentLogins] == null || it[studentLogins].toList()?.contains(login) == true ) {
                    val htD = homeTasksDone.firstOrNull { h -> h.homeWorkId == it[HomeTasks.id] }
                    ClientHomeworkItem(
                        id = it[HomeTasks.id],
                        date = date,
                        time = it[HomeTasks.time],
                        subjectId = it[subjectId],
                        type = it[type],
                        groupId = it[groupId],
                        text = it[text],
                        stups = it[stups],
                        fileIds = it[filesId].toList()?.map { f -> f.toInt() },
                        seconds = htD?.seconds ?: 0,
                        done = htD?.isDone ?: false,
                        doneId = htD?.id,
                        isNec = it[isNec]
                    )
                } else null
            }
        }
    }

    private fun getClientHomeTasksWithoutDate(groupIds: List<Int>, login: String, homeTasksDone: List<HomeTasksDoneDTO>): List<ClientHomeworkItem> {
        return transaction {
            val setOfDates = mutableSetOf<String>()
            HomeTasks.select { HomeTasks.groupId inList groupIds}
                .sortedBy {
                    val htD = homeTasksDone.firstOrNull { h -> h.homeWorkId == it[HomeTasks.id] }
                    htD?.isDone ?: false
                }
                .mapNotNull {
                val htD = homeTasksDone.firstOrNull { h -> h.homeWorkId == it[HomeTasks.id] }
                if((it[studentLogins] == null || it[studentLogins].toList()?.contains(login) == true) && (htD?.isDone != true || (it[HomeTasks.date] in setOfDates)) ) {
                    if(htD?.isDone != true) {
                        setOfDates.add(it[date])
                    }
                    ClientHomeworkItem(
                        id = it[HomeTasks.id],
                        date = it[HomeTasks.date],
                        time = it[HomeTasks.time],
                        subjectId = it[subjectId],
                        type = it[type],
                        groupId = it[groupId],
                        text = it[text],
                        stups = it[stups],
                        fileIds = it[filesId].toList()?.map { f -> f.toInt() },
                        seconds = htD?.seconds ?: 0,
                        done = htD?.isDone ?: false,
                        doneId = htD?.id,
                        isNec = it[isNec]
                    )
                } else null
            }
        }
    }


    fun getCountNOTDoneNecHomeTasks(groupIds: List<Int>, login: String): Int {
        return transaction {
            val homeTasksDone = HomeTasksDone.getByLogin(login = login)
            HomeTasks.select {(HomeTasks.groupId inList groupIds) and (HomeTasks.isNec eq true)}.count { x ->
                if (x[HomeTasks.studentLogins] == null || login in x[HomeTasks.studentLogins].toList()!!) {
                    val htd = homeTasksDone.firstOrNull { it.homeWorkId == x[HomeTasks.id] }
//                    if(htd == null || !htd.isDone) {
//                        println("COUNTED ${x[HomeTasks.id]}")
//                    }
                    htd == null || !htd.isDone
                } else {
                    false
                }
            }
        }
    }

    fun getClientHomeTasks(groupIds: List<Int>, login: String, date: String?): List<ClientHomeworkItem> {
        return transaction {
            val homeTasksDone = HomeTasksDone.getByLogin(login = login)

            if(date == null) {
                getClientHomeTasksWithoutDate(
                    groupIds = groupIds,
                    login = login,
                    homeTasksDone = homeTasksDone
                )
            } else {
                getClientHomeTasksWithDate(
                    groupIds = groupIds,
                    login = login,
                    date = date,
                    homeTasksDone = homeTasksDone
                )
            }
        }
    }
//    fun getHomeTasksForGroupsLogin(groupIds: List<Int>, login: String, date: String?): List<HomeTasksDTO> {
//        return transaction {
//
//            HomeTasks.select { HomeTasks.groupId inList groupIds}.mapNotNull {
//                if(((it[studentLogins] == null || it[studentLogins].toList()?.contains(login) == true) && date == null || date == it[HomeTasks.date])) {
//                    HomeTasksDTO(
//                        id = it[HomeTasks.id],
//                        date = it[HomeTasks.date],
//                        time = it[time],
//                        type = it[type],
//                        subjectId = it[subjectId],
//                        groupId = it[groupId],
//                        reportId = it[HomeTasks.reportId],
//                        studentLogins = it[HomeTasks.studentLogins].toList(),
//                        teacherLogin = it[teacherLogin],
//                        stups = it[stups],
//                        text = it[text],
//                        filesId = it[HomeTasks.filesId].toList()?.map { it.toInt() }
//                    )
//                } else null
//            }
//        }
//    }
    fun getHomeTasksDateForGroupsLogin(groupIds: List<Int>, login: String): List<String> {
        return transaction {
            HomeTasks.select { HomeTasks.groupId inList groupIds}.mapNotNull {
                if(it[studentLogins] == null || it[studentLogins].toList()?.contains(login) == true) {
                    it[HomeTasks.date]
                } else null
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
                    type = it[type],
                    subjectId = it[subjectId],
                    groupId = it[groupId],
                    reportId = it[HomeTasks.reportId],
                    studentLogins = it[HomeTasks.studentLogins].toList(),
                    teacherLogin = it[teacherLogin],
                    stups = it[stups],
                    text = it[text],
                    filesId = it[HomeTasks.filesId].toList()?.map { it.toInt() },
                    isNecessary = it[isNec]
                )
            }.sortedBy { it.id }
        }
    }
    fun getAllHomeTasksByGroupId(groupId: Int): List<HomeTasksDTO> {
        return transaction {
            HomeTasks.select { (HomeTasks.groupId eq groupId) }.map {
                HomeTasksDTO(
                    id = it[HomeTasks.id],
                    date = it[date],
                    time = it[time],
                    type = it[type],
                    subjectId = it[subjectId],
                    groupId = it[HomeTasks.groupId],
                    reportId = it[HomeTasks.reportId],
                    studentLogins = it[HomeTasks.studentLogins].toList(),
                    teacherLogin = it[teacherLogin],
                    stups = it[stups],
                    text = it[text],
                    filesId = it[HomeTasks.filesId].toList()?.map { it.toInt() },
                    isNecessary = it[isNec]
                )
            }.sortedBy { it.id }
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