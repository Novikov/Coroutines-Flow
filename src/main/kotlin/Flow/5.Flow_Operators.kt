package Flow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
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
    flowOperatorsExample1()
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