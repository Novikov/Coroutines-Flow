package coroutines

import kotlinx.coroutines.*

suspend fun main() {
    /**launch*/
//    launchExample1()
//    launchExample2()
//    launchExample3()
//    launchExample4()
//    launchExample5()
//    launchExample6()

    /**async*/
//    asyncExample1()
//    asyncExample2()
//    asyncExample3()
    asyncExample4()
//    asyncExample5()

    /**lazy*/
}

/**
 * Прежде всего, launch(), как правило, применяется,
 * когда нам не надо возвращать результат из корутины и когда нам ее надо выполнять одновременно с другим кодом.
 * */

suspend fun launchExample1() = coroutineScope {

    launch {
        for (i in 1..5) {
            println(i)
            delay(400L)
        }
    }

    println("Start")
    println("End")
}

/**
 * launch билдер возвращает объект job. Вызов join() по ссылке job заставит подождать кода ниже до тех пор пока job выше, на котором произошел вызов не перейдет
 * в состояние completed (т.е выполнится все внутри и дочерниие корутинын).
 * */

suspend fun launchExample2() = coroutineScope {

    val job = launch {
        for (i in 1..5) {
            println(i)
            delay(400L)
        }
    }

    println("Start")
    job.join()
    println("End")
}

/**
 * Запуск нескольких корутин.
 * Подобным образом можно запускать в одной функции сразу несколько корутин. И они будут выполняться одновременно.
 * Попробуй запустить с join и без него в разных вариациях.
 * Понимание join() - код ниже подождет, пока job выше, на котором вызван join() перейдет в состояние completed (т.е когда выполнится все внутри + дочерние корутины)
 * */

suspend fun launchExample3() = coroutineScope {
    val job1 = launch {
        for (i in 0..5) {
            delay(400L)
            println(i)
        }
    }
    val job2 = launch {
        for (i in 6..10) {
            delay(400L)
            println(i)
        }
    }

//    joinAll(job1, job2) // Принимает varags из job.

    println("Hello Coroutines")
}

/**
 * Отложенное выполнение
 * По умолчанию построитель корутин launch создает и сразу же запускает корутину.
 * Однако Kotlin также позволяет применять технику отложенного запуска корутины (lazy-запуск), при котором корутина запускается при вызове метода start() объекта Job.
 * Для установки отложенного запуска в функцию launch() передается значение start = CoroutineStart.LAZY
 * */

suspend fun launchExample4() = coroutineScope {

    // корутина создана, но не запущена
    val job = launch(start = CoroutineStart.LAZY) {
        delay(200L)
        println("Coroutine has started")
    }

    delay(5000L)
    job.start() // запускаем корутину
    println("Other actions in main method")
}

/**
 * Все тоже самое можно проделать с помощью async
 * */


/**
 * Async emulation
 * 1)С помощью launch можно сделать 2 simultaneously запроса. Для этого нужно убрать join()
 * 2)С помощью launch нельзя вернуть результат, но можно класть его в общий разделяемый ресурс. Но это shared mutable state. Что есть костыль и может выстрелить.
 * Есть способы как это обойти, но это тоже костыли. Лучше этого избегать.
 * 3)Мы можем влиять на время выполнения с помощью join(). В текущем примере ниже с помощью join() мы говорим внешней корутине подождать выполнения
 * дочерних корутин. Время выполнения будет немного больше чем 400 милисекунд. Если вызвать jon1.join() сразу после его launch билдера (закомментированный код),
 * то время работы увеличится потому что вторая корутина будет ждать выполнения первой.
 * */

suspend fun launchExample5() = coroutineScope {

    val startTime = System.currentTimeMillis()
    val resultList = mutableListOf<Int>()

    val job1 = launch {
        delay(200)
        println("work from job1")
        resultList.add(1)
    }

//    job1.join()

    val job2 = launch {
        delay(400)
        println("work from job2")
        resultList.add(2)
    }

    job1.join()
    job2.join()

    val endTime = System.currentTimeMillis() - startTime

    println("coroutine end with time - $endTime and result list - $resultList")
}

/**
 * Способ заставить parent launch подождать выпполнения дочерних корутин
 * */
suspend fun launchExample6() {
    val scope = CoroutineScope(Dispatchers.Default)

    val parentCoroutineJob = scope.launch {
        launch {
            delay(1000)
            println("Child coroutine 1 has completed")
        }
        launch {
            delay(1000)
            println("Child coroutine 2 has completed")
        }
    }
    parentCoroutineJob.join() //заставляет parent launch подождать
    println("Parent coroutine has been completed")
}

/**----------------------------------------------------------------------------------------------------------------------------------------------------*/

/**
 * Async
 * */

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

