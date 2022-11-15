package Flow

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

suspend fun main() {
    flowContextExample1()
}

suspend fun getSmallFlow() = flow {
    for (i in 1..10) {
        //withContext(..){} // если обернуть код ниже в withContext - бдует краш.
        emit(i)
        println(currentCoroutineContext())
        delay(500)
    }
}

/** Flow примет контекст того места откуда был вызван collect. Это называется context preservation (Сохранение контекста)
 * Если это будет не так (Например обернуть эмиссию с помощью withContext) - будет краш*/
suspend fun flowContextExample1() = coroutineScope {
    withContext(CoroutineName("Caller method context") + Dispatchers.Default) {
        getSmallFlow().collect {
            println(it)
        }
    }
}

/**Если добавим оператор flowOn - исключения не будет. Это единственный способ изменить поток на котором будет происходить эмиссия, с условием того
 * что наблюдать будем на другом потоке*/
suspend fun getSmallFlowException() = flow {
    for (i in 1..10) {
        //withContext(..){} // если обернуть код ниже в withContext - бдует краш.
        emit(i)
        println(currentCoroutineContext())
        delay(500)
    }
}.flowOn(Dispatchers.IO)
