package coroutines

import kotlinx.coroutines.*

suspend fun main(){
    coroutineBuilders2Example1()
}

/** Полезная техника маппинга списка через async
 * Позволяет выполнить асинхронный запрос на каждом элементе списка и возвращает в результате список с обновленными данными.
 * */
suspend fun coroutineBuilders2Example1() = coroutineScope {
    val idList = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    val dataList = idList.toMutableList().map { async { getDataById(it) } }.awaitAll()
    println(dataList)
}

suspend fun getDataById(id: Int): String {
    delay(1000)
    return "Data by id - $id"
}