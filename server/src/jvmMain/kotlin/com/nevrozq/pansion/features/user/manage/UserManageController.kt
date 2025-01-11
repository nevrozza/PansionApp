package com.nevrozq.pansion.features.user.manage

import FIO
import PersonParent
import admin.groups.forms.CutedForm
import admin.parents.RFetchParentsListResponse
import admin.parents.RUpdateParentsListReceive
import admin.users.RClearUserPasswordReceive
import admin.users.RCreateExcelStudentsReceive
import admin.users.RCreateUserResponse
import admin.users.RDeleteUserReceive
import admin.users.REditUserReceive
import admin.users.RFetchAllUsersResponse
import admin.users.RRegisterUserReceive
import com.nevrozq.pansion.database.forms.Forms
import com.nevrozq.pansion.database.parents.Parents
import com.nevrozq.pansion.database.parents.ParentsDTO
import com.nevrozq.pansion.database.studentsInForm.StudentInFormDTO
import com.nevrozq.pansion.database.studentsInForm.StudentsInForm
import com.nevrozq.pansion.database.subjects.Subjects
import com.nevrozq.pansion.database.tokens.FetchTokensResponse
import com.nevrozq.pansion.database.tokens.Tokens
import com.nevrozq.pansion.database.tokens.toFetchTokensResponse
import com.nevrozq.pansion.database.users.UserDTO
import com.nevrozq.pansion.database.users.Users
import com.nevrozq.pansion.database.users.mapToUser
import com.nevrozq.pansion.features.settings.DeleteTokenReceive
import com.nevrozq.pansion.utils.*
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import main.RFetchChildrenResponse
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.transactions.transaction
import server.Moderation
import server.Roles

class UserManageController() {

    suspend fun fetchAllParents(call: ApplicationCall) {
        val perm = call.isModer
        call.dRes(perm, "Can't fetch all parentsLines") {
            val users = Users.fetchAll().map {
                PersonParent(
                    login = it.login,
                    fio = FIO(
                        name = it.name,
                        surname = it.surname,
                        praname = it.praname
                    ),
                    isActive = it.isActive,
                    isParent = it.isParent,
                    isStudent = it.role == Roles.student,
                    formId = StudentsInForm.fetchFormIdOfLoginNullable(it.login)
                )
            }.sortedBy { it.fio.surname }.sortedBy { !it.isActive }

            val lines = Parents.fetchAll()

            call.respond(
                RFetchParentsListResponse(
                    users = users,
                    lines = lines,
                    forms = Forms.getAllForms().filter {it.isActive}.map { CutedForm(id = it.formId, title = it.title, classNum = it.classNum) }
                )
            ).done
        }
    }

    suspend fun updateParents(call: ApplicationCall) {
        val perm = call.isModer
        call.dRes(perm, "Can't update parentsLines") {
            val r = call.receive<RUpdateParentsListReceive>()
            if (r.parentLogin == "0") {
                if (r.id != 0) {
                    Parents.delete(r.id)
                }
            } else if (r.studentLogin != "") {
                Parents.insert(
                    ParentsDTO(
                        id = 0,
                        studentLogin = r.studentLogin,
                        parentLogin = r.parentLogin
                    )
                )
            } else if (r.id != 0) {
                Parents.update(
                    id = r.id,
                    parentLogin = r.parentLogin
                )
            }
            fetchAllParents(call).done
        }
    }

    suspend fun createUser(call: ApplicationCall) {
        val perm = call.isModer
        call.dRes(perm, "Can't create user") {

            val r = call.receive<RRegisterUserReceive>()
            val login = createLogin(r.userInit.fio.name, r.userInit.fio.surname)
            var pLogins: MutableList<String>? = null
            Users.insert(
                UserDTO(
                    login = login,
                    password = null,
                    name = r.userInit.fio.name,
                    surname = r.userInit.fio.surname,
                    praname = r.userInit.fio.praname,
                    birthday = r.userInit.birthday,
                    role = r.userInit.role,
                    moderation = r.userInit.moderation,
                    isParent = r.userInit.isParent,
                    avatarId = 0,
                    isActive = true,
                    subjectId = r.subjectId
                )
            )

            if (r.parentFIOs != null) {
                pLogins = mutableListOf()
                r.parentFIOs!!.forEach { p ->
                    val fio = p.split(" ")
                    val pLogin = createLogin(fio[1], fio[0])
                    Users.insert(
                        UserDTO(
                            login = pLogin,
                            password = null,
                            name = fio[1],
                            surname = fio[0],
                            praname = fio.getOrNull(2),
                            birthday = "01012000",
                            role = Roles.nothing,
                            moderation = Moderation.nothing,
                            isParent = true,
                            avatarId = 0,
                            isActive = true,
                            subjectId = null
                        )
                    )

                    Parents.insert(
                        ParentsDTO(
                            id = 0,
                            studentLogin = login,
                            parentLogin = pLogin
                        )
                    )
                    pLogins.add(pLogin)
                }
            }
            if (r.formId != 0) {
                StudentsInForm.insert(
                    StudentInFormDTO(
                        formId = r.formId,
                        login = login
                    )
                )
            }

            call.respond(RCreateUserResponse(login, pLogins)).done
        }
    }

    suspend fun createExcelStudents(call: ApplicationCall) {
        val perm = call.isModer
        call.dRes(perm, "Can't create excel users") {

            val r = call.receive<RCreateExcelStudentsReceive>()
            transaction {
                //                    println("STUDENTS: ${r.students}")
                r.students.forEach { l ->
                    val studentPreviousLogin = Users.getLoginWithFIO(
                        fio = FIO(
                            name = l.user.fio.name.replace("ë", "ё"),
                            surname = l.user.fio.surname.replace("ë", "ё"),
                            praname = (l.user.fio.praname ?: "").replace("ë", "ё")
                        ),
                        itShouldBeStudent = true
                    )
                    var student: UserDTO? = null
                    var login: String? = null
                    if (studentPreviousLogin == null || Users.fetchUser(studentPreviousLogin)?.birthday != l.user.birthday) {

                        login =
                            createLogin(name = l.user.fio.name, surname = l.user.fio.surname)
                        student = UserDTO(
                            login = login,
                            password = null,
                            name = l.user.fio.name.replace("ë", "ё"),
                            surname = l.user.fio.surname.replace("ë", "ё"),
                            praname = l.user.fio.praname?.replace("ë", "ё"),
                            birthday = l.user.birthday,
                            role = l.user.role,
                            moderation = l.user.moderation,
                            isParent = l.user.isParent,
                            avatarId = 1,
                            isActive = true,
                            subjectId = null
                        )
                    }
                    l.parents.forEach { pp ->
                        val p = pp.first.split(" ")
                        val pFio = FIO(
                            name = p[1].replace("ë", "ё").trim(),
                            surname = p[0].replace("ë", "ё").trim(),
                            praname = ((p.getOrNull(2)
                                ?: "") + (if (p.getOrNull(3) != null) " " + p.getOrNull(3) else "")).replace(
                                "ë",
                                "ё"
                            ).trim()
                        )

                        val parentPreviousLogin = Users.getLoginWithFIO(
                            fio = FIO(
                                name = pFio.name.replace("ë", "ё").trim(),
                                surname = pFio.surname.replace("ë", "ё").trim(),
                                praname = (pFio.praname?.replace("ë", "ё"))?.trim()?.replace("ë", "ё")
                            ),
                            itShouldBeStudent = false
                        )
                        var pLogin: String? = null
                        if (parentPreviousLogin == null || Users.fetchUser(parentPreviousLogin)?.birthday != pp.second.replace(
                                ".",
                                ""
                            )
                        ) {
                            pLogin = createLogin(pFio.surname, 1)
                            val parent = UserDTO(
                                login = pLogin,
                                password = null,
                                name = pFio.name.replace("ë", "ё"),
                                surname = pFio.surname.replace("ë", "ё"),
                                praname = pFio.praname?.replace("ë", "ё"),
                                birthday = pp.second.replace(".", ""),
                                role = Roles.nothing,
                                moderation = Moderation.nothing,
                                isParent = true,
                                avatarId = 1,
                                isActive = true,
                                subjectId = null
                            )

                            Users.insert(
                                listOf(student, parent)
                                    .mapNotNull { it }
                            )
                        }

                        val children = Parents.fetchChildren(pLogin ?: parentPreviousLogin!!)
                        if ((login ?: studentPreviousLogin!!) !in children.map { it.login }) {
                            Parents.insert(
                                ParentsDTO(
                                    id = 0,
                                    studentLogin = login ?: studentPreviousLogin!!,
                                    parentLogin = pLogin ?: parentPreviousLogin!!
                                )
                            )
                        }
                    }
                    if (l.formId != 0 && login != null) {
                        StudentsInForm.insert(
                            StudentInFormDTO(
                                formId = l.formId,
                                login = login
                            )
                        )
                    }
                }
            }

            call.respond(HttpStatusCode.OK).done
        }
    }

    suspend fun fetchAllUsers(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch all users") {

            val users = Users.fetchAll()
            val forms = Forms.getAllForms().mapNotNull {
                if (it.isActive) {
                    CutedForm(
                        id = it.formId,
                        title = it.title,
                        classNum = it.classNum
                    )
                } else null
            }
            val subjects = Subjects.fetchAllActiveSubjectsAsMap()
            call.respond(
                RFetchAllUsersResponse(
                    users.map { it.mapToUser() }.sortedBy { it.user.fio.surname },
                    forms,
                    subjects = subjects
                )
            ).done
        }
    }

    suspend fun fetchChildren(call: ApplicationCall) {
        val perm = call.isParent
        call.dRes(perm, "Can't fetch children") {
            val children = Parents.fetchChildren(call.login)
            call.respond(RFetchChildrenResponse(children)).done
        }
    }

    suspend fun performSearchTokens(call: ApplicationCall) {
        val token = call.request.headers["Bearer-Authorization"].toId()
        if (Tokens.isTokenValid(token)) {
            val login = Tokens.getLoginOfThisToken(token)
            call.respond(
                FetchTokensResponse(
                    Tokens.getTokensOfThisLogin(login).map { it.toFetchTokensResponse() }
                )
            )
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Token expired")
        }
    }

    suspend fun deleteToken(call: ApplicationCall) {
        val token = call.request.headers["Bearer-Authorization"].toId()

        val deleteTokenReceive = call.receive<DeleteTokenReceive>()
        if (Tokens.isTokenValid(token)) {
            val login = Tokens.getLoginOfThisToken(token)
            if (Tokens.getTokensOfThisLogin(login).any { it.deviceId == deleteTokenReceive.id }) {
                Tokens.deleteTokenByIdAndLogin(deleteTokenReceive.id, login)
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid id")
            }
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Token expired")
        }
    }

    suspend fun performEditUser(call: ApplicationCall) {
        val perm = call.isModer

        call.dRes(perm, "Can't edit user") {
            val r = call.receive<REditUserReceive>()
            Users.update(
                login = r.login,
                newName = r.user.fio.name,
                newSurname = r.user.fio.surname,
                newPraname = r.user.fio.praname,
                newBirthday = r.user.birthday,
                newRole = r.user.role,
                newModeration = r.user.moderation,
                newIsParent = r.user.isParent,
                newSubjectId = r.subjectId
            )
            call.respond(HttpStatusCode.OK).done
        }
    }

    suspend fun performDeleteUser(call: ApplicationCall) {
        val perm = call.isModer
        call.dRes(perm, "Can't delete user") {

            val r = call.receive<RDeleteUserReceive>()
            Users.update(
                login = r.login,
                newName = r.user.fio.name,
                newSurname = r.user.fio.surname,
                newPraname = r.user.fio.praname,
                newBirthday = r.user.birthday,
                newRole = r.user.role,
                newModeration = r.user.moderation,
                newIsParent = r.user.isParent,
                newIsActive = false,
                newSubjectId = null
            )
            call.respond(HttpStatusCode.OK).done
        }
        if (call.isModer) {
            try {
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "SQL Conflict")
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.BadRequest, "Can't delete user: ${e.localizedMessage}")
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun clearUserPassword(call: ApplicationCall) {
        val perm = call.isModer
        call.dRes(perm, "Can't clear password") {
            val r = call.receive<RClearUserPasswordReceive>()

            Users.clearPassword(r.login)
            Tokens.deleteTokenByLogin(r.login)
            call.respond(HttpStatusCode.OK).done
        }
    }
}