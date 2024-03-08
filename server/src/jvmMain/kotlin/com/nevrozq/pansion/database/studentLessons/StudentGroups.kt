package com.nevrozq.pansion.database.studentLessons

import admin.SubjectGroup
import com.nevrozq.pansion.database.groups.Groups
import com.nevrozq.pansion.database.subjects.GSubjects
import com.nevrozq.pansion.database.users.Users
import journal.init.StudentInGroup
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object StudentGroups : Table() {
    private val groupId = StudentGroups.integer("groupId")
    private val studentLogin = StudentGroups.varchar("studentLogin", 30).uniqueIndex()

//    init {
//        index(true, studentLogin)
//    }

//    private val teacherLogin = Groups.varchar("teacherLogin", 30)
//    private val gSubjectId = Groups.integer("GSubjectId")
//    private val difficult = Groups.varchar("difficult", 1)
//    private val isActivated = Groups.bool("isActivated")

    fun insert(studentLessons: StudentLessonsDTO) {
        try {
            transaction {

                StudentGroups.insert {
                    it[groupId] = studentLessons.groupId
                    it[studentLogin] = studentLessons.studentLogin
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun getGroupsOfStudent(studentLogin: String): List<SubjectGroup> {
        return transaction {
            try {

                val groupsIds = StudentGroups.select { StudentGroups.studentLogin eq studentLogin }
                groupsIds.mapNotNull {
                    Groups.getGroupById(it[groupId])
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }

    fun getStudentsOfGroup(groupId: Int): List<StudentInGroup> {
        return transaction {
            StudentGroups.select { StudentGroups.groupId eq groupId }.map { group ->
                val user = Users.fetchUser(group[studentLogin])
                StudentInGroup(
                    login = user!!.login,
                    name = user.name,
                    surname = user.surname,
                    praname = user.praname,
                    isActivated = true
                )
            }
        }
    }
}