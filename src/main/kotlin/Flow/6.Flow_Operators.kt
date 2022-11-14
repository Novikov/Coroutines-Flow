package Flow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform

/**
 * RxJava operators analogs
 * flatMapMerge - flatMap
 * flatMapConcat - concatMap
 * flatMapLatest - switchMap
 * combine - combineLatest
 * drop - skip
 * catch - onError
 * ...
 * */

suspend fun main() {
//    flowOperatorsExample1()
    flowOperatorsExample2()
}

suspend fun performRequest(request: Int): String {
    delay(1000) // imitate long-running asynchronous work
    return "response $request"
}

/**Transform похож на оператор map, но более глобальный*/
suspend fun flowOperatorsExample1() {
    (1..3).asFlow() // a flow of requests
        .transform { request ->
            emit("Making request $request")
            emit(performRequest(request))
        }
        .collect { response -> println(response) }
}

/**Все операции flow выполняются последовательно (событие проходит либо до терминального оператора, либо фильтруется)
 * Посмотри порядок вывода.
 * */
suspend fun flowOperatorsExample2(){
    (1..5).asFlow()
        .filter {
            println("Filter $it")
            it % 2 == 0
        }
        .map {
            println("Map $it")
            "string $it"
        }.collect {
            println("Collect $it")
        }
}

