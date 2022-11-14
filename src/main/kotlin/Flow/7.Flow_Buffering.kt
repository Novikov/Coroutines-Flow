package Flow

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow
import kotlin.system.measureTimeMillis

suspend fun main(){
    bufferingExample1()
}

suspend fun simpleBufferingFlow()= flow {
    for (i in 1..3) {
        delay(100) // pretend we are asynchronously waiting 100 ms
        emit(i) // emit next value
    }
}

/** На время получения элемента flow влияет как задержка при эмиссии + задержка при потреблении */
suspend fun bufferingExample1() = coroutineScope{
    val time = measureTimeMillis {
        simpleBufferingFlow()
            .buffer() // приведет кк тому что код эмиссии и потребления будет вызываться одновременно. Это экономит 100мс на выпуск
            .collect { value ->
            delay(300) // pretend we are processing it for 300 ms
            println(value)
        }
    }
    println("Collected in $time ms")
}