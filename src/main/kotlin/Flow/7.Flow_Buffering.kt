package Flow

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlin.system.measureTimeMillis

suspend fun main() {
//    bufferingExample1()
    bufferingExample2()
}

suspend fun simpleBufferingFlow() = flow {
    for (i in 1..10) {
        delay(100) // pretend we are asynchronously waiting 100 ms
        emit(i) // emit next value
    }
}

/** На время получения элемента flow влияет как задержка при эмиссии + задержка при потреблении */
suspend fun bufferingExample1() = coroutineScope {
    val time = measureTimeMillis {
        simpleBufferingFlow()
            .buffer() // приведет кк тому что код эмиссии и потребления будет вызываться одновременно. Это экономит 100мс на выпуск
            .collect { value ->
                delay(300)
                println(value)
            }
    }
    println("Collected in $time ms")
}

suspend fun bufferingExample2() = coroutineScope {
    val time = measureTimeMillis {
        simpleBufferingFlow()
            .conflate() //Пропустит некоторые значения если их обработка будет занимать длительное время
            .collect { value ->
                delay(500)
                println(value)
            }
    }
    println("Collected in $time ms")
}