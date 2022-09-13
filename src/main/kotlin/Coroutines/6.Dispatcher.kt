package Coroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

suspend fun main() {
//    checkThread()
    setDispatcher()
}


/**
 * Очень важный момент касательно разделения потоков. Код внутри launch будет выполняться на собственном потоке, а код внутри coroutineScope
 * на отдельном.
 * */
suspend fun checkThread() = coroutineScope {
    launch {
        println("Корутина выполняется на потоке: ${Thread.currentThread().name}")
    }
    println("Функция checkThread выполняется на потоке: ${Thread.currentThread().name}")
}

/**
 * Мы сами можем задать диспатчер передав его в билдер
 * */
suspend fun setDispatcher() = coroutineScope() {
    launch(Dispatchers.Default) {   // явным образом определяем диспетчер Dispatcher.Default
        println("Корутина выполняется на потоке: ${Thread.currentThread().name}")
    }
    println("Функция main выполняется на потоке: ${Thread.currentThread().name}")
}