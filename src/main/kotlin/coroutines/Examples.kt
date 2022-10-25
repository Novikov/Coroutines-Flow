package coroutines

import kotlinx.coroutines.*

suspend fun main() {
    example1()
//    example2()
}

/**
 * Если не вызывать join на дочерних корутинах то родительская ждать их не будет.
 * Но a напечатается быстрее end просто потому что launch отрабатывает быстрее чем родительская корутина.
 * Если туда добавить delay то родительская корутина отработает быстрее. Но нужно помнить, что если родительскаяа корутина отработала быстрее
 * это не значит что она отменена.
 *
 * Обсудить
 * */
suspend fun example1() = coroutineScope {
    launch {
        println("start")
        launch { println("a") }
        launch { println("b") }
        println("end")
    }
}

/**
 * Без использования join это все будет выполняться в разнобой. Корутины не блокируют друг друга.
 * */
suspend fun example2() = coroutineScope {
    val scope = CoroutineScope(SupervisorJob())
    scope.launch {
        println("First launch start")
        launch {
            println("1.1 inner launch start")
            delay(300)
            println("1.1 inner launch end")
        }
        launch {
            println("1.2 inner launch start")
            delay(300)
            println("1.2 inner launch end")
        }
        launch {
            println("1.3 inner launch start")
            delay(300)
            println("1.3 inner launch end")
        }
        println("First launch end")
    }

    scope.launch {
        println("Second launch start")
        launch {
            println("2.1 inner launch start")
            delay(300)
            println("2.1 inner launch end")
        }
        launch {
            println("2.2 inner launch start")
            delay(300)
            println("2.2 inner launch end")
        }
        launch {
            println("2.3 inner launch start")
            delay(300)
            println("2.3 inner launch end")
        }
        println("Second launch end")
    }

    scope.launch {
        println("Third launch start")
        launch {
            println("3.1 inner launch start")
            delay(300)
            println("3.1 inner launch end")
        }
        launch {
            println("3.2 inner launch start")
            delay(300)
            println("3.2 inner launch end")
        }
        launch {
            println("3.3 inner launch start")
            delay(300)
            println("3.3 inner launch end")
        }
        println("Third launch end")
    }
    delay(3000)
}
