package Flow

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.coroutineContext
import kotlinx.coroutines.flow.flow

suspend fun main() {
    flowContextExample1()
}

suspend fun getSmallFlow() = flow {
    for (i in 1..10) {
        emit(i)
        println(currentCoroutineContext())
        delay(500)
    }
}

/** Flow примет контекст того места откуда был вызван collect*/
suspend fun flowContextExample1() = coroutineScope {
    withContext(CoroutineName("Caller method context") + Dispatchers.Default) {
        getSmallFlow().collect {
            println(it)
        }
    }
}