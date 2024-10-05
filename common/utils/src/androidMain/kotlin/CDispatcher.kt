import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import server.DeviceTypex

actual val CDispatcher = Dispatchers.IO

actual val deviceType = DeviceTypex.android