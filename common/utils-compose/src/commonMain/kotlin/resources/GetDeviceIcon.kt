import resources.RIcons
import server.DeviceTypex

fun getDeviceIcon(deviceType: String, deviceName: String): String {
    return when (deviceType) {
        DeviceTypex.desktop -> when {
            deviceName.contains("windows", true) -> RIcons.Devices.Desktop.windows
            deviceName.contains("mac", true)
                || deviceName.contains("air", true)
                || deviceName.contains("pro", true) -> RIcons.Devices.Desktop.laptop

            else -> RIcons.Devices.Desktop.computer
        }

        DeviceTypex.android -> RIcons.Devices.Mobile.Android
        DeviceTypex.ios -> RIcons.Devices.Mobile.Apple
        DeviceTypex.web -> with(RIcons.Devices.Web) {
            when {
                deviceName.contains("edge", true) -> Edge
                deviceName.contains("chrome", true) -> Chrome
                deviceName.contains("safari", true) -> Safari
                deviceName.contains("yandex", true) -> Yandex
                deviceName.contains("opera", true) -> Opera
                deviceName.contains("firefox", true) -> Firefox
                else -> Web
            }
        }

        else -> RIcons.QuestionCircle

    }
}