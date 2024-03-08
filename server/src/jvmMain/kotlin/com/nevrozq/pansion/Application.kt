package com.nevrozq.pansion

import com.nevrozq.pansion.database.defaultGroupsForms.DefaultGroupsForms
import com.nevrozq.pansion.database.forms.Forms
import com.nevrozq.pansion.database.groups.Groups
import com.nevrozq.pansion.database.studentLessons.StudentGroups
import com.nevrozq.pansion.database.studentLessons.StudentLessonsDTO
import com.nevrozq.pansion.database.subjects.GSubjects
import com.nevrozq.pansion.database.tokens.Tokens
import com.nevrozq.pansion.database.userForms.UserForms
import com.nevrozq.pansion.database.users.UserDTO
import com.nevrozq.pansion.database.users.Users
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.Database
import com.nevrozq.pansion.plugins.configureSerialization
import com.nevrozq.pansion.features.auth.activation.configureActivationRouting
import com.nevrozq.pansion.features.auth.login.configureLoginRouting
import com.nevrozq.pansion.features.lessons.configureLessonsRouting
import com.nevrozq.pansion.features.user.manageOld.configureUserManageRouting
import com.nevrozq.pansion.plugins.configureRouting
import com.nevrozq.pansion.features.user.manage.configureRegisterRouting
import com.nevrozq.pansion.utils.createLogin
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import server.Moderation
import server.Roles

// app: учителя,3333
// server: типы уроков(айди, название, какие типы включает другие типы), классы (номер, направление), кабинеты
// уроки +направление, группы +обязательность к классам, +проверка есть ли такой урок в классе
fun main() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/pansionApp", driver = "org.postgresql.Driver",
        user = "postgres", password = "6556"
    )
    transaction {
        SchemaUtils.create(
            Users,
            Tokens,
            GSubjects,
            Groups,
            Forms,
            DefaultGroupsForms,
            StudentGroups,
            UserForms
        )
        StudentGroups.insert(
            StudentLessonsDTO(
                groupId = 123,
                studentLogin = "sad"
            )
        )

//        Users.deleteAll()
//        Tokens.deleteAll()
//        GSubjects.deleteAll()
//        Groups.deleteAll()
//        Forms.deleteAll()
//        DefaultGroupsForms.deleteAll()
//        StudentGroups.deleteAll()
//        UserForms.deleteAll()

//        val login = createLogin("Артём", "Маташков")
//        Users.insert(
//            UserDTO(
//                login = login,
//                password = null,
//                name = "Артём",
//                surname = "Маташков",
//                praname = "Игоревич",
//                birthday = null,
//                role = Roles.teacher,
//                moderation = Moderation.moderator,
//                isParent = false,
//                avatarId = 0
//            )
//        )

    }
//    transaction {
//        Users.deleteAll()
//        Tokens.deleteAll()
//    }
//    println(createLogin("Артём", "Маташков"))

    embeddedServer(Netty, port = 8081, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
    configureRegisterRouting()
    configureActivationRouting()
    configureUserManageRouting()
    configureLoginRouting()
    configureLessonsRouting()
//    configureLessonRouting()
//    configureScheduleRouting()
}
