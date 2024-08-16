package com.nevrozq.pansion.features.auth

import FIO
import admin.cabinets.RUpdateCabinetsReceive
import admin.groups.Group
import admin.groups.GroupInit
import admin.groups.Subject
import admin.users.UserInit
import applicationVersion
import auth.ActivationReceive
import auth.ActivationResponse
import auth.CheckActivationReceive
import auth.CheckActivationResponse
import auth.Device
import auth.LoginReceive
import auth.LoginResponse
import auth.RChangeAvatarIdReceive
import auth.RCheckConnectionResponse
import auth.RCheckGIASubjectReceive
import auth.RFetchAboutMeReceive
import auth.RFetchAboutMeResponse
import auth.RFetchAllDevicesResponse
import auth.RTerminateDeviceReceive
import com.nevrozq.pansion.database.cabinets.Cabinets
import com.nevrozq.pansion.database.cabinets.CabinetsDTO
import com.nevrozq.pansion.database.formGroups.FormGroups
import com.nevrozq.pansion.database.forms.Forms
import com.nevrozq.pansion.database.forms.mapToForm
import com.nevrozq.pansion.database.homework.HomeTasksDone
import com.nevrozq.pansion.database.pickedGIA.PickedGIA
import com.nevrozq.pansion.database.pickedGIA.PickedGIADTO
import com.nevrozq.pansion.database.studentGroups.StudentGroups
import com.nevrozq.pansion.database.studentLines.StudentLines
import com.nevrozq.pansion.database.studentsInForm.StudentsInForm
import com.nevrozq.pansion.database.subjects.Subjects
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.exceptions.ExposedSQLException
import com.nevrozq.pansion.database.tokens.TokenDTO
import com.nevrozq.pansion.database.tokens.Tokens
import com.nevrozq.pansion.database.users.Users
import com.nevrozq.pansion.utils.isMember
import com.nevrozq.pansion.utils.isModer
import com.nevrozq.pansion.utils.login
import com.nevrozq.pansion.utils.nullUUID
import com.nevrozq.pansion.utils.toId
import com.nevrozq.pansion.utils.token
import homework.RCheckHomeTaskReceive
import server.DataLength
import server.Moderation
import server.Roles
import server.cut
import java.util.HashMap
import java.util.UUID

class AuthController {


    suspend fun fetchAboutMe(call: ApplicationCall) {
        if (call.isMember) {
            val r = call.receive<RFetchAboutMeReceive>()
            try {
                val form = Forms.fetchById(StudentsInForm.fetchFormIdOfLogin(r.studentLogin))
                val groups =
                    StudentGroups.fetchGroupsOfStudent(r.studentLogin).filter { it.isActive }
                val subjects =
                    Subjects.fetchAllSubjects().filter { it.id in groups.map { it.subjectId } }
                        .filter { it.isActive }
                val teachers = ( Users.fetchAllTeachers()
                    .filter { it.isActive && it.login in groups.map { it.teacherLogin } } + Users.fetchAllMentors().firstOrNull { it.login == form.mentorLogin }).filterNotNull()
                var likes = 0
                var dislikes = 0

                StudentLines.fetchStudentLinesByLogin(r.studentLogin).forEach {
                    if(it.isLiked == "t") {
                        likes++
                    } else if (it.isLiked == "f") {
                        dislikes++
                    }
                }


                call.respond(
                    RFetchAboutMeResponse(
                        form = form.mapToForm(),
                        groups = groups.map {
                            Group(
                                id = it.id,
                                group = GroupInit(
                                    name = it.name,
                                    teacherLogin = it.teacherLogin,
                                    subjectId = it.subjectId,
                                    difficult = it.difficult
                                ),
                                isActive = it.isActive
                            )
                        },
                        subjects = subjects.map {
                            Subject(
                                id = it.id,
                                name = it.name,
                                isActive = it.isActive
                            )
                        },
                        teachers = HashMap(teachers.associateBy({
                            it.login
                        }, { "${it.surname} ${it.name[0]}. ${(it.praname ?: " ")[0]}." }
                        )),
                        likes = likes,
                        dislikes = dislikes,
                        giaSubjects = PickedGIA.fetchByStudent(r.studentLogin)
                    ))
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Conflict when get about me")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch about me: ${e.localizedMessage}"
                )
            }
        }
    }

    suspend fun terminateDevice(call: ApplicationCall) {
        if (call.isMember) {
            try {
                val r = call.receive<RTerminateDeviceReceive>()
                Tokens.deleteTokenByIdAndLogin(id = r.id.toId(), login = call.login)
                call.respond(HttpStatusCode.OK)
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Idk ERROR Terminate")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't Terminate: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(
                HttpStatusCode.Forbidden
            )
        }
    }


    suspend fun fetchAllDevices(call: ApplicationCall) {
        if (call.isMember) {
            try {
                val devices = Tokens.getTokensOfThisLogin(thisLogin = call.login).map {
                    Device(
                        deviceId = it.deviceId.toString(),
                        deviceName = it.deviceName,
                        deviceType = it.deviceType,
                        time = it.time,
                        isThisSession = it.token.toString() == call.token
                    )
                }
                call.respond(RFetchAllDevicesResponse(devices))
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Idk ERROR FETCH DEVICES")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch devices: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(
                HttpStatusCode.Forbidden
            )
        }
    }

    suspend fun updateAvatarId(call: ApplicationCall) {
        if (call.isMember) {
            val r = call.receive<RChangeAvatarIdReceive>()
            try {
                Users.updateAvatarId(
                    login = call.login,
                    avatarId = r.avatarId
                )
                call.respond(HttpStatusCode.OK)
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "Idk ERROR WHEN CHANGE AVATAR ID CONFLICT!!")
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't change avatarId: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(
                HttpStatusCode.Forbidden
            )
        }
    }

    suspend fun checkConnection(call: ApplicationCall) {
        //val isTokenValid: Boolean,
        //    val name: String,
        //    val surname: String,
        //    val praname: String,
        //    val role: String,
        //    val moderation: String,
        //    val avatarId: Int
        val isTokenValid: Boolean = Tokens.isTokenValid(call.token.toId())
        var name: String = ""
        var surname: String = ""
        var praname: String? = ""
        var role: String = ""
        var moderation: String = ""
        var avatarId: Int = 0
        var isParent: Boolean = false
        var birthday: String = ""

        if (isTokenValid) {
            val user = Users.fetchUser(call.login)!!
            name = user.name
            surname = user.surname
            praname = user.praname
            role = user.role
            moderation = user.moderation
            avatarId = user.avatarId
            isParent = user.isParent
            birthday = user.birthday
        }
        call.respond(
            RCheckConnectionResponse(
                isTokenValid = isTokenValid,
                name = name,
                surname = surname,
                praname = praname,
                role = role,
                moderation = moderation,
                avatarId = avatarId,
                isParent = isParent,
                birthday = birthday,
                version = applicationVersion
            )
        )
    }

    suspend fun checkGIASubject(call: ApplicationCall) {
        val r = call.receive<RCheckGIASubjectReceive>()
        if (call.isMember && r.login == call.login) {
            try {
                val dto = PickedGIADTO(
                    studentLogin = r.login,
                    subjectGIAId = r.subjectId
                )
                if(r.isChecked) {
                    PickedGIA.insert(dto)
                } else {
                    PickedGIA.delete(dto)
                }
                call.respond(
                    HttpStatusCode.OK
                )
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't check GIA: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }


    suspend fun activateUser(call: ApplicationCall) {
        val authReceive = call.receive<ActivationReceive>()

        val loginUser = Users.fetchUser(authReceive.login)
        if (authReceive.deviceId.toId() != nullUUID) {
            if (loginUser != null) {
                if (loginUser.password != null) {
                    call.respond(HttpStatusCode.Conflict, "admin.users.User already authorized")
                } else {
                    val token = UUID.randomUUID()

                    try {
                        Users.activate(
                            authReceive.login,
                            authReceive.password
                        )
                    } catch (e: ExposedSQLException) {
                        call.respond(HttpStatusCode.Conflict, "admin.users.User already exists")
                    } catch (e: Throwable) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            "Can't create user ${e.localizedMessage}"
                        )
                    }

                    Tokens.insert(
                        TokenDTO(
                            deviceId = authReceive.deviceId.toId(),
                            login = authReceive.login,
                            token = token,
                            deviceName = authReceive.deviceName,
                            deviceType = authReceive.deviceType,
                            time = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+3"))
                                .toString()
                                .cut(16)
                        )
                    )

                    call.respond(
                        ActivationResponse(
                            token = token.toString(),
                            user = UserInit(
                                fio = FIO(
                                    name = loginUser.name,
                                    surname = loginUser.surname,
                                    praname = loginUser.praname
                                ),
                                role = loginUser.role,
                                moderation = loginUser.moderation,
                                birthday = loginUser.birthday,
                                isParent = loginUser.isParent
                            ),
                            login = loginUser.login
                        )
                    )

                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "No admin.users.User found")
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "NullableUID")
        }


    }

    suspend fun checkUserActivation(call: ApplicationCall) {
        val authReceive = call.receive<CheckActivationReceive>()

        val loginUser = Users.fetchUser(authReceive.login)

        if (loginUser != null) {
            if (loginUser.password != null) {
                call.respond(CheckActivationResponse(loginUser.name, true))
            } else {
                call.respond(CheckActivationResponse(loginUser.name, false))

            }
        } else {
            call.respond(CheckActivationResponse(null, false))
        }

    }

    suspend fun performLogin(call: ApplicationCall) {
        val receive = call.receive<LoginReceive>()
        val userDTO = Users.fetchUser(receive.login)

        if (userDTO == null) {
//            call.respond(HttpStatusCode.BadRequest, "admin.users.User not found")
            call.respond(
                errorLogin("user")
            )
        } else {
            if (receive.deviceId.toId() != nullUUID) {
                when (userDTO.password ?: "".cut(DataLength.passwordLength)) {
                    receive.password -> {
                        val token = UUID.randomUUID()
                        Tokens.insert(
                            TokenDTO(
                                deviceId = receive.deviceId.toId(),
                                login = userDTO.login,
                                token = token,
                                deviceName = receive.deviceName,
                                deviceType = receive.deviceType,
                                time = Clock.System.now()
                                    .toLocalDateTime(TimeZone.of("UTC+3")).toString()
                                    .cut(16)
                            )
                        )
                        //if (userDTO.isActive) {
                            call.respond(
                                LoginResponse(
                                    activation = ActivationResponse(
                                        token = token.toString(),
                                        user = UserInit(
                                            fio = FIO(
                                                name = userDTO.name,
                                                surname = userDTO.surname,
                                                praname = userDTO.praname
                                            ),
                                            birthday = userDTO.birthday,
                                            role = userDTO.role,
                                            moderation = userDTO.moderation,
                                            isParent = userDTO.isParent
                                        ),
                                        login = userDTO.login
                                    ),
                                    avatarId = userDTO.avatarId
                                )
                            )
                        //} else {
//                            call.respond(
//                                HttpStatusCode.Forbidden,
//                                "Your account has been deactivated"
//                            )
                        //    call.respond(
                        //        errorLogin("deactivated")
                        //    )
                        //}
                    }

//                    null -> {
//                        call.respond(
//                            HttpStatusCode.Unauthorized,
//                            "admin.users.User wasn't authorized"
//                        )
//                    }

                    else -> {
//                        call.respond(HttpStatusCode.BadRequest, "Invalid password")
                        call.respond(
                            errorLogin("password")
                        )
                    }
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "NullableUUID")
            }
        }
    }
}

private fun errorLogin(reason: String): LoginResponse {
    return LoginResponse(
        activation = ActivationResponse(
            token = reason,
            user = UserInit(
                fio = FIO(
                    name = "",
                    surname = "",
                    praname = ""
                ),
                birthday = "22012008",
                role = Roles.nothing,
                moderation = Moderation.nothing,
                isParent = false
            ),
            login = ""
        ),
        avatarId = 0
    )
}