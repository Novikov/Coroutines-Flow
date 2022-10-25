package coroutines

import kotlinx.coroutines.*

suspend fun main() {
//    launchExample1()
    launchExample2()
//    joinLaunchExample()
//    lazyLaunchExample()
//    asyncEmulationExample()
//    parentLaunchWaitsNestedExample()
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
 * в состояние completed.
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
 * Понимание join() - код ниже подождет, пока job выше, на котором вызван join() перейдет в состояние completed.
 * */

suspend fun multipleCoroutines() = coroutineScope {
    launch {
        for (i in 0..5) {
            delay(400L)
            println(i)
        }
    }
    launch {
        for (i in 6..10) {
            delay(400L)
            println(i)
        }
    }

    println("Hello Coroutines")
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
 * 2)С помощью launch нельзя вернуть результат, но можно класть его в общий разделяемый ресурс. Но это shared mutable state. Что есть костыль и может выстрелить.
 * Есть способы как это обойти, но это тоже костыли. Лучше этого избегать.
 * 3)Мы можем влиять на время выполнения с помощью join(). В текущем примере ниже с помощью join() мы говорим внешней корутине подождать выполнения
 * дочерних корутин. Время выполнения будет немного больше чем 400 милисекунд. Если вызвать jon1.join() сразу после его launch билдера (закомментированный код),
 * то время работы увеличится потому что вторая корутина будет ждать выполнения первой.
 * */

suspend fun asyncEmulationExample() = coroutineScope {

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

    println("coroutine end with time - $endTime and result list - $resultList" )
}

/**
 * Способ заставить parent launch подождать выпполнения дочерних корутин
 * */

suspend fun parentLaunchWaitsNestedExample() {
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

//    parentCoroutineJob.join() заставляет parent launch подождать

    println("Parent coroutine has been completed")
}