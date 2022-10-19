package coroutines

import kotlinx.coroutines.*

suspend fun main() {
//    cancellationExample1()
//    cancellationExample1_1()
//    cancellationExample1_2()
//    cancellationExample1_3()
//    cancellationExample1_4()
    cancellationExample2()
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
 * Все suspend функции в библиотеке корутин - cancelable (генерят Cancellation exception если job isActive == false)
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
 * 1.Вызов suspend функций ensureActive(), yield(), delay() или любая другая suspend function. Task - Посмотреть разницу между данными функциями
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

/*** Данный способ имеет преимущество в том, что не выбрасывает мгновенно cancellation exception и позволяет выполнить некоторые cleanUp operations */
suspend fun cancellationExample1_3() = coroutineScope {
    val job = launch {
        repeat(10) { index ->
            if (isActive) {
                delay(1)
                println("operation number $index")
                Thread.sleep(100)
            } else {
                //perform cleanup operations
                println("Clean up")
                throw CancellationException()
            }
        }
    }
    delay(250)
    println("canceling coroutine")
    job.cancel()
}

/**
 * После того, как выполнился cancel и job корутины перешел в состояние isActive == false - вызов любой функции приведет к выбросу cancellation exception.
 * Поэтому если необходимо выполнить некоторый suspend код (например в блоке else clenUp operations) - оборачиваем все это дело в
 * withContext(NonCancelable)
 * */
suspend fun cancellationExample1_4() = coroutineScope {
    val job = launch {
        repeat(10) { index ->
            if (isActive) {
                delay(1)
                println("operation number $index")
                Thread.sleep(100)
            } else {
                //perform cleanup operations
                withContext(NonCancellable){
                    delay(100)
                    println("Clean up")
                }
                throw CancellationException()
            }
        }
    }
    delay(250)
    println("canceling coroutine")
    job.cancel()
}

/**
 * TimeOut
 * Есть специальная функция которая генерит наследника cancelation exception если выполнение корутины занимает больше по времени, чем наши ограничения.
 * Есть аналог который возвращает вместо CancelationException - null (withTimeoutOrNull())
 * */
suspend fun cancellationExample2() = coroutineScope {
    launch {
        withTimeout(1300L) {
            repeat(1000) { i ->
                println("I'm sleeping $i ...")
                delay(500L)
            }
        }
    }
}


//join + cancelAndJoin

/**
 * CancelAndJoin() нужен для того чтобы подождать пока завершится корутина. В офф доке есть пример где эта строчка кода заставляет ждать пока выполнится
 * finaly блок у try-catch. https://kotlinlang.org/docs/cancellation-and-timeouts.html#closing-resources-with-finally
 *
 *
 * cancelable
 *
 * состояния
 *
 * /**
 * Существует 2 типа suspend функций: обычные и отменяемые.
 * Первая создается через suspendCoroutine билдер и не реагируют на отмену корутины. Насколько я понял речь идет о launch.
 * Вторая создается через suspendCancellableCoroutine и имеет внутренний колбэк для прекращения работы suspend фукнции который вызовется
 * в случае отмены. Подробности можно посмотреть здесь https://startandroid.ru/ru/courses/kotlin/29-course/kotlin/611-urok-16-korutiny-otmena-kak-oshibka.html
 * */
 * */