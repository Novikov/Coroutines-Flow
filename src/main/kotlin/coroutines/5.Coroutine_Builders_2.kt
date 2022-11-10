package coroutines

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

suspend fun main() {
//    coroutineBuilders2Example1()
//    coroutineBuilders2Example2()
    coroutineBuilders2Example3()
}

/** Полезная техника маппинга списка через async
 * Позволяет выполнить асинхронный запрос на каждом элементе списка и возвращает в результате список с обновленными данными.
 * */
suspend fun coroutineBuilders2Example1() = coroutineScope {
    val idList = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    val measuredTime = measureTimeMillis {
        val dataList = idList.toMutableList().map { async { getDataById(it) } }.awaitAll()
        println(dataList)
    }
    println(measuredTime)
}

suspend fun getDataById(id: Int): String {
    delay(1000)
    return "Data by id - $id"
}

/** Тоже самое можно сделать с помощью launch
 * Здесь есть отличие от async в том, что если в launch передать start параметр CoroutineStart.Lazy - то асинхронная работа прекратится и все будет
 * выполняться последовательно. Это происходит потому что в launch в это случае Lazy старта необходимо руками вызвать start() на экземпляре job.
 * В Async start вызовется без нас при вызове await. Соответственно чтоб работало нужно руками вызывать start. Смотри закоментированный пример.
 * */
suspend fun coroutineBuilders2Example2() = coroutineScope {
    val idList = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    val measuredTime = measureTimeMillis {
        idList.toMutableList().map { launch { makeRequestByValue(it) } }.joinAll()
//        idList.toMutableList().map { launch(start = CoroutineStart.LAZY) { makeRequestByValue(it) }.apply { start() } }.joinAll()
    }
    println(measuredTime)
}

suspend fun makeRequestByValue(id: Int) {
    delay(1000)
    println("request for id $id")
}

/** Если мы сделаем 3 корутины с lazy запуском и будем в привычной манере вызывать await - потеряется асинхронное поведение. Время выполнения каждой
 * корутины будет складываться. Это можно исправить, вызвая на каждом async start до того как вызываем await.*/
suspend fun coroutineBuilders2Example3() = coroutineScope {
    val measuredTime = measureTimeMillis {
        val def1 = async(start = CoroutineStart.LAZY) { getDataById(1) }
        val def2 = async(start = CoroutineStart.LAZY){ getDataById(2) }
        val def3 = async(start = CoroutineStart.LAZY){ getDataById(3) }
//        val def1 = async(start = CoroutineStart.LAZY) { getDataById(1) }.apply { start() }
//        val def2 = async(start = CoroutineStart.LAZY) { getDataById(2) }.apply { start() }
//        val def3 = async(start = CoroutineStart.LAZY) { getDataById(3) }.apply { start() }
        println("val1: ${def1.await()}, val2: ${def2.await()}, val3: ${def3.await()}")
    }
    println(measuredTime)
}