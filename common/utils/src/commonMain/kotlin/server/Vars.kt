package server


const val delayForNewQRToken = 10000000000

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
    const val passwordLength = 50
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