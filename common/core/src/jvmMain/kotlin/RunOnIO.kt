//import kotlinx.coroutines.CoroutineDispatcher
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.IO
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.runBlocking
//
//
//actual suspend fun <T> executeAsync(block: suspend () -> T): T {
//    // For iOS, you can use GlobalScope.launch directly
//    return runBlocking(Dispatchers.IO) {
//        block()
//    }
//}
