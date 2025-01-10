package server

import kotlinx.datetime.TimeZone


const val delayForNewQRToken : Long = 1000 * 60 * 5

val appTimeZone = TimeZone.of("UTC+3")

object ExtraSubjectsId {
    const val common = -1
    const val mvd = -2
    const val social = -3
    const val creative = -4
}

object ScheduleIds {
    const val extra = -6
    const val food = -11
}

val headerTitlesForMinistry = mapOf(
    "0" to "...",
    Ministries.MVD to "МВД",
    Ministries.Culture to "Культура",
    Ministries.DressCode to "Здравоохранение",
    Ministries.Education to "Образование",
    Ministries.Print to "Печать",
    Ministries.Social to "Соц опрос",
    Ministries.Sport to "Спорт",
)

object DeviceTypex {
    const val android = "Android"
    const val ios = "IOS"
    const val web = "WEB"
    const val desktop = "Desktop"
}

object Roles {

    const val nothing = "0"
    const val student = "1"
    const val teacher = "2"
}

object Ministries {
    const val MVD = "1"
    const val Culture = "2"
    const val Social = "3"
    const val DressCode = "4"
    const val Education = "5"
    const val Sport = "6"
    const val Print = "7"
}

object Moderation {
    const val nothing = "0"
    const val mentor = "1"
    const val moderator = "2"
    const val both = "3"
//    const val superModerator = "4"
//    const val superBoth = "5"
}

object IsParentStatus {

    const val no = "0"
    const val yes = "1"
}

object DataLength {
    const val passwordLength = 70
}


val weekPairs = hashMapOf(
    1 to "Пн",
    2 to "Вт",
    3 to "Ср",
    4 to "Чт",
    5 to "Пт",
    6 to "Сб",
    7 to "Вс",
)