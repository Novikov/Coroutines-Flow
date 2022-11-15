package coroutines

import kotlinx.coroutines.*

suspend fun main() {
    example1()
//    example2()
}

suspend fun example1() = runBlocking {
    launch {
        println("start")
        launch { println("a") }
        launch { println("b") }
        println("end")
    }
}

suspend fun example2() = runBlocking {
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

    launch {
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

    launch {
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
    delay(5000)
}
