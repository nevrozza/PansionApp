package deviceSupport

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

expect val CDispatcher: CoroutineDispatcher

suspend fun <T>  withMain(block: suspend CoroutineScope.() -> T) : T = withContext(Dispatchers.Main, block)
suspend fun <T>  withIO(block: suspend CoroutineScope.() -> T) : T = withContext(CDispatcher, block)

fun CoroutineScope.launchIO(block: suspend CoroutineScope.() -> Unit) = this.launch(CDispatcher, block = block)


