package coroutines

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

suspend fun main() {
    channelExample1()
//    channelExample2()
//    channelExample3()
}

/** Simple Channel*/
suspend fun channelExample1() = coroutineScope {
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
 * Closing chanell
 * Чтобы указать, что в канале больше нет данных, его можно закрыть с помощью метода close().
 * Если для получения данных из канала применяется цикл for, то, получив сигнал о закрытии канала,
 * данный цикл получит все ранее посланные объекты до закрытия и завершит выполнение:
 * */

suspend fun channelExample2() = coroutineScope {

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

/**
 * Канал используется, как средство передачи данных между корутинами.
 * Т.е. он является ячейкой, куда одна корутина может поместить данные, а другая корутина - взять их оттуда.
 * Канал - потокобезопасен и нам не надо самим возиться с блокировками и синхронностью.
 * */

suspend fun channelExample3() = coroutineScope {
    val channel = Channel<Int>()

    launch {
        channel.send(5)
    }

    launch {
        val i = channel.receive()
        println(i)
        channel.close()
    }

    delay(2000)
}

/**
 * Эти методы являются suspend функциями. Они могут отработать мгновенно, а могут и приостановить корутину на какое-то время.
 * Это зависит от того, какой из них был вызван раньше.
 * Если первая корутина пытается отправить данные методом send, но вторая корутина еще не вызвала receive, то метод send приостановит первую корутину и будет ждать.
 * Аналогично наоборот. Если вторая корутина уже вызвала receive, чтобы получить данные, но первая корутина еще не отправила их, то метод receive приостановит вторую корутину.
 * Примеры в уроках про channels.
 * */

/**
 * Так же нужно помнить о том, что есть ньюансы с приостановкой функций отправки и получения. Если будет несовпадение в количестве отправлений
 * или количестве приемов - то в зависимости от этого может навсегда приостановиться первая или вторая функция
 * */

/**
 * Сlose
 * Очевидно, что корутина получатель не может всегда заранее знать о количестве элементов, которые будут отправлены в канал.
 * Хорошо бы дать отправителю возможность сообщать о том, что он закончил передавать данные.
 * Получив такой сигнал получатель больше не вызывал бы метод receive, который приостановит корутину, но никогда ничего не вернет.
 * */

