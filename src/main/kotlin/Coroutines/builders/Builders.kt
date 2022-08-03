package Coroutines.builders

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//Необходимы чтобы разрешить невозможность вызова suspend функции в обычной функции

fun main() {
    GlobalScope.launch {
        delay(1000L)
        println("World!")
    }
    Thread.sleep(2000L)
}