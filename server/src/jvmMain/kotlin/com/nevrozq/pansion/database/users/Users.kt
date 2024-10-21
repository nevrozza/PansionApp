package com.nevrozq.pansion.database.users

import FIO
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.mindrot.jbcrypt.BCrypt
import server.DataLength
import server.Moderation
import server.Roles
import server.cut

object Users : Table() {
    val login = Users.varchar("login", 30).uniqueIndex()
    private val password = Users.varchar("password", 70).nullable()
    val name = Users.varchar("name", 30)
    val surname = Users.varchar("surname", 50)
    val praname = Users.varchar("praname", 30).nullable()
    private val birthday = Users.varchar("birthday", 8)
    private val role = Users.varchar("role", 1)
    private val moderation = Users.varchar("moderation", 1)
    private val isParent = Users.bool("isParent")
    val avatarId = Users.integer("avatarId")
    val subjectId = Users.integer("subjectId").nullable()
    private val isActive = Users.bool("isActive")

    fun insert(userDTOs: List<UserDTO>) {
        transaction {
            userDTOs.forEach { userDTO ->
                Users.insert {
                    it[login] = userDTO.login
                    it[password] = userDTO.password
                    it[name] = userDTO.name
                    it[surname] = userDTO.surname
                    it[praname] = userDTO.praname
                    it[birthday] = userDTO.birthday
                    it[role] = userDTO.role
                    it[moderation] = userDTO.moderation
                    it[isParent] = userDTO.isParent
                    it[avatarId] = userDTO.avatarId
                    it[isActive] = userDTO.isActive
                    it[subjectId] = userDTO.subjectId
                }
            }
        }
    }

    fun getLoginWithFIO(fio: FIO): String {
        return transaction {
            val u = Users.select((Users.surname eq fio.surname) and (Users.name eq fio.name) and (Users.praname eq fio.praname)).first()
            u[login]
        }
    }

    fun insert(userDTO: UserDTO) {
        transaction {
            Users.insert {
                it[login] = userDTO.login
                it[password] = userDTO.password
                it[name] = userDTO.name
                it[surname] = userDTO.surname
                it[praname] = userDTO.praname
                it[birthday] = userDTO.birthday
                it[role] = userDTO.role
                it[moderation] = userDTO.moderation
                it[isParent] = userDTO.isParent
                it[avatarId] = userDTO.avatarId
                it[isActive] = userDTO.isActive
                it[subjectId] = userDTO.subjectId
            }
        }
    }

    fun activate(login: String, password: String) {
        try {
            transaction {
                Users.update({ Users.login eq login }) {
                    it[Users.password] = BCrypt.hashpw(password.cut(DataLength.passwordLength), BCrypt.gensalt())
                }
                println(Users.selectAll().map { it[Users.password] })
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun getCount(): Int {
        return try {
            transaction {
                Users.selectAll().count()
            }.toInt()
        } catch (e: Throwable) {
            0
        }
    }

//    fun getRoles(login: String): List<String> {
//        return try {
//            transaction {
//                Users.select { (Users.login eq login) }
//                    .map {
//                        it[role]
//                    }
//            }
//        } catch (e: Exception) {
//            println(e)
//            emptyList()
//        }
//
//    }

    fun getRole(login: String): String {
        return try {
            transaction {
                Users.select { (Users.login eq login) }.first()[role]
            }
        } catch (e: Throwable) {
            println(e)
            Roles.nothing
        }
    }

    fun getModeration(login: String): String {
        return try {
            transaction {
                Users.select { (Users.login eq login) }.first()[moderation]

            }
        } catch (e: Throwable) {
            println(e)
            Moderation.nothing
        }
    }

    fun getIsParentStatus(login: String): Boolean {
        return try {
            transaction {
                Users.select { (Users.login eq login) }.first()[isParent]

            }
        } catch (e: Throwable) {
            println(e)
            false
        }
    }

    fun getIsMember(login: String): Boolean {
        return try {
            transaction {
                Users.select { (Users.login eq login) }.first()
                true
            }
        } catch (e: Throwable) {
            println(e)
            false
        }
    }

    fun update(
        login: String,
        newName: String,
        newSurname: String,
        newPraname: String?,
        newBirthday: String,
        newRole: String,
        newModeration: String,
        newIsParent: Boolean,
        newIsActive: Boolean = true,
        //newSubjectId: Int?
    ) {
        try {
            transaction {
                Users.update({ Users.login eq login }) {
                    it[name] = newName
                    it[surname] = newSurname
                    it[praname] = newPraname
                    it[birthday] = newBirthday
                    it[role] = newRole
                    it[moderation] = newModeration
                    it[isParent] = newIsParent
                    it[isActive] = newIsActive
                   // it[subjectId] = newSubjectId
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun clearPassword(login: String) {
        try {
            transaction {
                Users.update({ Users.login eq login }) {
                    it[password] = null
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun updateAvatarId(login: String, avatarId: Int) {
        try {
            transaction {
                Users.update({ Users.login eq login }) {
                    it[Users.avatarId] = avatarId
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun fetchUser(thisLogin: String): UserDTO? {
        return try {
            transaction {
                Users.select {
                    (login eq thisLogin)
                }.map {
                    UserDTO(
                        login = it[login],
                        password = it[password],
                        name = it[name],
                        surname = it[surname],
                        praname = it[praname],
                        birthday = it[birthday],
                        role = it[role],
                        moderation = it[moderation],
                        isParent = it[isParent],
                        avatarId = it[avatarId],
                        isActive = it[isActive],
                        subjectId = it[subjectId]
                    )
                }.first()
            }
        } catch (e: Throwable) {
            null
        }
    }

    fun fetchByLoginsActivated(logins: List<String>) : List<UserDTO> {
        return transaction {
            Users.select { Users.login inList logins }.mapNotNull {
                if (it[isActive]) UserDTO(
                    login = it[login],
                    password = it[password],
                    name = it[name],
                    surname = it[surname],
                    praname = it[praname],
                    birthday = it[birthday],
                    role = it[role],
                    moderation = it[moderation],
                    isParent = it[isParent],
                    avatarId = it[avatarId],
                    isActive = true,
                    subjectId = it[subjectId]//it[isActive]
                )
                else null
            }
        }
    }

    fun fetchAll(): List<UserDTO> {
        return try {
            transaction {
                Users.selectAll().map {
                    UserDTO(
                        login = it[login],
                        password = it[password],
                        name = it[name],
                        surname = it[surname],
                        praname = it[praname],
                        birthday = it[birthday],
                        role = it[role],
                        moderation = it[moderation],
                        isParent = it[isParent],
                        avatarId = it[avatarId],
                        isActive = it[isActive],
                        subjectId = it[subjectId]
                    )
//                    User(
//                        login = it[login],
//                        isProtected = it[password] != null,
//                        user = UserInit(
//                            fio = FIO(
//                                name = it[name],
//                                surname = it[surname],
//                                praname = it[praname],
//                            ),
//                            birthday = it[birthday],
//                            role = it[role],
//                            moderation = it[moderation],
//                            isParent = it[isParent]
//                        ),
//                        avatarId = it[avatarId],
//                        isActive = it[isActive]
//                    )
                }
            }
        } catch (e: Throwable) {
            listOf()
        }
    }
//    fun fetchStudentsByForm(formId: Int): List<UserDTO> {
//        return try {
//            transaction {
////                Users.select { Users.role eq Roles.student and (Users.classNum eq classNum) } .map {
////                    Student(
////                        login = it[login],
////                        name = it[name],
////                        surname = it[surname],
////                        praname = it[praname],
//////                        isActivated = true
////                    )
////                }
////                listOf() //TODO
//            }
//        } catch (e: Throwable) {
//            listOf()
//        }
//    }

    fun fetchAllTeachers(): List<UserDTO> {
        return try {
            transaction {
                val teachersQuery = Users.select { Users.role eq Roles.teacher }

                teachersQuery.map {

                    UserDTO(
                        login = it[login],
                        password = it[password],
                        name = it[name],
                        surname = it[surname],
                        praname = it[praname],
                        birthday = it[birthday],
                        role = it[role],
                        moderation = it[moderation],
                        isParent = it[isParent],
                        avatarId = it[avatarId],
                        isActive = it[isActive],
                        subjectId = it[subjectId]
                    )
                }
            }

        } catch (e: Throwable) {
            listOf()
        }
    }

    fun fetchAllMentors(): List<UserDTO> {
        return try {
            transaction {
                val mentorsQuery = Users.select {
                    Users.moderation.inList(
                        listOf(
                            Moderation.mentor,
                            Moderation.both,
                            //Moderation.superBoth
                        )
                    )
                }

                mentorsQuery.map {
                    UserDTO(
                        login = it[login],
                        password = it[password],
                        name = it[name],
                        surname = it[surname],
                        praname = it[praname],
                        birthday = it[birthday],
                        role = it[role],
                        moderation = it[moderation],
                        isParent = it[isParent],
                        avatarId = it[avatarId],
                        isActive = it[isActive],
                        subjectId = it[subjectId]
                    )
                }
            }

        } catch (e: Throwable) {
            listOf()
        }
    }

    fun fetchAllStudents(): List<UserDTO> {
        return try {
            transaction {
                val studentQuery = Users.select { Users.role eq Roles.student }

                studentQuery.map {

                    UserDTO(
                        login = it[login],
                        password = it[password],
                        name = it[name],
                        surname = it[surname],
                        praname = it[praname],
                        birthday = it[birthday],
                        role = it[role],
                        moderation = it[moderation],
                        isParent = it[isParent],
                        avatarId = it[avatarId],
                        isActive = it[isActive],
                        subjectId = it[subjectId]
                    )
                }
            }

        } catch (e: Throwable) {
            listOf()
        }
    }

    fun deleteUserByLogin(login: String) {
        try {
            transaction {
                Users.deleteWhere { Users.login eq login }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }
}