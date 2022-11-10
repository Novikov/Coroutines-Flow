package coroutines

import kotlinx.coroutines.*

suspend fun main() {
    coroutineScopeExample()
//    coroutineScopeExample2()
//    coroutineScopeExample3()
//    coroutineScopeExample3_1()
//    coroutineScopeExample4()
//    coroutineScopeExample5()
//    coroutineScopeExample6()
//    coroutineScopeExample7()
}

/**
 * Корутина может выполняться только в определенной области корутины (coroutine scope).
 * Область корутин представляет пространство, в рамках которого действуют корутины, она имеет определенный жизненный цикл и
 * сама управляет жизненным циклом создаваемых внутри нее корутин.
 *
 * Scope вершина иерархии связи корутин через job. Scope так же имеет свой job.
 *
 * И для создания области корутин в Kotlin может использоваться ряд функций, которые создают объект интерфейса CoroutineScope.
 * Одной из функций является coroutineScope. Она может применяться к любой функции, например:
 * */

suspend fun coroutineScopeExample() {
    val scope = CoroutineScope(Job())
    val jobInstance = scope.launch {
        for (i in 0..5) {
            println(i)
            delay(400L)
        }
    }
    println("Hello Coroutines")
    println("${scope.coroutineContext[Job]} $jobInstance") // распечатать ссылку на job из текущего scope и parent корутины
}

/**У Global scope отсутствует job, а это значит что не будет формироваться иерархия если мы создадим семейство корутин на данном scope.
 * Время жизни данного scope соответствует времени жизни приложения. Его нужно избегать. Отменить его можно только вручную.
 * Если отсутствует job то значит отсутствует механизм structured concurrency (отмены корутин и проброса исключений).
 * */
suspend fun coroutineScopeExample2() {
    val jobInstance = GlobalScope.launch {
        for (i in 0..5) {
            println(i)
            delay(400L)
        }
        throw Exception("ex")
    }.join()

    val jobInstance2 = GlobalScope.launch {
        for (i in 5..10) {
            println(i)
            delay(400L)
        }
    }.join()

    println("Hello Coroutines")
    println("${GlobalScope.coroutineContext[Job]} $jobInstance") // распечатать ссылку на job из текущего scope и parent корутины
}

/**
 * Функция runBlocking блокирует вызывающий поток, пока все корутины внутри вызова runBlocking { ... } не завершат свое выполнение.
 * Нет необходимости вызывать join()
 * */
fun coroutineScopeExample3() {
    runBlocking {
        launch {
            for (i in 0..5) {
                delay(400L)
                println(i)
            }
        }
    }
    println("Hello Coroutines")
}

/** Но нужно помнить, что runBlocking блокирует только поток выполнения пока не выполнится. Корутины внутри потока не блокирует.*/
fun coroutineScopeExample3_1() {
    runBlocking {
        launch {
            for (i in 0..5) {
                delay(400L)
                println(i)
            }
        }

        launch {
            for (i in 5..10) {
                delay(400L)
                println(i)
            }
        }
    }
    println("Hello Coroutines")
}

/**
 * CoroutineScope билдер.
 * Ниже есть поведение которого мы можем добиться с помощью coroutineScope билдера, что сделано в coroutineScopeExample5.
 * Т.е дочерние корутины
 * */
fun coroutineScopeExample4() {
    val scope = CoroutineScope(Job())
    scope.launch {
        val job1 = launch {
            println("Start task 1")
            delay(300)
            println("End task 1")
        }
        val job2 = launch {
            println("Start task 2")
            delay(200)
            println("End task 2")
        }

        job1.join()
        job2.join()

        val job3 = scope.launch {
            println("Start task 3")
            delay(100)
            println("End task 3")
        }
    }

    Thread.sleep(2000)
}

/**
 * Есть еще другой вариант - поместить task1 и task2 в launch и вызывать на нем join() до выполнения task3.
 * Данный scope отдаст управление на task3 только когда все его child будут завершены. Т.е теряется конкурентное поведение.
 * В случае выброса exception launch 1,2 и 3 будут отменены.
 * */
suspend fun coroutineScopeExample5() {
    val scope = CoroutineScope(Job())
    scope.launch {
        coroutineScope {
            launch {
                println("Start task 1")
                delay(100)
                throw Exception()
                println("End task 1")
            }
            launch {
                println("Start task 2")
                delay(200)
                println("End task 2")
            }
        }

        val job3 = launch {
            println("Start task 3")
            delay(300)
            println("End task 3")
        }
    }

    Thread.sleep(2000)
}

/**
 * SupervisorScope builder
 * Исключение бросится, но launch2 и launch3 продолжат свое выполнение
 * */
suspend fun coroutineScopeExample6() {
    val scope = CoroutineScope(Job())
    scope.launch {
        supervisorScope {
            launch {
                println("Start task 1")
                delay(100)
                throw Exception()
                println("End task 1")
            }
            launch {
                println("Start task 2")
                delay(200)
                println("End task 2")
            }
        }

        val job3 = launch {
            println("Start task 3")
            delay(300)
            println("End task 3")
        }
    }

    Thread.sleep(2000)
}

/**
 * coroutineScope, как и supervisorScope билдеры с вызыванным join() по умолчанию. Код ниже будет ждать пока он перейдет в состояние completed
 * (когда выполнится все внутри и то что ниже)
 * */
suspend fun coroutineScopeExample7() {
    coroutineScope {
        launch {
            delay(5000L)  // имитация продолжительной работы
            println("Hello work!")
        }
    }

    println("Program has finished")
}