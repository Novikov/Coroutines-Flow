package Coroutines

import kotlinx.coroutines.*

suspend fun main() {
//    asyncExample()
//    asyncValueExample()
//    tripleAsync()
    lazyAsync()
}

/** async запускает отдельную корутину, которая выполняется параллельно с остальными корутинами.* */
suspend fun asyncExample() = coroutineScope {

    async {
        delay(500L)  // имитация продолжительной работы
        println("Hello work!")
    }
    println("Program has finished")
}

/**
 * Кроме того, async-корутина возвращает объект Deferred, который ожидает получения результата корутины.
 * (Интерфейс Deferred унаследован от интерфейса Job, поэтому для также доступны весь функционал, определенный для интефейса Job)
 * Для получения результата из объекта Deferred применяется функция await().
 * */

suspend fun asyncValueExample() = coroutineScope {

    val message: Deferred<String> = async { getMessage() }
    println("message: ${message.await()}")
    println("Program has finished")
}

suspend fun getMessage(): String {
    delay(500L)  // имитация продолжительной работы
    return "Hello"
}


/**
 * Мы можем с помощью async запустить несколько корутин, которые будут выполняться параллельно.
 * */
suspend fun tripleAsync() = coroutineScope {

    val numDeferred1 = async { sum(1, 2) }
    val numDeferred2 = async { sum(3, 4) }
    val numDeferred3 = async { sum(5, 6) }
    val num1 = numDeferred1.await()
    val num2 = numDeferred2.await()
    val num3 = numDeferred3.await()

    println("number1: $num1  number2: $num2  number3: $num3")
}

suspend fun sum(a: Int, b: Int): Int {
    delay(500L) // имитация продолжительной работы
    return a + b
}

/** Отложенный запуск
 * По умолчанию построитель корутин async создает и сразу же запускает корутину.
 * Но как и при создании корутины с помощью launch для async-корутин можно применять технику отложенного запуска.
 * Только в данном случае корутина запускается не только при вызове метода start объекта Deferred (который усналедован от интерфейса Job),
 * но также и с помощью метода await() при обращении к результу корутины
 * */

suspend fun lazyAsync() = coroutineScope {

    // корутина создана, но не запущена
    val sum = async(start = CoroutineStart.LAZY) { sumLazy(1, 2) }

    //    sum.start()                      // запуск корутины
    delay(1000L)
    println("Actions after the coroutine creation")
    println("sum: ${sum.await()}")   // запуск и выполнение корутины
}

fun sumLazy(a: Int, b: Int): Int {
    println("Coroutine has started")
    return a + b
}

// Если необходимо, чтобы корутина еще до метода await() начала выполняться, то можно вызвать метод start()
