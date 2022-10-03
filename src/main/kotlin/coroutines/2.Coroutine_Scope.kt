package coroutines

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

suspend fun main() {
//    doWork()
//    multipleCoroutines()
    blockingScope()
}

/**
 * Корутина может выполняться только в определенной области корутины (coroutine scope).
 * Область корутин представляет пространство, в рамках которого действуют корутины, она имеет определенный жизненный цикл и
 * сама управляет жизненным циклом создаваемых внутри нее корутин.
 *
 * И для создания области корутин в Kotlin может использоваться ряд функций, которые создают объект интерфейса CoroutineScope.
 * Одной из функций является coroutineScope. Она может применяться к любой функции, например:
 * */

suspend fun doWork() = coroutineScope {
    launch {
        for (i in 0..5) {
            println(i)
            delay(400L)
        }
    }
    println("Hello Coroutines")
}

/**
 * Запуск нескольких корутин.
 * Подобным образом можно запускать в одной функции сразу несколько корутин. И они будут выполняться одновременно.
 * Попробуй запустить с join и без него в разных вариациях.
 * Понимание join() - подождать выполнения работы все что ниже
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
 * Функция runBlocking блокирует вызывающий поток, пока все корутины внутри вызова runBlocking { ... } не завершат свое выполнение.
 * В этом собственно основное отличие runBlocking от coroutineScope: coroutineScope не блокирует вызывающий поток,
 * а просто приостанавливает выполнение, освобождая поток для использования другими ресурсами.
 * */
fun blockingScope() = runBlocking {
    launch {
        for (i in 0..5) {
            delay(400L)
            println(i)
        }
    }

    println("Hello Coroutines")
}