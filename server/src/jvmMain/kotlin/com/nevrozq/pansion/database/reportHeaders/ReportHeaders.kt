package com.nevrozq.pansion.database.reportHeaders

import com.nevrozq.pansion.database.groups.Groups
import com.nevrozq.pansion.database.ratingEntities.Marks
import com.nevrozq.pansion.database.ratingEntities.RatingEntityDTO
import com.nevrozq.pansion.database.ratingEntities.Stups
import com.nevrozq.pansion.database.studentLines.StudentLines
import com.nevrozq.pansion.database.studentLines.StudentLinesDTO
import com.nevrozq.pansion.database.subjects.Subjects
import com.nevrozq.pansion.database.users.Users
import com.nevrozq.pansion.utils.getModuleByDate
import com.nevrozq.pansion.utils.toList
import com.nevrozq.pansion.utils.toStr
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import report.RCreateReportReceive
import report.RUpdateReportReceive
import report.ServerRatingUnit
import javax.management.monitor.StringMonitor
import kotlin.math.abs

object ReportHeaders : Table() {
    val id = ReportHeaders.integer("id").autoIncrement().uniqueIndex()
    private val subjectId = reference("subjectId", Subjects.id)
    private val subjectName = ReportHeaders.varchar("subjectName", 50)
    private val groupId = ReportHeaders.integer("groupId")
    private val groupName = ReportHeaders.varchar("groupName", 50)
    private val teacherLogin = ReportHeaders.reference("teacherLogin", Users.login)
    private val teacherName = ReportHeaders.varchar("teacherName", 50)
    private val date = ReportHeaders.varchar("date", 10)
    private val time = ReportHeaders.varchar("time", 5)
    private val editTime = ReportHeaders.varchar("editTime", 18)
    private val topic = ReportHeaders.text("topic")
    private val description = ReportHeaders.text("description")
    private val customColumns = ReportHeaders.varchar("customColumns", 255)
    private val isMentorWas = ReportHeaders.bool("isMentorWas")
    private val status = ReportHeaders.bool("status")
    private val ids = ReportHeaders.integer("ids")
    private val module = ReportHeaders.varchar("module", 1)


    private fun deleteReportHeader(reportId: Int) {
        try {
            transaction {
                ReportHeaders.deleteWhere {
                    (ReportHeaders.id eq reportId)
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun fetchPreviousReportId(currentReportId: Int, groupId: Int): Int? {
        return transaction {
            try {
                ReportHeaders.select { (ReportHeaders.id.less(currentReportId)) and (ReportHeaders.groupId eq groupId) }.lastOrNull()
                    ?.get(ReportHeaders.id)
            } catch (_: Throwable) {
                null
            }
        }
    }

    fun fetchReportHeaders(): List<ReportHeadersDTO> {
        println("xxy1")
        return transaction {
            ReportHeaders.selectAll().map {
                ReportHeadersDTO(
                    id = it[ReportHeaders.id],
                    subjectId = it[ReportHeaders.subjectId],
                    groupId = it[ReportHeaders.groupId],
                    date = it[ReportHeaders.date],
                    time = it[ReportHeaders.time],
                    topic = it[ReportHeaders.topic],
                    description = it[ReportHeaders.description],
                    customColumns = it[ReportHeaders.customColumns].toList() ?: listOf(),
                    isMentorWas = it[ReportHeaders.isMentorWas],
                    status = it[ReportHeaders.status],
                    ids = it[ReportHeaders.ids],
                    editTime = it[ReportHeaders.editTime],
                    subjectName = it[subjectName],
                    groupName = it[groupName],
                    teacherLogin = it[ReportHeaders.teacherLogin],
                    teacherName = it[teacherName],
                    module = it[module]
                )
            }

        }
    }


    fun createReport(r: RCreateReportReceive, teacherLogin: String): Int {
        return transaction {
            val teacher = Users.fetchUser(teacherLogin)!!
            val tN =
                "${teacher.surname} ${teacher.name[0]}.${if (teacher.praname != null) " " + teacher.praname[0] + "." else ""}"
            val cSubjectId = Groups.fetchSubjectIdOfGroup(r.groupId)
            val reportId = ReportHeaders.insert {

                if (cSubjectId != -1) {
                    it[subjectId] = cSubjectId
                } else Throwable("there is no subjectId")
                it[groupId] = r.groupId
                it[date] = r.date
                it[time] = r.time
                it[editTime] = ""
                it[topic] = ""
                it[description] = ""
                it[customColumns] = ""
                it[isMentorWas] = false
                it[status] = false
                it[ids] = 0
                it[subjectName] = Subjects.fetchName(cSubjectId)
                it[groupName] = Groups.getName(r.groupId)
                it[ReportHeaders.teacherLogin] = teacherLogin
                it[teacherName] = tN
                it[module] = getModuleByDate(r.date)?.num?.toString() ?: "1"
            }[ReportHeaders.id]

            r.studentLogins.forEach {
                StudentLines.insert(
                    StudentLinesDTO(
                        reportId = reportId,
                        groupId = r.groupId,
                        login = it,
                        lateTime = "0",
                        isLiked = "",
                        attended = null,
                        aReason = null
                    ),
                    isDelete = false
                )
            }

            reportId
            //ReportHeaders.select { ReportHeaders.groupId eq groupId }.last()
        }
    }


    fun updateWholeReport(r: RUpdateReportReceive) {
        transaction {
            val pHeader = ReportHeaders.fetchHeader(r.lessonReportId)

//            val pStudents = StudentLines.fetchStudentLinesOfReport(r.lessonReportId)

//            deleteReportHeader(r.lessonReportId)
//            println(r.columnNames.toStr())
            ReportHeaders.update({ ReportHeaders.id eq r.lessonReportId }) {
                it[editTime] = r.editTime
                it[topic] = if (r.topic == "!!") pHeader.topic else r.topic
                it[description] =
                    if (r.description == "!!") pHeader.description else r.description
                it[customColumns] = r.columnNames.toStr() ?: ""
                it[isMentorWas] = r.isMentorWas
                it[status] = r.status
                it[ids] = r.ids
            }

//            if (pStudents.size == r.students.size) {
            r.students.forEach { student ->
//                    val pLine = pStudents.first { it.login == student.login }
//                    if (with(student) {
//                            pLine.isLiked != isLiked ||
//                                    pLine.lateTime != lateTime
//                        }) {
                StudentLines.update({ (StudentLines.reportId eq r.lessonReportId) and (StudentLines.login eq student.login) }) {
                    it[StudentLines.isLiked] = student.isLiked
                    it[StudentLines.lateTime] = student.lateTime
                    it[StudentLines.attended] = student.attended?.attendedType
                    it[StudentLines.aReason] = student.attended?.reason
//                        }
//                        StudentLines.insert(
//                            StudentLinesDTO(
//                                reportId = r.lessonReportId,
//                                groupId = r.groupId,
//                                login = student.login,
//                                lateTime = student.lateTime,
//                                isLiked = student.isLiked
//                            ),
//                            isDelete = true
//                        )
                }
            }
//            } else {
//                StudentLines.deleteAllLinesOfReport(r.lessonReportId)
//                r.students.forEach {
//                    StudentLines.insert(
//                        StudentLinesDTO(
//                            reportId = r.lessonReportId,
//                            groupId = r.groupId,
//                            login = it.login,
//                            lateTime = it.lateTime,
//                            isLiked = it.isLiked
//                        ), false
//                    )
//                }
//            }

            listOf(Pair(Marks, r.marks), Pair(Stups, r.stups)).forEach { pair ->
                val db = pair.first
                val c = pair.second
                val p = db.fetchForReport(r.lessonReportId).map {
                    ServerRatingUnit(
                        login = it.login,
                        id = it.id,
                        content = it.content,
                        reason = it.reason,
                        isGoToAvg = it.isGoToAvg,
                        deployTime = it.deployTime,
                        deployDate = it.deployDate,
                        deployLogin = it.deployLogin
                    )
                }
                val toDelete = p - c.toSet()
                val toAdd = c - p.toSet()
                println("xxxsad: ${toAdd}")
                for (i in toDelete) {
                    db.delete(id = i.id, reportId = r.lessonReportId)
                }
                for (i in toAdd.filter { abs(it.content.toInt()) > 0 }) {
                    db.insert(
                        r = RatingEntityDTO(
                            groupId = pHeader.groupId,
                            reportId = r.lessonReportId,
                            login = i.login,
                            content = i.content,
                            reason = i.reason,
                            id = i.id,
                            part = pHeader.module,
                            isGoToAvg = i.isGoToAvg,
                            subjectId = pHeader.subjectId,
                            date = pHeader.date,
                            deployTime = i.deployTime,
                            deployLogin = i.deployLogin,
                            deployDate = i.deployDate
                        ),
                        isDelete = false
                    )
                }
            }


        }
    }


    fun fetchHeader(reportId: Int): ReportHeadersDTO {
        return transaction {
            ReportHeaders.select { ReportHeaders.id eq reportId }.map {
                ReportHeadersDTO(
                    id = it[ReportHeaders.id],
                    subjectId = it[ReportHeaders.subjectId],
                    groupId = it[ReportHeaders.groupId],
                    date = it[ReportHeaders.date],
                    time = it[ReportHeaders.time],
                    topic = it[ReportHeaders.topic],
                    description = it[ReportHeaders.description],
                    customColumns = it[ReportHeaders.customColumns].toList() ?: listOf(),
                    isMentorWas = it[ReportHeaders.isMentorWas],
                    status = it[ReportHeaders.status],
                    ids = it[ReportHeaders.ids],
                    editTime = it[ReportHeaders.editTime],
                    subjectName = it[subjectName],
                    groupName = it[groupName],
                    teacherName = it[teacherName],
                    teacherLogin = it[teacherLogin],
                    module = it[module]
                )
            }.first()
        }
    }
    fun fetchDate(reportId: Int): ReportTimeDate {
        return transaction {
            ReportHeaders.select { ReportHeaders.id eq reportId }.map {
                ReportTimeDate(
                    date = it[date],
                    start = it[time]
                )
            }.first()
        }
    }
}

data class ReportTimeDate(
    val date: String,
    val start: String
)