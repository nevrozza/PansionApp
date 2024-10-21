package com.nevrozq.pansion.database.studentLines

import com.nevrozq.pansion.database.preAttendance.PreAttendance
import com.nevrozq.pansion.database.reportHeaders.ReportHeaders
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import report.ClientStudentLine
import server.toMinutes

object StudentLines : Table() {
    val reportId = reference("reportId", ReportHeaders.id)

    //attended
    val attended = this.varchar("attended", 1).nullable() //0-bil. 1-n. 2-Uv
    val aReason = this.text("aReason").nullable() //0-bil. 1-n. 2-Uv
    val groupId = StudentLines.integer("groupId")
    val login = StudentLines.varchar("login", 30)
    val lateTime = StudentLines.varchar("lateTime", 10)
    val isLiked = StudentLines.varchar("isLiked", 1)
    val module = this.varchar("module", 1)

    val subjectN = this.varchar("SubjectName", 50)
    val groupN = this.varchar("GroupName", 50)
    val timeN = this.varchar("time", 5)
    val dateN = this.varchar("date", 10)


    private fun deleteStudentLine(login: String, reportId: Int) {
        try {
            transaction {
                StudentLines.deleteWhere {
                    (StudentLines.login eq login) and (StudentLines.reportId eq reportId)
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun insert(studentLinesDTO: StudentLinesDTO, isDelete: Boolean) {
        try {
            if (isDelete) {
                deleteStudentLine(studentLinesDTO.login, studentLinesDTO.reportId)
            }
            transaction {
                StudentLines.insert {
                    it[reportId] = studentLinesDTO.reportId
                    it[groupId] = studentLinesDTO.groupId
                    it[login] = studentLinesDTO.login
                    it[lateTime] = studentLinesDTO.lateTime
                    it[isLiked] = studentLinesDTO.isLiked
                    it[attended] = studentLinesDTO.attended
                    it[aReason] = studentLinesDTO.aReason
                    it[subjectN] = studentLinesDTO.subjectName
                    it[groupN] = studentLinesDTO.groupName
                    it[timeN] = studentLinesDTO.time
                    it[dateN] = studentLinesDTO.date
                    it[module] = studentLinesDTO.module
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

//    fun deleteAllLinesOfReport(reportId: Int) {
//        transaction {
//            try {
//                StudentLines.deleteWhere { StudentLines.reportId eq reportId }
//            } catch (_: Throwable) {
//
//            }
//        }
//    }

    fun fetchStudentLinesOfReport(reportId: Int): List<StudentLinesDTO> {
        return transaction {
            try {
                val studentLines =
                    StudentLines.select(StudentLines.reportId eq reportId)
                studentLines.map {
                    StudentLinesDTO(
                        reportId = it[StudentLines.reportId],
                        groupId = it[groupId],
                        login = it[login],
                        lateTime = it[lateTime],
                        isLiked = it[isLiked],
                        attended = it[attended],
                        aReason = it[aReason],
                        subjectName = it[subjectN],
                        groupName = it[groupN],
                        time = it[timeN],
                        date = it[dateN],
                        module = it[module]
                    )
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }

    fun fetchStudentLinesByLogin(login: String): List<StudentLinesDTO> {
        return transaction {
            try {
                val studentLines =
                    StudentLines.select(StudentLines.login eq login)
                studentLines.map {
                    StudentLinesDTO(
                        reportId = it[reportId],
                        groupId = it[groupId],
                        login = it[StudentLines.login],
                        lateTime = it[lateTime],
                        isLiked = it[isLiked],
                        attended = it[attended],
                        aReason = it[aReason],
                        subjectName = it[subjectN],
                        groupName = it[groupN],
                        time = it[timeN],
                        date = it[dateN],
                        module = it[module]
                    )
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }

    fun fetchStudentLinesByLoginAndGroup(login: String, groupId: Int): List<StudentLinesDTO> {
        return transaction {
            try {
                val studentLines =
                    StudentLines.select{(StudentLines.login eq login) and (StudentLines.groupId eq groupId)}
                studentLines.map {
                    StudentLinesDTO(
                        reportId = it[reportId],
                        groupId = it[StudentLines.groupId],
                        login = it[StudentLines.login],
                        lateTime = it[lateTime],
                        isLiked = it[isLiked],
                        attended = it[attended],
                        aReason = it[aReason],
                        subjectName = it[subjectN],
                        groupName = it[groupN],
                        time = it[timeN],
                        date = it[dateN],
                        module = it[module]
                    )
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }

    fun fetchClientStudentLines(login: String): List<ClientStudentLine> {
        return transaction {
            try {
                val studentLines =
                    StudentLines.select(StudentLines.login eq login)
                studentLines.map {
                    val reportHeader = ReportHeaders.fetchHeader(it[reportId])

                    var preAttendance = PreAttendance.fetchPreAttendanceByDateAndLogin(
                        date = it[dateN],
                        login = login
                    )

                    val minutes = it[timeN].toMinutes()

                    preAttendance =
                        if (preAttendance != null && preAttendance.start.toMinutes() <= minutes && preAttendance.end.toMinutes() > minutes) preAttendance else null

                    ClientStudentLine(
                        reportId = it[reportId],
//                        groupId = it[groupId],
//                        login = it[StudentLines.login],
                        lateTime = it[lateTime],
                        isLiked = it[isLiked],
                        attended = if (it[attended] != null) it[attended] else if (preAttendance != null) if (preAttendance.isGood) "2" else "1" else null,
//                        aReason = it[aReason],
                        subjectName = it[subjectN],
                        groupName = it[groupN],
                        time = it[timeN],
                        date = it[dateN],
                        login= it[StudentLines.login],
                        topic = reportHeader.topic
                    )
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }

    fun fetchClientStudentLine(login: String, reportId: Int): ClientStudentLine? {
        return transaction {
            try {
                val it =
                    StudentLines.select((StudentLines.login eq login) and (StudentLines.reportId eq reportId))
                        .first()

                var preAttendance = PreAttendance.fetchPreAttendanceByDateAndLogin(
                    date = it[dateN],
                    login = login
                )

                val minutes = it[timeN].toMinutes()

                preAttendance =
                    if (preAttendance != null && preAttendance.start.toMinutes() <= minutes && preAttendance.end.toMinutes() > minutes) preAttendance else null

                ClientStudentLine(
                    reportId = it[StudentLines.reportId],
                    lateTime = it[lateTime],
                    isLiked = it[isLiked],
                    attended = if (it[attended] != null) it[attended] else if (preAttendance != null) if (preAttendance.isGood) "2" else "1" else null,
                    subjectName = it[subjectN],
                    groupName = it[groupN],
                    time = it[timeN],
                    date = it[dateN],
                    login = it[StudentLines.login],
                    topic = "" //U GET IT LATER!!
                )

            } catch (e: Throwable) {
                println(e)
                null
            }
        }
    }
}