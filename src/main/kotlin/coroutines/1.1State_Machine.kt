package coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main(){
    stateMachineExample()
    stateMachineExample2()
}

//Tools -> Kotlin -> ShowBytecode
fun stateMachineExample() = runBlocking {
    println("Coroutine start")
    delay(100)
    println("coroutineEnd")
}

fun stateMachineExample2() = runBlocking {
    println("Coroutine start")
    delay(100)
    println("bcg work")
    delay(100)
    println("coroutineEnd")
}

/**
 * Принцип разделения следующий
 * 1)В Case 0 - попадет код до первого suspend point
 * 2)В Case 1, 2, 3...n попадет код suspend point
 * 3)все что после suspend point выносится отдельно и не попадает в state machine
 * */