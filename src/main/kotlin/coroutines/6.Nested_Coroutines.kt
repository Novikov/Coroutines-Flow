package coroutines

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

suspend fun main() {
//    nestedCoroutineExample1()
    nestedCoroutineExample2()
//    parallelWorkExample()
//    relationBetweenParentAndChild()
}

/*** Launch билдер создает свой внутренний scope.
 * Родительская корутина выполнила весь свой код не дожидаясь выполнения дочерней. А дочерняя корутина в отдельном потоке выполнила свой код.
 * Хоть родительская корутина и выполнила сразу же весь свой код, но ее статус поменяется на Completed только когда выполнится дочерняя корутина.
 * */
suspend fun nestedCoroutineExample1() = coroutineScope {
    launch {
        println("Outer coroutine start ${Thread.currentThread().name}")
        launch {
            println("Inner coroutine start ${Thread.currentThread().name}")
            delay(400L)
            println("Inner coroutine end ${Thread.currentThread().name}")
        }
        println("Outer coroutine end ${Thread.currentThread().name}")
    }
}

/**
 * Возможно, этот пример кажется вам бессмысленным.
 * Зачем запускать дочернюю корутину в новом фоновом потоке, если родительская корутина и так уже выполняется в фоновом потоке.
 * Можно было бы всю работу сделать в родительской корутине и не плодить потоки.

 * И вот тут я вам должен сказать очень интересную вещь, которая, может поменять ваше текущее представление о корутинах.
 * Постарайтесь понять следующее утверждение, т.к. это очень важно:

 * Корутина может выполняться в main потоке.

 * Выше рассмотренный пример станет выглядеть гораздо более осмысленным и полезным, если код родительской корутины выполняется в main потоке,
 * а код дочерней - в фоновом. Получается, что из main потока мы запустили фоновую работу и подождали ее окончания без каких-либо блокировок и колбэков.
 * join не заблокирует main поток, а только приостановит
 * */
suspend fun nestedCoroutineExample2() = coroutineScope {
    launch {
        println("parent coroutine, start")

        val job = launch {
            println("child coroutine, start")
            delay(1000)
            println("child coroutine, end")
        }

        println("parent coroutine, wait until child completes")
        job.join() //в этом случае будет suspend, а не блокировка потока

        println("parent coroutine, end")
    }
}

/**
 * Если возникает вопрос, почему то же самое нельзя без родительской корутины провернуть, то вспомните, что join - это suspend функция, которая не
 * может быть вызвана вне scope
 * */

/**
 * В следующем примере показан пример паралельной работы дочерних корутин.
 * Родительская корутина будет их ждать. Общее время выполнения будет 1500
 * */
suspend fun parallelWorkExample() = coroutineScope {
    launch {
        val begin = System.currentTimeMillis()
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

        val end = System.currentTimeMillis()

        println("parent coroutine, end")

        println("Elapsed time in miliseconds: ${end - begin}")
    }
}

/** Родительская корутина выполнится первой, но будет иметь статус isActive = true до тех пор пока дочерние корутины выполняются.
 * */
suspend fun relationBetweenParentAndChild() = coroutineScope {
    val job = launch {
        println("parent start")
        launch {
            println("child start")
            delay(1000)
            println("child end")
        }
        println("parent end")
    }

    launch {
        delay(500)
        println("parent job is active: ${job.isActive}")
        delay(1000)
        println("parent job is active: ${job.isActive}")
    }
}

/**
 * Можно переопределять контекст дочерних корутин путем передачи других параметров.
 * */