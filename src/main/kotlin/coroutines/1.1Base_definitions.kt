package coroutines

import kotlinx.coroutines.*

suspend fun main(){
    routineExample()
//    coroutineExample()
}

/**
 * In computer programming, a function or subroutine (when it doesn't return a value) is a sequence of program instructions that performs a specific task,
 * packaged as a unit. This unit can then be used in programs wherever that particular task should be performed.
 * Метод это понятие связанное с классом и ООП.
 * */


/** Когда происходит вызов routine1 - control flow передается в routine1. После выполнения subroutine поток выполнения передается обратно в routineExample() */
fun routineExample(){
    println("main starts")
    routine(1,300)
    routine(2,500)
    println("main ends")
}

fun routine(number: Int, delay: Long){
    println("Routine $number starts work")
    Thread.sleep(delay)
    println("Routine $number has finished")
}

/** В данном случае control flow передастся в coroutine1, но в тот момент когда он дойдет до suspend функции delay - поток выполнения будет отдан для coroutine2
 * coroutine2 запустится до того, выполнится coroutine1*/
suspend fun coroutineExample() = coroutineScope{
    println("main starts")
    joinAll(
        launch{coroutine(1,500)},
        launch{coroutine(2,300)}
    )
    println("main ends")
}

suspend fun coroutine(number: Int, delay: Long){
    println("Routine $number starts work")
    delay(delay)
    println("Routine $number has finished")
}