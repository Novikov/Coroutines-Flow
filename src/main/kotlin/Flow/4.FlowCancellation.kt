package Flow

import kotlinx.coroutines.*
import kotlinx.coroutines.NonCancellable.cancel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

suspend fun main() {
    cancelFlowExample1()
//    cancelFlowExample2()
//    cancelFlowExample3()
//    cancelFlowExample4()
//    cancelFlowExample5()
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

/** Отмена по таймауту*/
suspend fun cancelFlowExample1() = coroutineScope {
    withTimeoutOrNull(1000) {
        getSimpleFlow().collect { value -> println(value) }
        println("done")
    }
}

/** Отмена по значению*/
suspend fun cancelFlowExample2() = coroutineScope {
    withTimeoutOrNull(1000) {
        getSimpleFlow().collect { value ->
            println(value)
            if (value == 3) cancel()
        }
        println("done")
    }
}

/** Некоторые операторы не имеют обработчика Cancellation Exception.
 * Если попытаемся отменить flow то выбросится cancellation exception, который не остановит flow и выбросится в обработчик выше*/
suspend fun cancelFlowExample3() = coroutineScope {
    (1..5).asFlow().collect { value ->
        if (value == 3) {
            cancel()
        }
        println(value)
    }
}

/** Есть способ порешать данную проблему с помощью операторов
 * Корутина прервется, исключение хоть и печатается, но это ок.
 * */
suspend fun cancelFlowExample4() = coroutineScope {
    (1..5).asFlow()
//        .onEach { currentCoroutineContext().ensureActive() } //1 способ
        .cancellable() //2 способ
        .collect { value ->
            if (value == 3) {
                cancel()
            }
            println(value)
        }
}