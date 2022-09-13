package coroutines

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

suspend fun main() = coroutineScope{

    launch{
        println("Outer coroutine")
        launch{
            println("Inner coroutine")
            delay(400L)
        }
    }

    println("End of Main")
}