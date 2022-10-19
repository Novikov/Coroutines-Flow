package coroutines

import kotlinx.coroutines.*

suspend fun main() {
//    cancellationExample1()
//    cancellationExample1_1()
//    cancellationExample1_2()
    cancellationExample1_3()
}

/**
 *Пример отмены корутины.
 * job.cancel() не является suspend функцией и может быть вызывана где угодно.
 * Тоже самое можно сделать если вручную создать scope и вызывать cancel на нем.
 * */
suspend fun cancellationExample1() = coroutineScope {
    val job = launch {
        repeat(10) { index ->
            println("operation number $index")
            delay(100)
        }
    }
    delay(250)
    println("canceling coroutine")
    job.cancel()
}

/**
 * Если заменить delay внутри launch на Thread sleep, то отмена не произойдет. Причина в том, что если вызывается suspend функция (например delay)
 * на coroutine scope который уже в состоянии cancelling то будет брошен особый CancellationException, который прервет выполнение корутины.
 * Данный Exception обрабатывать не обязательно, но можно если надо. В примере ниже нет suspend функции поэтоу выполнение не прервется.
 * */
suspend fun cancellationExample1_1() = coroutineScope {
    val job = launch {
        repeat(10) { index ->
            delay(1)
            println("operation number $index")
            Thread.sleep(100)
        }
    }
    delay(250)
    println("canceling coroutine")
    job.cancel()
}

/**
 * Способы остановки работы внутри корутины
 * 1.Вызов suspend функций ensureActive(), yield(), delay(). Task - Посмотреть разницу между данными функциями
 * 2.Проверка состояния корутины с помощью оборота всего блока в if(isActive)
 * */

suspend fun cancellationExample1_2() = coroutineScope {
    val job = launch {
        repeat(10) { index ->
            yield()
            delay(1)
            println("operation number $index")
            Thread.sleep(100)
        }
    }
    delay(250)
    println("canceling coroutine")
    job.cancel()
}

suspend fun cancellationExample1_3() = coroutineScope {
    val job = launch {
        repeat(10) { index ->
            if (isActive) {
                delay(1)
                println("operation number $index")
                Thread.sleep(100)
            }
        }
    }
    delay(250)
    println("canceling coroutine")
    job.cancel()
}