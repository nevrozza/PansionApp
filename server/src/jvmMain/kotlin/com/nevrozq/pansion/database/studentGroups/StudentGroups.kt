package com.nevrozq.pansion.database.studentGroups

import FIO
import Person
import com.nevrozq.pansion.database.groups.GroupDTO
import com.nevrozq.pansion.database.groups.Groups
import com.nevrozq.pansion.database.subjects.SubjectDTO
import com.nevrozq.pansion.database.subjects.Subjects
import com.nevrozq.pansion.database.users.Users
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object StudentGroups : Table() {
    val groupId = StudentGroups.integer("groupId")
    val subjectId = StudentGroups.integer("subjectId")
    val studentLogin = StudentGroups.varchar("studentLogin", 30)

//    init {
//        index(true, studentLogin)
//    }

//    private val teacherLogin = Groups.varchar("teacherLogin", 30)
//    private val gSubjectId = Groups.integer("GSubjectId")
//    private val difficult = Groups.varchar("difficult", 1)
//    private val isActivated = Groups.bool("isActivated")



    fun insert(studentLessons: StudentGroupDTO) {
        try {
            transaction {
                StudentGroups.delete(studentLessons)
                StudentGroups.insert {
                    it[groupId] = studentLessons.groupId
                    it[studentLogin] = studentLessons.studentLogin
                    it[subjectId] = studentLessons.subjectId
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun delete(studentLessons: StudentGroupDTO) {
        try {
            transaction {
                StudentGroups.deleteWhere { (StudentGroups.groupId eq studentLessons.groupId) and
                        (StudentGroups.subjectId eq studentLessons.subjectId) and
                        (StudentGroups.studentLogin eq studentLessons.studentLogin) }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun fetchAll(): List<StudentGroupDTO> {
        return transaction {
            StudentGroups.selectAll().map {
                StudentGroupDTO(
                    groupId = it[StudentGroups.groupId],
                    subjectId = it[StudentGroups.subjectId],
                    studentLogin = it[StudentGroups.studentLogin]
                )

            }
        }
    }

    fun fetchGroupOfStudentIDS(studentLogin: String): List<Int> {
        return transaction {
            try {

                val groupsIds = StudentGroups.select { StudentGroups.studentLogin eq studentLogin }
                groupsIds.mapNotNull {
                    Groups.getGroupById(it[groupId])?.id
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }

    fun fetchGroupIdsOfStudents(logins: List<String>): List<Int> {
        return transaction {
            val was = mutableListOf<Int>()
            StudentGroups.select { StudentGroups.studentLogin inList logins }.mapNotNull {
                val groupId =
                    it[StudentGroups.groupId]
                if (groupId !in was) {
                    was.add(groupId)
                    groupId
                } else null
            }
        }
    }

    fun fetchGroupsOfStudent(studentLogin: String): List<GroupDTO> {
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


    fun fetchSubjectsOfStudent(studentLogin: String): List<SubjectDTO> {
        return transaction {
            try {

                val groupsIds = StudentGroups.select { StudentGroups.studentLogin eq studentLogin }
                groupsIds.mapNotNull {
                    println("testik ${it[StudentGroups.studentLogin]}")
                    Subjects.getSubjectById(it[subjectId])
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }

    fun fetchStudentsOfGroup(groupId: Int): List<Person> {
        return transaction {
            StudentGroups.select { StudentGroups.groupId eq groupId }.map { group ->
                val user = Users.fetchUser(group[studentLogin])
                Person(
                    login = user!!.login,
                    fio = FIO(
                        name = user.name,
                        surname = user.surname,
                        praname = user.praname
                    ),
                    isActive = user.isActive
                )
            }
        }
    }
}