package server


const val delayForNewQRToken : Long = 1000L * 60 * 5

object ExtraSubjectsId {
    const val COMMON = -1
    const val MVD = -2
    const val SOCIAL = -3
    const val CREATIVE = -4
    const val ZDRAV = -5
}

object ScheduleIds {
    const val EXTRA = -6
    const val FOOD = -11
}

val headerTitlesForMinistry = mapOf(
    "0" to "...",
    Ministries.MVD to "МВД",
    Ministries.CULTURE to "Культура",
    Ministries.DRESS_CODE to "Здравоохранение",
    Ministries.EDUCATION to "Образование",
    Ministries.PRINT to "Печать",
    Ministries.SOCIAL to "Соц опрос",
    Ministries.SPORT to "Спорт",
)


object Roles {
    const val NOTHING = "0"
    const val STUDENT = "1"
    const val TEACHER = "2"
}

object Ministries {
    const val MVD = "1"
    const val CULTURE = "2"
    const val SOCIAL = "3"
    const val DRESS_CODE = "4"
    const val EDUCATION = "5"
    const val SPORT = "6"
    const val PRINT = "7"
}

object Moderation {
    const val NOTHING = "0"
    const val MENTOR = "1"
    const val MODERATOR = "2"
    const val BOTH = "3"
}

object DataLength {
    const val PASSWORD_LENGTH = 70
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