package Coroutines.builders

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() {
    example_2()
}

//Блокировать поток ненужно.Билдер это делает за нас и все выполняется синхронно

fun example_2() {
    runBlocking {
        delay(1000L)
        println("World!")
    }
    runBlocking {
        delay(1000L)
        println("World!")
    }
    runBlocking {
        delay(1000L)
        println("World!")
    }
    println("Hello,")
}