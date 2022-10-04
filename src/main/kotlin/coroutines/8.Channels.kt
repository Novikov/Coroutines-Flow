package coroutines

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

suspend fun main() {
    simpleChannel()
//    closingChannelExample()
}

suspend fun simpleChannel() = coroutineScope {
    val channel = Channel<String>()
    launch {
        val users = listOf("Tom", "Bob", "Sam")
        for (user in users) {
            channel.send(user)  // Отправляем данные в канал
        }
        channel.close()  // Закрытие канала
    }

    repeat(3) {
        val number = channel.receive()
        println(number)
    }
    println("End")
}

/**
 * Чтобы указать, что в канале больше нет данных, его можно закрыть с помощью метода close().
 * Если для получения данных из канала применяется цикл for, то, получив сигнал о закрытии канала,
 * данный цикл получит все ранее посланные объекты до закрытия и завершит выполнение:
 * */

suspend fun closingChannelExample() = coroutineScope {

    val channel = Channel<String>()
    launch {
        val users = listOf("Tom", "Bob", "Sam")
        for (user in users) {
            channel.send(user)  // Отправляем данные в канал
        }
        channel.close()  // Закрытие канала
    }

    for (user in channel) {  // Получаем данные из канала
        println(user)
    }
    println("End")
}