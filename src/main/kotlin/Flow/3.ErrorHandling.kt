package Flow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.io.IOException
import java.lang.ArithmeticException

suspend fun main() {
    handleErrorExample1()
//    handleErrorExample2()
//    handleErrorExample3()
//    handleErrorExample4()
//    handleErrorExample5()
//    handleErrorExample6()
//    handleErrorExample7()
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
 * Catch срабатывает только для предшествующих ему операторов.
 * Можно добавить несколько операторов catch. Один будет ловить исключения из flow, другой из map.
 * */

suspend fun handleErrorExample4() {
    getErrorFlow()
        .catch {
            println("Exception has been thrown")
            emit("3")
        }
        .map {
            if (it == "3") {
                it.toInt() / 0
            } else {
                it
            }

        }
        .catch {
            println("Exception in map has been thrown")
            emit(0)
        }
        .collect {
            println(it)
        }
}

/**
 * Retry
 * Перезапустит Flow в случае ошибки. Так же как и catch сработает только для операторов выше.
 * */

suspend fun handleErrorExample5() {
    getErrorFlow()
        .retry(2)
        .collect {
            println(it)
        }
}

/**
 * Можно добавить дополнительную логику для добавления паузы дальнейшего повтора.
 * */
suspend fun handleErrorExample6() {
    getErrorFlow()
        .retry(2) {
            //В данном блоке можно обработать Exception каким либо специфическим образом.
            //Нужно вернуть boolean value по которому дальше будет происходить определение продолжать повтор (true) или пробросить исключения далее (false)
            if (it is ArithmeticException) {
                delay(5000)
                true
            } else {
                false
            }
        }
        .collect {
            println(it)
        }
}

/**
 * В этот оператор не передается количество повторов. Вместо этого условие повтора. Если - true перезапускаем flow{}
 * */
suspend fun handleErrorExample7() {
    getErrorFlow()
        .retryWhen { cause, attempt ->
            cause is ArithmeticException && attempt < 5
        }
        .collect {
            println(it)
        }
}