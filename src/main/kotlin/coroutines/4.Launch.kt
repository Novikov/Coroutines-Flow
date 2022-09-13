package coroutines

import kotlinx.coroutines.*

suspend fun main() {
    lazyLaunchExample()
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
 * launch билдер возвращает объект job. Вызов join() по ссылке job заставит подождать выполенения блока кода внутри launch
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

suspend fun lazyLaunchExample() = coroutineScope{

    // корутина создана, но не запущена
    val job = launch(start = CoroutineStart.LAZY) {
        delay(200L)
        println("Coroutine has started")
    }

    delay(5000L)
    job.start() // запускаем корутину
    println("Other actions in main method")
}