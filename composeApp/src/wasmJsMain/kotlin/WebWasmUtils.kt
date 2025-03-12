



import com.benasher44.uuid.uuid4
import deviceSupport.getWebDeviceName
import kotlinx.browser.window
import kotlin.js.Promise

external class SizeManager {
    fun getChanges(): Promise<JsAny?>
    fun resize()
}

external interface Size {
    val width: Int
    val height: Int
}

fun getDeviceName(): String = getWebDeviceName(window.navigator.userAgent, "WASM")

fun getOrCreateDeviceUUID(): String {
    val storedUUID = kotlinx.browser.localStorage.getItem("deviceUUID")
    return if (storedUUID != null) {
        storedUUID
    } else {
        val newUUID = uuid4().toString()
        kotlinx.browser.localStorage.setItem("deviceUUID", newUUID)
        newUUID
    }
}
