package coroutines

import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

suspend fun main() {
//    errorExample1()
//    errorExample1_1()
//    errorExample2()
//    errorExample2_1()
//    errorExample4()
//    errorExample4_1()
//    errorExample5()
    errorExample5_1()

}

/**
 * Лучшее объяснение обработки ошибок можно посмотреть здесь https://www.lukaslechner.com/why-exception-handling-with-kotlin-coroutines-is-so-hard-and-how-to-successfully-master-it/
 * */

/** #1 try catch
 * try-catch не отработает за пределами launch из-за особенностей structured concurrency
 * */
suspend fun errorExample1() = coroutineScope {
    println("onRun start")
    try {
        launch {
            Integer.parseInt("a")
        }
    } catch (e: Exception) {
        println("error $e")
    }

    println("onRun end")
}


/** errorExample1_1
 * try-catch отработает т.к он сработает в месте выброса exception
 * */
suspend fun errorExample1_1() = coroutineScope {
    println("onRun start")
    launch {
        try {
            Integer.parseInt("a")
        } catch (e: Exception) {
            println("error $e")
        }
    }
    println("onRun end")
}

/**
 * #2 Coroutine Exception Handler
 *
 * Example2
 * Отработает в двух случаях:
 * 1)Если coroutineExceptionHandler установлен в scope на котором происходит вызов билдера корутины
 * */

suspend fun errorExample2() {
    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, exception ->
        println("Handle $exception in CoroutineExceptionHandler")
    }

    val topLevelScope = CoroutineScope(Job() + coroutineExceptionHandler)

    topLevelScope.launch {
        launch {
            throw RuntimeException("RuntimeException in nested coroutine")
        }
    }

    Thread.sleep(100)
}

/**
 * 2)Если handler передан в parent coroutine builder. Если мы передадим его в дочернюю корутину - обработка exception не произойдет.
 * */
suspend fun errorExample2_1() {
    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, exception ->
        println("Handle $exception in CoroutineExceptionHandler")
    }

    val topLevelScope = CoroutineScope(Job())

    topLevelScope.launch(coroutineExceptionHandler) {
        launch {
            Integer.parseInt("a")
        }
    }

    Thread.sleep(100)
}

/**
 * #3 try-catch vs coroutine exception handler
 * “CoroutineExceptionHandler is a last-resort mechanism for global “catch all” behavior.
 * You cannot recover from the exception in the CoroutineExceptionHandler.The coroutine had already completed with the corresponding exception when the handler is called.
 * Normally, the handler is used to log the exception, show some kind of error message, terminate, and/or restart the application
 *
 * Use try/catch if you want to retry the operation or do other actions before the Coroutine completes.
 * Keep in mind that by catching the exception directly in the Coroutine, it isn’t propagated up the job hierarchy and you aren’t making use of the cancellation functionality of Structured Concurrency.
 * Use the CoroutineExceptionHandler for logic that should happen after the coroutine already completed.
 * */


/**
 * #4 Async
 *
 *
 * */

/** errorExample4
 * Async требует иного способа работы с try-catch.
 *
 * Поведение для top level async
 * 1)Если мы вызываем async на созданном scope то exception выбросится только при вызове await()
 * 2)Если мы передадим coroutine exception handler то exception всеровно туда не придет. В example4_1 показано как отправить exception
 * в coroutineExceptionHandler
 *
 * */
suspend fun errorExample4() = coroutineScope {

    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, exception ->
        println("Handle $exception in CoroutineExceptionHandler")
    }

    val topLevelScope = CoroutineScope(Job() + coroutineExceptionHandler)

    val deferredResult = topLevelScope.async {
        throw RuntimeException("RuntimeException in async coroutine")
    }

    try {
        deferredResult.await()
    } catch (exception: Exception) {
        println("Handle $exception in try/catch")
    }

    delay(100)
}

/**
 * Поведение для child async
 * 1)Если async является дочерним билдером то для выброса исключения не требуется вызывать await().
 * 2)В это случае исключение придет в coroutineExceptionHandler
 *
 * Вывод:
 * 1)Вызов launch всегда приведет к попаданию exception в coroutineExceptionHandler не зависимо от того этот launch child или parent.
 * 2)У launch отсутствует await() метод поэтому всегда try-catch всегда используем внутри launch, оборачивая код корутины
 * */
suspend fun errorExample4_1() = coroutineScope {
    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, exception ->
        println("Handle $exception in CoroutineExceptionHandler")
    }

    val topLevelScope = CoroutineScope(Job() + coroutineExceptionHandler)
    topLevelScope.launch {
        async {
            throw RuntimeException("RuntimeException in async coroutine")
        }
    }
    Thread.sleep(100)
}

/**
 * #5 Scope
 * Разные scope никак не связаны между собой. Если в первом будет ошибка, второй продолжит работать.
 * Данный handler передастся во все дочерние корутины т.к он является частью coroutine context.
 * */
suspend fun errorExample5() = coroutineScope {
    val handler = CoroutineExceptionHandler { context, exception ->
        println("first coroutine exception $exception")
    }

    val scope1 = CoroutineScope(Job() + handler)
    val scope2 = CoroutineScope(Job())

    scope1.launch(handler) {
        TimeUnit.MILLISECONDS.sleep(300)
        Integer.parseInt("a")
    }

    scope2.launch {
        repeat(5) {
            TimeUnit.MILLISECONDS.sleep(300)
            println("second coroutine isActive ${isActive}")
        }
    }

    delay(5000)
}

/**
 * CoroutineScope
 * Try-Catch не должен перехватывать exception если он расположен над coroutine билдером, но в случае с coroutine scope это правило н работает.
 * Coroutine scope повтороно генерирует исключение своих дочерних элементов вместо того, чтобы прокидывать их вверх по иерархии, что позволяет ловить
 * его с помощью try-catch за пределами scope.
 * */
suspend fun errorExample5_1() {
    val topLevelScope = CoroutineScope(Job())

    topLevelScope.launch {
        try {
            coroutineScope {
                launch {
                    throw RuntimeException("RuntimeException in nested coroutine")
                }
            }
        } catch (exception: Exception) {
            println("Handle $exception in try/catch")
        }
    }

    Thread.sleep(100)
}
