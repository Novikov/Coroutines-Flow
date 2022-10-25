package coroutines

import kotlinx.coroutines.*

suspend fun main() {
//    asyncExample1()
//    asyncExample2()
//    asyncExample3()
//    asyncExample4()
    asyncExample5()
}

/** async запускает отдельную корутину, которая выполняется параллельно с остальными корутинами. Тоже самое что и Launch*
 * Тут важно запомнить что async всеровно начнет выполнение и без await(). Await - это аналог join но только для того, чтобы вернуть результат.
 * Интерфейс Deferred унаследован от интерфейса Job, поэтому для также доступны весь функционал, определенный для интерфейса Job
 * */
suspend fun asyncExample1() {
    val scope = CoroutineScope(Job())
    val deferred = scope.async {
        delay(500L)  // имитация продолжительной работы
        println("Hello work!")
    }
    deferred.join() //Так же может использоваться потому что deferred наследник job
    println("Program has finished")
}

/** Для получения результата из объекта Deferred применяется функция await(). */
suspend fun asyncExample2() = coroutineScope {
    val message: Deferred<String> = async { getMessage() }
    println("message: ${message.await()}")
    println("Program has finished")
}

suspend fun getMessage(): String {
    delay(500L)  // имитация продолжительной работы
    return "Hello"
}

/** С помощью async можно запустить несколько корутин, которые будут выполняться параллельно */
suspend fun asyncExample3() = coroutineScope {

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
 * Только в данном случае корутина запускается не только при вызове метода start объекта Deferred (который унаcледован от интерфейса Job),
 * но также и с помощью метода await() при обращении к результу корутины
 * */

suspend fun asyncExample4() = coroutineScope {
    // корутина создана, но не запущена
    val sum = async(start = CoroutineStart.LAZY) { sumLazy(1, 2) }
//    sum.start()                      // запуск корутины, если необходимо начать выполнение до вызова await()
    delay(1000L)
    println("Actions after the coroutine creation")
    println("sum: ${sum.await()}")   // запуск и выполнение корутины
}

fun sumLazy(a: Int, b: Int): Int {
    println("Coroutine has started")
    return a + b
}

/** Полезная техника маппинга списка через async*/
suspend fun asyncExample5() = coroutineScope {
    val idList = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    val dataList = idList.toMutableList().map { async { getDataById(it) } }.awaitAll()
    println(dataList)
}
suspend fun getDataById(id: Int): String {
    delay(100)
    return "Data by id - $id"
}
