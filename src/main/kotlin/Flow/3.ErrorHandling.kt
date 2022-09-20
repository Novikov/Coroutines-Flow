package Flow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

suspend fun main() {
//    handleErrorExample1()
//    handleErrorExample2()
    handleErrorExample3()
//    handleErrorExample4()
}

suspend fun getErrorFlow(): Flow<String> {
    return flow {
        delay(500)
        emit("1")
        delay(500)
        emit("2")

        val a = 1 / 0

        delay(500)
        emit("3")
        delay(500)
        emit("4")
    }
}

/** Пример без обработки ошибки * */
suspend fun handleErrorExample1() {
    getErrorFlow().collect {
        println(it)
    }
}

/** Обработка Exception через try-catch. Это сработает, но внутри flow есть встроенные операторы обработки ошибок*/
suspend fun handleErrorExample2() {
    try {
        getErrorFlow().collect {
            println(it)
        }
    } catch (ex: Exception) {
        println("Exception has been thrown")
    }
}

/** Обработка ошибки через оператор catch. Внутри catch можно использовать функцию emit для выпуска значения в случае ошибки.
 * flow все ровно завершится, но последнее значение будет из функции emit.
 * */
suspend fun handleErrorExample3() {
    getErrorFlow()
        .catch {
            println("Exception has been thrown")
            emit("another value")
        }
        .collect {
            println(it)
        }
}

/**
 * Можно добавить несколько операторов catch. Один будет ловить исключения из flow, другой из map, но из map у меня не отрабатывает.
 * */

suspend fun handleErrorExample4() {
    getErrorFlow()
        .catch { println("Exception has been thrown") }
        .map { it.toInt() }
        .catch { println("Exception in map has been thrown") }
        .collect {
            println(it)
        }
}
