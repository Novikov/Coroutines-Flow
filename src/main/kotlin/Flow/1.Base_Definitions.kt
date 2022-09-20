package Flow

import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.internal.ChannelFlow
import kotlinx.coroutines.launch

suspend fun main() {
//    flowExample1()
//    flowExample2()
//    flowExample3()
    flowExample4()
}

/**
 * Обычно suspend функция возвращает нам одно значение. И пока мы ждем это значение, корутина приостанавливается.
 * Flow позволяет расширить это поведение. Он делает так, что мы можем получать последовательность (поток) данных вместо одного значения.
 * И это будет происходит в suspend режиме.Т.е. корутина будет приостанавливаться на время ожидания каждого элемента.
 * */
suspend fun flowExample1() {
    getData().onEach { println(it) }.collect()
}

fun getData(): Flow<Int> {
    return flow {
        for (i in 0..10) {
            emit(i)
        }
    }
}

/**
 * Flow является холодным источником данных. Он для каждого получателя будет генерировать данные заново.
 * */

/**
 * Выпуск значений flow должен быть в одной корутине иначе будет crash. Пример ниже упадет.
 * */
suspend fun flowExample2() {
    wrongWayGetData().onEach { println(it) }.collect()
}

fun wrongWayGetData(): Flow<Int> {
    return flow {
        coroutineScope {
            launch {
                delay(1000)
                emit(1)
            }

            launch {
                delay(1000)
                emit(2)
            }

            launch {
                delay(1000)
                emit(3)
            }
        }
    }
}

/**
 * Основная проблема, с которой мы столкнулись в прошлом примере - это то, что в блоке flow нельзя вызывать emit из другой корутины.
 * Т.е. можно запустить другую корутину и создать там данные, а вот отправить их оттуда получателю методом emit нельзя, т.к., тем самым, мы меняем корутину, в которой будет запущен код получателя (блок collect).
 * Это значит, что нам надо вызов emit вернуть в нашу текущую корутину.
 * Есть способ решить данную проблему. Для этого используется channelFlow.
 * */

suspend fun flowExample3() {
    getDataRightWay().onEach { println(it) }.collect()
}

suspend fun getDataRightWay(): Flow<Int> {
    return flow {
        coroutineScope {
            val channel = produce<Int> {
                launch {
                    delay(1000)
                    send(1)
                }
                launch {
                    delay(1000)
                    send(2)
                }
                launch {
                    delay(1000)
                    send(3)
                }
            }
            channel.consumeEach {
                emit(it)
            }
        }
    }
}

/**
 * Но есть внутренний оператор, который позволит добиться того же еффекта что и при использовании Channel - ChannelFlow.
 * */

suspend fun flowExample4() {
    getDataChannelFlow().onEach { println(it) }.collect()
}

suspend fun getDataChannelFlow(): Flow<Int> {
    return channelFlow {
        launch {
            delay(1000)
            send(1)
        }
        launch {
            delay(1000)
            send(2)
        }
        launch {
            delay(1000)
            send(3)
        }
    }
}