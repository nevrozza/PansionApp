package deviceSupport

fun initIsLockedVerticalView(currentLocked: Boolean?, deviceName: String, onSuccess: (Boolean) -> Unit) {
    if (currentLocked == null) {
        val isMobile = deviceName.contains("iPhone", ignoreCase = true) || deviceName.contains("Android", ignoreCase = true)
        onSuccess(isMobile)
    }
}


fun getWebDeviceName(userAgent: String, addition: String): String {
    var deviceName = when {
        userAgent.contains("iPhone", ignoreCase = true) -> "iPhone"
        userAgent.contains("Samsung", ignoreCase = true) -> "Samsung"
        userAgent.contains("Ubuntu", ignoreCase = true) -> "Ubuntu"
        userAgent.contains("Fedora", ignoreCase = true) -> "Fedora"
        userAgent.contains("iPad", ignoreCase = true) -> "iPad"
        userAgent.contains("Android", ignoreCase = true) -> "Android"
        userAgent.contains("Windows", ignoreCase = true) -> "Windows"
        userAgent.contains("Macintosh", ignoreCase = true) -> "MacOS"
        userAgent.contains("Linux", ignoreCase = true) -> "Linux"
        else -> "Устройство"
    }
    deviceName = when {
        userAgent.contains("OPR", ignoreCase = true) -> "Opera "
        userAgent.contains("Edg", ignoreCase = true) -> "Edge "
        userAgent.contains("Firefox", ignoreCase = true) -> "Firefox "

        userAgent.contains("EdgiOS", ignoreCase = true) -> "Edge "
        userAgent.contains("FxiOS", ignoreCase = true) -> "Firefox "
        userAgent.contains("CriOS", ignoreCase = true) -> "Chrome "
        userAgent.contains("Chrome", ignoreCase = true) -> "Chrome "
        userAgent.contains("Safari", ignoreCase = true) -> "Safari "
        userAgent.contains("YaBrowser", ignoreCase = true) -> "Yandex "
        else -> ""
    } + deviceName
    return "$deviceName $addition"
}