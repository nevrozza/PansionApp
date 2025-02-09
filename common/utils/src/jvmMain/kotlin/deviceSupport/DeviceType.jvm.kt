package deviceSupport

private fun getDesktopType(): String {
    val property = System.getProperty("os.name").lowercase()
    return when {
        property.contains("win") -> DeviceTypex.WINDOWS
        property.contains("mac") -> DeviceTypex.MAC
        else -> DeviceTypex.LINUX
    }
}

actual val deviceType = getDesktopType()
