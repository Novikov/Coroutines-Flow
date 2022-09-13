package coroutines.task

import kotlinx.coroutines.*

fun main() {
    GlobalScope.launch {
        println("start")
        launch { println("a") }
        launch { println("b") }
        println("end")
        delay(5000)
    }

    Thread.sleep(2000L)

}