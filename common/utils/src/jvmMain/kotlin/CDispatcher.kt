import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import server.DeviceTypex
import kotlin.coroutines.CoroutineContext


actual val CDispatcher = Dispatchers.IO

actual val deviceType = DeviceTypex.desktop
