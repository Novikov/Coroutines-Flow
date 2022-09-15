package coroutines

import kotlinx.coroutines.*

suspend fun main() {
//    getNecessaryDataOuterTryCatch()
//    getNecessaryDataInnerTryCatch()
    runCatchingExample()
}

suspend fun getFailingData(): Int {
    delay(100)
    throw RuntimeException("Request Failed")
    return 5
}

suspend fun getData(): Int {
    delay(100)
    return 10
}

/**
 * Не отработает т.к исключение не пробросится, а передастся на вершину иерархии (Это называется structured concurrency)
 * */
suspend fun getNecessaryDataOuterTryCatch(): Int = coroutineScope {
    try {
        val failingDataDeferred = async { getFailingData() }
        val successDataDeferred = async { getData() }
        failingDataDeferred.await().plus(successDataDeferred.await())
    } catch (ex: Exception) {
        10
    }
}

/**
 * В данном случае отработает т.к исключение отлавливается внутри async
 * */
suspend fun getNecessaryDataInnerTryCatch(): Int = coroutineScope {
    val failingDataDeferred = async {
        try {
            getFailingData()
        } catch (ex: java.lang.Exception) {
            10
        }
    }
    val successDataDeferred = async { getData() }
    failingDataDeferred.await().plus(successDataDeferred.await())
}

/**
 * В таком виде exception не отловится. Нужно убирать async.
 * Обработка ошибки через передачу в launch coroutineExceptionHandler будет работать только для viewModelScope. Нужно узнать почему.
 * Нужно помнить что установка обработчика в дочерней корутине работать не будет.
 * */
suspend fun runCatchingExample() = coroutineScope {
    launch {
        kotlin.runCatching {
            val failingDataDeferred = async { getFailingData() }
            val successDataDeferred = async { getData() }
            failingDataDeferred.await().plus(successDataDeferred.await())
        }
            .onSuccess { println("Success") }
            .onFailure { println("Failure") }
    }
}
