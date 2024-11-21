import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import server.DeviceTypex


import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

actual val CDispatcher = Dispatchers.IO

actual val deviceType = DeviceTypex.ios

