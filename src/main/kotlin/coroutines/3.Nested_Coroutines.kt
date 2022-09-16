package coroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

suspend fun main() {
//    nestedCoroutineExample()
//    parentWaitChildExample()
    parallelWorkExample()
}

/*** Launch билдер создает свой внутренний scope.
 * Родительская корутина выполнила весь свой код не дожидаясь выполнения дочерней. А дочерняя корутина в отдельном потоке выполнила свой код.
 * Хоть родительская корутина и выполнила сразу же весь свой код, но ее статус поменяется на Завершена только когда выполнится дочерняя корутина.
 * */
suspend fun nestedCoroutineExample() = coroutineScope {
    launch {
        println("Outer coroutine start")
        launch {
            println("Inner coroutine start")
            delay(400L)
            println("Inner coroutine end")
        }
        println("Outer coroutine end")
    }
}

/**
 * Возможно, этот пример кажется вам бессмысленным.
 * Зачем запускать дочернюю корутину в новом фоновом потоке, если родительская корутина и так уже выполняется в фоновом потоке.
 * Можно было бы всю работу сделать в родительской корутине и не плодить потоки.

 * И вот тут я вам должен сказать очень интересную вещь, которая, может поменять ваше текущее представление о корутинах.
 * Постарайтесь понять следующее утверждение, т.к. это очень важно:

 * Корутина может выполняться в main потоке.

 * Выше рассмотренный пример станет выглядеть гораздо более осмысленным и полезным, если код родительской корутины выполняется в main потоке, а код дочерней - в фоновом. Получается, что из main потока мы запустили фоновую работу и подождали ее окончания без каких-либо блокировок и колбэков.
 * join не заблокирует main поток, а только приостановит
 * */
suspend fun parentWaitChildExample() = coroutineScope {
    launch {
        println("parent coroutine, start")

        val job = launch {
            println("child coroutine, start")
            delay(1000)
            println("child coroutine, end")
        }

        println("parent coroutine, wait until child completes")
        job.join()

        println("parent coroutine, end")
    }
}

/**
 * Если возникает вопрос, почему то же самое нельзя без родительской корутины провернуть, то вспомните, что join - это suspend функция.
 * Она не может быть вызвана вне корутины.
 * */

/**
 * В следующем примере показан пример паралельной работы дочерних корутин.
 * Родительская корутина будет их ждать. Общее время выполнения будет 1500
 * */
suspend fun parallelWorkExample() = coroutineScope {
    launch {
        println("parent coroutine, start")

        val job = launch {
            delay(1000)
        }

        val job2 = launch {
            delay(1500)
        }

        println("parent coroutine, wait until children complete")
        job.join()
        job2.join()

        println("parent coroutine, end")
    }
}