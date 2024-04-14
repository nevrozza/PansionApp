package com.nevrozq.pansion.database.studentLines

import com.nevrozq.pansion.database.groups.GroupDTO
import com.nevrozq.pansion.database.groups.Groups
import com.nevrozq.pansion.database.reportHeaders.ReportHeaders
import com.nevrozq.pansion.database.reportHeaders.ReportHeaders.autoIncrement
import com.nevrozq.pansion.database.reportHeaders.ReportHeaders.uniqueIndex
import com.nevrozq.pansion.database.studentsInForm.StudentInFormDTO
import com.nevrozq.pansion.database.studentsInForm.StudentsInForm
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object StudentLines : Table() {
     val reportId = reference("reportId", ReportHeaders.id)
    //attended
    val groupId = StudentLines.integer("groupId")
    val login = StudentLines.varchar("login", 30)
    val lateTime = StudentLines.varchar("lateTime", 10)
    val isLiked = StudentLines.varchar("isLiked", 1)




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
            if(isDelete) {
                deleteStudentLine(studentLinesDTO.login, studentLinesDTO.reportId)
            }
            transaction {
                StudentLines.insert {
                    it[reportId] = studentLinesDTO.reportId
                    it[groupId] = studentLinesDTO.groupId
                    it[login] = studentLinesDTO.login
                    it[lateTime] = studentLinesDTO.lateTime
                    it[isLiked] = studentLinesDTO.isLiked
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun deleteAllLinesOfReport(reportId: Int) {
        transaction {
            try {
                StudentLines.deleteWhere { StudentLines.reportId eq reportId }
            } catch (_: Throwable) {

            }
        }
    }

    fun fetchStudentLinesOfReport(reportId: Int): List<StudentLinesDTO> {
        return transaction {
            try {
                val studentLines =
                    StudentLines.select(StudentLines.reportId eq reportId)

                studentLines.forEach {
                    println("sadik0 ${it[StudentLines.login]}")
                }
                studentLines.map {
                    StudentLinesDTO(
                        reportId = it[StudentLines.reportId],
                        groupId = it[groupId],
                        login = it[login],
                        lateTime = it[lateTime],
                        isLiked = it[isLiked]
                    )
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }
}