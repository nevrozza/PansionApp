import kotlinx.coroutines.CoroutineDispatcher

expect suspend fun <T> executeAsync(block: suspend () -> T): T
