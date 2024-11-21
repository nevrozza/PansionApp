import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import server.DeviceTypex
import kotlin.coroutines.CoroutineContext


actual val CDispatcher = Dispatchers.Unconfined

actual val deviceType = DeviceTypex.web
