package Flow

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlin.system.measureTimeMillis

suspend fun main() {
//    bufferingExample1()
//    bufferingExample2()
    bufferingExample3()
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

/** Еще один способ ускорить обработку. Каждый раз при долгой обработке будет происходить отмена flow и рестарт с последнего значения.
 * Похоже что такое поведение будет всегда когда обработка длиться больше чем выпуск на 2-3 милисекунды*/
suspend fun bufferingExample3() = coroutineScope {
    val time = measureTimeMillis {
        simpleBufferingFlow()
            .collectLatest { value -> // cancel & restart on the latest value
                println("Collecting $value")
                delay(150) // pretend we are processing it for 300 ms
                println("Done $value")
            }
    }
    println("Collected in $time ms")
}