package com.nevrozq.pansion.database.studentGroups

import FIO
import Person
import com.nevrozq.pansion.database.groups.GroupDTO
import com.nevrozq.pansion.database.groups.Groups
import com.nevrozq.pansion.database.users.Users
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

    fun insert(studentLessons: StudentGroupDTO) {
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
                    isActive = true
                )
            }
        }
    }
}