package coroutines

import kotlinx.coroutines.*

fun main() {
//    example1()
//    println("after example 1")

//    example2()

    example3()
}

/** Блокирует поток выполнения*/
 fun example1() = runBlocking {
    launch {
        println("start")
        launch { println("a") }
        launch { println("b") }
        println("end")
    }
}

/** Предсказуемый порядок запуска. Сначала выполнение parent, затем child*/
fun example2() = runBlocking {
    launch {
        println("start 1")
        launch { println("1.1") }
        launch { println("1.2") }
        println("end 1")
    }

    launch {
        println("start 2")
        launch { println("2.1") }
        launch { println("2.2") }
        println("end 2")
    }
}

/** Непредсказуемый порядок*/
fun example3() {
    val scope = CoroutineScope(SupervisorJob())
    scope.launch {
        println("First launch start")
        launch {
            println("1.1 inner launch start")
        }
        launch {
            println("1.2 inner launch start")
        }
        launch {
            println("1.3 inner launch start")
        }
        println("First launch end")
    }

    scope.launch {
        println("Second launch start")
        launch {
            println("2.1 inner launch start")
        }
        launch {
            println("2.2 inner launch start")
        }
        launch {
            println("2.3 inner launch start")
        }
        println("Second launch end")
    }

    Thread.sleep(5000)
}
