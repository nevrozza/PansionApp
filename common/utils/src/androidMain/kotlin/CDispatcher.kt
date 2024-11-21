
import kotlinx.coroutines.Dispatchers
import server.DeviceTypex
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking


actual val CDispatcher = Dispatchers.IO

actual val deviceType = DeviceTypex.android
