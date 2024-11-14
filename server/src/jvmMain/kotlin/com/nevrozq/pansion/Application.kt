package com.nevrozq.pansion

import AvatarsShop
import com.nevrozq.pansion.database.achievements.Achievements
import com.nevrozq.pansion.database.cabinets.Cabinets
import com.nevrozq.pansion.database.calendar.Calendar
import com.nevrozq.pansion.database.checkedNotifications.CheckedNotifications
import com.nevrozq.pansion.database.deviceBinds.DeviceBinds
import com.nevrozq.pansion.database.duty.Duty
import com.nevrozq.pansion.database.duty.DutyCount
import com.nevrozq.pansion.database.duty.DutySettings
import com.nevrozq.pansion.database.formGroups.FormGroups
import com.nevrozq.pansion.database.forms.Forms
import com.nevrozq.pansion.database.groups.Groups
import com.nevrozq.pansion.database.homework.HomeTasks
import com.nevrozq.pansion.database.homework.HomeTasksDone
import com.nevrozq.pansion.database.pansCoins.PansCoins
import com.nevrozq.pansion.database.parents.Parents
import com.nevrozq.pansion.database.pickedGIA.PickedGIA
import com.nevrozq.pansion.database.preAttendance.PreAttendance
import com.nevrozq.pansion.database.ratingEntities.Marks
import com.nevrozq.pansion.database.ratingEntities.Stups
import com.nevrozq.pansion.database.ratingTable.RatingModule0Table
import com.nevrozq.pansion.database.ratingTable.RatingModule1Table
import com.nevrozq.pansion.database.ratingTable.RatingModule2Table
import com.nevrozq.pansion.database.ratingTable.RatingPreviousWeek0Table
import com.nevrozq.pansion.database.ratingTable.RatingPreviousWeek1Table
import com.nevrozq.pansion.database.ratingTable.RatingPreviousWeek2Table
import com.nevrozq.pansion.database.ratingTable.RatingWeek0Table
import com.nevrozq.pansion.database.ratingTable.RatingWeek1Table
import com.nevrozq.pansion.database.ratingTable.RatingWeek2Table
import com.nevrozq.pansion.database.ratingTable.RatingYear0Table
import com.nevrozq.pansion.database.ratingTable.RatingYear1Table
import com.nevrozq.pansion.database.ratingTable.RatingYear2Table
import com.nevrozq.pansion.database.ratingTable.updateRatings
import com.nevrozq.pansion.database.reportHeaders.ReportHeaders
import com.nevrozq.pansion.database.schedule.Schedule
import com.nevrozq.pansion.database.scheduleConflicts.ScheduleConflicts
import com.nevrozq.pansion.database.secondLogins.SecondLogins
import com.nevrozq.pansion.database.studentGroups.StudentGroups
import com.nevrozq.pansion.database.studentLines.StudentLines
import com.nevrozq.pansion.database.studentMinistry.StudentMinistry
import com.nevrozq.pansion.database.studentsInForm.StudentsInForm
import com.nevrozq.pansion.database.subjects.Subjects
import com.nevrozq.pansion.database.tokens.Tokens
import com.nevrozq.pansion.database.users.Users
import com.nevrozq.pansion.features.achievements.configureAchievementsRouting
import com.nevrozq.pansion.features.auth.configureActivationRouting
import com.nevrozq.pansion.features.homeworks.configureHomeworksRouting
import com.nevrozq.pansion.features.lessons.configureLessonsRouting
import com.nevrozq.pansion.features.mentoring.configureMentoringRouting
import com.nevrozq.pansion.features.reports.configureReportsRouting
import com.nevrozq.pansion.features.school.configureSchoolRouting
import com.nevrozq.pansion.features.settings.configureSettingsRouting
import com.nevrozq.pansion.features.user.manage.configureRegisterRouting
import com.nevrozq.pansion.plugins.configureCORS
import com.nevrozq.pansion.plugins.configureRouting
import com.nevrozq.pansion.plugins.configureSerialization
import io.ktor.server.application.Application
import io.ktor.server.engine.*
import io.ktor.server.netty.Netty
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import server.getSixTime
import java.io.File
import java.io.FileInputStream
import java.security.KeyStore

// app: учителя,3333
// server: типы уроков(айди, название, какие типы включает другие типы), классы (номер, направление), кабинеты
// уроки +направление, группы +обязательность к классам, +проверка есть ли такой урок в классе

var lastTimeRatingUpdate: String = getSixTime()

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    Database.connect(
        sqlUrl, driver = "org.postgresql.Driver",
        user = sqlUser, password = sqlPassword
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
            RatingPreviousWeek0Table,
            RatingPreviousWeek1Table,
            RatingPreviousWeek2Table,
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
            DeviceBinds,
            SecondLogins,
            StudentMinistry,
            Duty,
            DutySettings,
            DutyCount,
            ScheduleConflicts,
            PansCoins,
            AvatarsShop
        )

    }

    GlobalScope.launch {
        while (true) {
            transaction {
                updateRatings()
            }
            lastTimeRatingUpdate = getSixTime()
            delay((1000 * 60 * ratingDelay).toLong())
        }
    }

    embeddedServer(
        factory = Netty,
//        port = h_port,
        environment = applicationEnvironment {
            log = LoggerFactory.getLogger("ktor.application")
        },
        configure = {
            configureSSLConnectors(
                host = "0.0.0.0",
                sslPort = https_port.toString(),
                sslKeyStorePath = "keystore.jks",
                sslPrivateKeyPassword = sslPass,
                sslKeyStorePassword = sslPass,
                sslKeyAlias = sslAlias
            )
        },
        module = Application::module
    )
        .start(wait = true)
}

fun ApplicationEngine.Configuration.configureSSLConnectors(
    host: String,
    sslPort: String,
    sslKeyStorePath: String?,
    sslKeyStorePassword: String?,
    sslPrivateKeyPassword: String?,
    sslKeyAlias: String
) {
    if (sslKeyStorePath == null) {
        throw IllegalArgumentException(
            "SSL requires keystore: use -sslKeyStore=path or ${ConfigKeys.hostSslKeyStore} config"
        )
    }
    if (sslKeyStorePassword == null) {
        throw IllegalArgumentException(
            "SSL requires keystore password: use ${ConfigKeys.hostSslKeyStorePassword} config"
        )
    }
    if (sslPrivateKeyPassword == null) {
        throw IllegalArgumentException(
            "SSL requires certificate password: use ${ConfigKeys.hostSslPrivateKeyPassword} config"
        )
    }

    val keyStoreFile = File(sslKeyStorePath).let { file ->
        if (file.exists() || file.isAbsolute) file else File(".", sslKeyStorePath).absoluteFile
    }
    val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
        FileInputStream(keyStoreFile).use {
            load(it, sslKeyStorePassword.toCharArray())
        }

        requireNotNull(getKey(sslKeyAlias, sslPrivateKeyPassword.toCharArray())) {
            "The specified key $sslKeyAlias doesn't exist in the key store $sslKeyStorePath"
        }
    }

    connector {
        this.port = h_port
    }

    sslConnector(
        keyStore,
        sslKeyAlias,
        { sslKeyStorePassword.toCharArray() },
        { sslPrivateKeyPassword.toCharArray() }
    ) {
        this.host = host
        this.port = sslPort.toInt()
        this.keyStorePath = keyStoreFile
    }
}

fun Application.module() {
    configureSerialization()
    configureRouting()
    configureCORS()
    //configureHttpsRedirect()
    configureRegisterRouting()
    configureActivationRouting()
    configureLessonsRouting()
    configureSettingsRouting()
    configureReportsRouting()
    configureHomeworksRouting()
    configureMentoringRouting()
    configureAchievementsRouting()
    configureSchoolRouting()
}
