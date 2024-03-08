// JS module
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.promise
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.js.Promise

actual suspend fun <T> executeAsync(block: suspend () -> T): T {
    return Promise<T> { resolve, reject ->
        GlobalScope.promise {
            try {
                resolve(block())
            } catch (e: Throwable) {
                reject(e)
            }
        }
    }.await()
}
