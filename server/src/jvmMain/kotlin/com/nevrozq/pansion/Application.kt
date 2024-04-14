package com.nevrozq.pansion

import com.nevrozq.pansion.database.formGroups.FormGroups
import com.nevrozq.pansion.database.forms.Forms
import com.nevrozq.pansion.database.groups.Groups
import com.nevrozq.pansion.database.ratingEntities.Marks
import com.nevrozq.pansion.database.ratingEntities.Stups
import com.nevrozq.pansion.database.reportHeaders.ReportHeaders
import com.nevrozq.pansion.database.studentGroups.StudentGroups
import com.nevrozq.pansion.database.studentLines.StudentLines
import com.nevrozq.pansion.database.subjects.Subjects
import com.nevrozq.pansion.database.tokens.Tokens
import com.nevrozq.pansion.database.studentsInForm.StudentsInForm
import com.nevrozq.pansion.database.users.UserDTO
import com.nevrozq.pansion.database.users.Users
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.Database
import com.nevrozq.pansion.plugins.configureSerialization
import com.nevrozq.pansion.features.auth.configureActivationRouting
import com.nevrozq.pansion.features.lessons.configureLessonsRouting
import com.nevrozq.pansion.features.reports.configureReportsRouting
import com.nevrozq.pansion.features.settings.configureSettingsRouting
import com.nevrozq.pansion.features.user.manageOld.configureUserManageRouting
import com.nevrozq.pansion.plugins.configureRouting
import com.nevrozq.pansion.features.user.manage.configureRegisterRouting
import com.nevrozq.pansion.plugins.configureCORS
import com.nevrozq.pansion.utils.createLogin
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import report.RUpdateReportReceive
import server.Moderation
import server.Roles

// app: учителя,3333
// server: типы уроков(айди, название, какие типы включает другие типы), классы (номер, направление), кабинеты
// уроки +направление, группы +обязательность к классам, +проверка есть ли такой урок в классе
fun main() {
    Database.connect(
        url = System.getenv("DATABASE_CONNECTION_STRING"),
        driver = "org.postgresql.Driver",
        user = System.getenv("POSTGRES_USER"),
        password = System.getenv("POSTGRES_PASSWORD")
    )
    transaction {
        SchemaUtils.create(
            Users,
            Tokens,
            Subjects,
            Groups,
            Forms,
            FormGroups,
            StudentGroups,
            StudentsInForm,
            StudentLines,
            ReportHeaders,
            Marks,
            Stups
        )

//        Users.deleteAll()
//        Tokens.deleteAll()
//        Subjects.deleteAll()
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
//                birthday = "15111978",
//                role = Roles.teacher,
//                moderation = Moderation.moderator,
//                isParent = false,
//                avatarId = 0,
//                isActive = true
//            )
//        )

//        println(login)
    }
//    transaction {
//        Users.deleteAll()
//        Tokens.deleteAll()
//    }

    embeddedServer(Netty,
        port = System.getenv("SERVER_PORT").toInt(),
        module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
    configureCORS()
    configureRegisterRouting()
    configureActivationRouting()
    configureUserManageRouting()
    configureLessonsRouting()
    configureSettingsRouting()
    configureReportsRouting()
//    configureLessonRouting()
//    configureScheduleRouting()
}
