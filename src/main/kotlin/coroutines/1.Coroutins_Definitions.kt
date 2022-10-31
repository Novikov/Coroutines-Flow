package coroutines

import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.system.measureTimeMillis

suspend fun main() {
//    routineExample()
//    coroutineExample()
//    suspendExample()
    asynchExample()
//    suspendApiCallExample()
//    blockingCoroutinesExample()
}

/**
 * In computer programming, a function or subroutine (when it doesn't return a value) is a sequence of program instructions that performs a specific task,
 * packaged as a unit. This unit can then be used in programs wherever that particular task should be performed.
 * Метод это понятие связанное с классом и ООП.
 * */


/** Когда происходит вызов routine1 - control flow передается в routine1. После выполнения subroutine поток выполнения передается обратно в routineExample() */
fun routineExample() {
    println("main starts")
    routine(1, 300)
    routine(2, 500)
    println("main ends")
}

fun routine(number: Int, delay: Long) {
    println("Routine $number starts work")
    Thread.sleep(delay)
    println("Routine $number has finished")
}

/** В данном случае control flow передастся в coroutine1, но в тот момент когда он дойдет до suspend функции delay - поток выполнения будет отдан для coroutine2
 * coroutine2 запустится до того, выполнится coroutine1
 *
 * В этом кроется смысл корутин. Приставка co обозначает cooperative. Т.е возможность одной рутины передавать поток управления для другой пока первая
 * находится в состоянии suspended.
 * */
suspend fun coroutineExample() = coroutineScope {
    println("main starts")
    joinAll(
        launch { coroutine(1, 500) },
        launch { coroutine(2, 300) }
    )
    println("main ends")
}

suspend fun coroutine(number: Int, delay: Long) {
    println("Routine $number starts work")
    delay(delay)
    println("Routine $number has finished")
}

/**
 * Определение из документации
 * A coroutine is an instance of suspendable computation. It is conceptually similar to a thread, in the sense that it takes a block of code to run
 * that works concurrently with the rest of the code. However, a coroutine is not bound to any particular thread. It may suspend its execution in one
 * thread and resume in another one.

 * Coroutines can be thought of as light-weight threads, but there is a number of important differences that make their real-life usage very different from threads.
 * */

/**
 * Любая корутина может выполняться только на coroutineScope. Способы получения scope в 11.Scope_Builders. Scope имеет контекст состоящий из 4 параметров:
 * Job, Dispatcher, ErrorHandler, CoroutineName.*/

/**
 * Suspend
 * Модификатор suspend не делает функцию асинхронной, но suspend функции можно вызывать только из других suspend функций или из переходника runblocking.
 * suspend обозначает что функция может приостановить свое выполнение и отдать control flow другой функции. После чего suspend может возобновить свое выполнение
 * и завершиться на другом потоке. Корутина выполняющая suspend функцию не привязана к конкретному потоку.
 * */

/** Само по себе слово suspend не делает работу асинхронной. Функции будут засыпать на указанное время и выполняться последовательно*/
suspend fun suspendExample() {
    val time = measureTimeMillis {
        val one = doSomethingUsefulOne()
        val two = doSomethingUsefulTwo()
        println("The answer is ${one + two}")
    }
    println("time $time")
}

suspend fun doSomethingUsefulOne(): Int {
    delay(1500L) // pretend we are doing something useful here
    return 13
}

suspend fun doSomethingUsefulTwo(): Int {
    delay(1000L) // pretend we are doing something useful here, too
    return 29
}

/** Пример асинхронного выполнения. */
suspend fun asynchExample() {
    val time = measureTimeMillis {
        coroutineScope {
            val one = async { doSomethingUsefulOne() }
            val two = async { doSomethingUsefulTwo() }
            println("The answer is ${one.await() + two.await()}")
        }
    }
    println("time $time")
}

/**
 * Способы реализации асинхронщины в Android:
 * 1)Callback функции
 * 2)RX
 * 3)Croroutines
 *
 * Иногда возникает необходимость обернуть 1 подход в suspend функции. Такие задачи часто встречаются у разработчиков API которые хотят подружить его с
 * корутинами и обернуть в suspend функции. Для этого необходимо получить continuation и не забыть возобновить работку корутины иначе она потеряется.
 * */

suspend fun suspendApiCallExample() {
    val scope = CoroutineScope(Dispatchers.Unconfined)

    scope.launch() {
        println("start coroutine ${Thread.currentThread().name}")
        val data = getDataFromApi()
        println("end coroutine ${Thread.currentThread().name}")
    }

    delay(2000)
}

suspend fun getDataFromApi(): Int = suspendCoroutine {
    println("suspend function, start")
    thread {
        println("suspend function, background work")
        TimeUnit.MILLISECONDS.sleep(1000)
        it.resume(5) // Возобновление работы корутины. Если не вызвать то корутина не завершится
    }
}

/**
 * Корутины никак не блокируют друг друга. Обсудить последовательность вызовов.
 * */
private fun blockingCoroutinesExample() {
    val scope = CoroutineScope(Job())
    println("onRun, start")

    scope.launch {
        println("coroutine, start ${Thread.currentThread().name}")
        delay(1000)
        println("coroutine, end ${Thread.currentThread().name}")
    }

    println("onRun, middle")

    scope.launch {
        println("coroutine2, start ${Thread.currentThread().name}")
        delay(1000)
        println("coroutine2, end ${Thread.currentThread().name}")
    }

    println("onRun, end")
}

/**
 * Structured concurrency это набор принципов работы с асинхронщиной:
 * 1)Every coroutine needs to be started in a logical scope with limited life-time.
 * 2)Coroutines started in the same scope from a hierarchy.
 * 3)A parent job won't complete, until all of its children completed.
 * 4)Canceling a parent will cancel all children. Canceling a child won't cancel the parent or siblings.
 * 5)If a child coroutine fails, the exception propagated upwards and depending on the job type, either all siblings are canceled or not.
 * */
