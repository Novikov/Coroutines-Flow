package coroutines

import kotlinx.coroutines.*

suspend fun main() {
//    launchExample()
//    joinLaunchExample()
//    lazyLaunchExample()
    asyncEmulationExample()
}

/**
 * Прежде всего, launch(), как правило, применяется,
 * когда нам не надо возвращать результат из корутины и когда нам ее надо выполнять одновременно с другим кодом.
 * */

suspend fun launchExample() = coroutineScope {

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
 * launch билдер возвращает объект job. Вызов join() по ссылке job заставит подождать выполенения блока кода внутри launch для всего что ниже join()
 * */

suspend fun joinLaunchExample() = coroutineScope {

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
 * Отложенное выполнение
 * По умолчанию построитель корутин launch создает и сразу же запускает корутину.
 * Однако Kotlin также позволяет применять технику отложенного запуска корутины (lazy-запуск), при котором корутина запускается при вызове метода start() объекта Job.
 * Для установки отложенного запуска в функцию launch() передается значение start = CoroutineStart.LAZY
 * */

suspend fun lazyLaunchExample() = coroutineScope {

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
 * 2)С помощью launch нельзя вернуть результат.
 * */

suspend fun asyncEmulationExample() = coroutineScope {

    val startTime = System.currentTimeMillis()

    val job1 = launch {
        delay(200)
        println("work from job1")
    }

    val job2 = launch {
        delay(400)
        println("work from job2")
    }

    job1.join()
    job2.join()

    val endTime = System.currentTimeMillis() - startTime

    println("coroutine end $endTime")
}