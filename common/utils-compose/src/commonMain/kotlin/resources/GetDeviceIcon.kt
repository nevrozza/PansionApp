
import deviceSupport.DeviceTypex
import resources.RIcons

fun getDeviceIcon(deviceType: String, deviceName: String): String {
    return when (deviceType) {
        DeviceTypex.MAC -> RIcons.Devices.Desktop.LAPTOP
        DeviceTypex.WINDOWS -> RIcons.Devices.Desktop.WINDOWS
        DeviceTypex.LINUX -> RIcons.Devices.Desktop.COMPUTER
        "Desktop" ->  RIcons.Devices.Desktop.COMPUTER
        DeviceTypex.ANDROID -> RIcons.Devices.Mobile.ANDROID
        DeviceTypex.IOS -> RIcons.Devices.Mobile.APPLE
        "IOS" -> RIcons.Devices.Mobile.APPLE
        DeviceTypex.WEB -> with(RIcons.Devices.Web) {
            when {
                deviceName.contains("edge", true) -> EDGE
                deviceName.contains("chrome", true) -> CHROME
                deviceName.contains("safari", true) -> SAFARI
                deviceName.contains("yandex", true) -> YANDEX
                deviceName.contains("opera", true) -> OPERA
                deviceName.contains("firefox", true) -> FIREFOX
                else -> WEB
            }
        }

        else -> RIcons.QUESTION_CIRCLE

    }
}