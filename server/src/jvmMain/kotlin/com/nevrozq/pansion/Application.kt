package com.nevrozq.pansion

import com.nevrozq.pansion.database.achievements.Achievements
import com.nevrozq.pansion.database.cabinets.Cabinets
import com.nevrozq.pansion.database.calendar.Calendar
import com.nevrozq.pansion.database.checkedNotifications.CheckedNotifications
import com.nevrozq.pansion.database.deviceBinds.DeviceBinds
import com.nevrozq.pansion.database.formGroups.FormGroups
import com.nevrozq.pansion.database.forms.Forms
import com.nevrozq.pansion.database.groups.Groups
import com.nevrozq.pansion.database.homework.HomeTasks
import com.nevrozq.pansion.database.homework.HomeTasksDone
import com.nevrozq.pansion.database.parents.Parents
import com.nevrozq.pansion.database.pickedGIA.PickedGIA
import com.nevrozq.pansion.database.preAttendance.PreAttendance
import com.nevrozq.pansion.database.ratingEntities.Marks
import com.nevrozq.pansion.database.ratingEntities.Stups
import com.nevrozq.pansion.database.ratingTable.RatingModule0Table
import com.nevrozq.pansion.database.ratingTable.RatingModule1Table
import com.nevrozq.pansion.database.ratingTable.RatingModule2Table
import com.nevrozq.pansion.database.ratingTable.RatingWeek0Table
import com.nevrozq.pansion.database.ratingTable.RatingWeek1Table
import com.nevrozq.pansion.database.ratingTable.RatingWeek2Table
import com.nevrozq.pansion.database.ratingTable.RatingYear0Table
import com.nevrozq.pansion.database.ratingTable.RatingYear1Table
import com.nevrozq.pansion.database.ratingTable.RatingYear2Table
import com.nevrozq.pansion.database.ratingTable.updateRatings
import com.nevrozq.pansion.database.reportHeaders.ReportHeaders
import com.nevrozq.pansion.database.schedule.Schedule
import com.nevrozq.pansion.database.studentGroups.StudentGroups
import com.nevrozq.pansion.database.studentLines.StudentLines
import com.nevrozq.pansion.database.subjects.Subjects
import com.nevrozq.pansion.database.tokens.Tokens
import com.nevrozq.pansion.database.studentsInForm.StudentsInForm
import com.nevrozq.pansion.database.users.Users
import com.nevrozq.pansion.features.achievements.configureAchievementsRouting
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.Database
import com.nevrozq.pansion.plugins.configureSerialization
import com.nevrozq.pansion.features.auth.configureActivationRouting
import com.nevrozq.pansion.features.homeworks.configureHomeworksRouting
import com.nevrozq.pansion.features.lessons.configureLessonsRouting
import com.nevrozq.pansion.features.mentoring.configureMentoringRouting
import com.nevrozq.pansion.features.reports.configureReportsRouting
import com.nevrozq.pansion.features.settings.configureSettingsRouting
import com.nevrozq.pansion.plugins.configureRouting
import com.nevrozq.pansion.features.user.manage.configureRegisterRouting
import com.nevrozq.pansion.plugins.configureCORS
import io.ktor.network.tls.certificates.buildKeyStore
import io.ktor.network.tls.certificates.saveToFile
import io.ktor.server.config.ApplicationConfig
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import server.getSixTime
import java.io.File
import kotlin.time.Duration

// app: учителя,3333
// server: типы уроков(айди, название, какие типы включает другие типы), классы (номер, направление), кабинеты
// уроки +направление, группы +обязательность к классам, +проверка есть ли такой урок в классе

var lastTimeRatingUpdate: String = getSixTime()

@OptIn(DelicateCoroutinesApi::class)
fun main() {
//    Database.connect(
//        "jdbc:postgresql://localhost:5432/pansionApp", driver = "org.postgresql.Driver",
//        user = "postgres", password = "6556"
//    )
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
            Stups,
            Cabinets,
            Schedule,
            RatingWeek0Table,
            RatingWeek1Table,
            RatingWeek2Table,
            RatingModule0Table,
            RatingModule1Table,
            RatingModule2Table,
            RatingYear0Table,
            RatingYear1Table,
            RatingYear2Table,
            Calendar,
            HomeTasks,
            HomeTasksDone,
            PreAttendance,
            Achievements,
            CheckedNotifications,
            Parents,
            PickedGIA,
            DeviceBinds
        )

    }
    GlobalScope.launch(Dispatchers.IO) {
        while (true) {
            transaction {
                updateRatings()
            }
            lastTimeRatingUpdate = getSixTime()
            delay(1000 * 60 * 5)
        }
    }
    embeddedServer(
        Netty,
        applicationEnvironment { log = LoggerFactory.getLogger("ktor.application") }, {
            envConfig()
        }, module = Application::module
    )
        .start(wait = true)
}

private fun ApplicationEngine.Configuration.envConfig() {

    val keyStoreFile = File("build/keystore.jks")
    val keyStore = buildKeyStore {
        certificate("sampleAlias") {
            password = "foobar"
            domains = listOf("127.0.0.1", "0.0.0.0", "localhost")
        }
    }
    keyStore.saveToFile(keyStoreFile, "123456")

    connector {
        port = /*8080*/System.getenv("SERVER_PORT").toInt()
    }
    sslConnector(
        keyStore = keyStore,
        keyAlias = "sampleAlias",
        keyStorePassword = { "123456".toCharArray() },
        privateKeyPassword = { "foobar".toCharArray() }) {
        port = 8443
        keyStorePath = keyStoreFile
    }
}

fun Application.module() {
    configureSerialization()
    configureRouting()
    configureCORS()
    configureRegisterRouting()
    configureActivationRouting()
    configureLessonsRouting()
    configureSettingsRouting()
    configureReportsRouting()
    configureHomeworksRouting()
    configureMentoringRouting()
    configureAchievementsRouting()
}
