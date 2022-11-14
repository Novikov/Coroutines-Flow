package Flow

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeoutOrNull

suspend fun main() {
    cancelFlowExample1()
}

suspend fun getSimpleFlow() = flow {
    emit(1)
    delay(500)
    emit(2)
    delay(500)
    emit(3)
    delay(500)
    emit(4)
    delay(500)
    emit(5)
    delay(500)
    emit(6)
    delay(500)
    emit(7)
    delay(500)
    emit(8)
    delay(500)
    emit(9)
    delay(500)
    emit(10)
    delay(500)
}

suspend fun cancelFlowExample1() = coroutineScope{
    withTimeoutOrNull(1000){
        getSimpleFlow().collect{ value -> println(value)}
        println("done")
    }
}