package coroutines.task

import kotlinx.coroutines.*

suspend fun main() {
    example1()
}

suspend fun example1() = coroutineScope {
    launch {
        println("start")
        launch { println("a") }
        launch { println("b") }
        println("end")
    }
}