package coroutines

import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun main() {
//    checkThread()
//    setDispatcher()
    getDataOnUnconfinedDispatcherExample()
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

/**
 * При указании Dispatcher мы определяем границы потоков в которых будет выполняться работа.
 * Default - число потоков ограничено ядрами процессора
 * IO - Число потоков до 64 или числу ядер процессора
 * Default используем для высоконагруженных операций, IO не требуют больших затрат CPU поэтому мы их можем создать больше количество.
 * Они не будут ждать друг друга. Если мы будем выполнять тяжелые вычисления в IO то это создаст большую нагрузку на CPU.
 * Важный момеент - при завершении операции. Т.е когда придет ответ - корутина может сменить поток.
 */


/** Main dispatcher можно использовать для выполнения операций на главном потоке. Т.к suspend функции не блокируют поток то это не будет
 * тормозить UI. Если мы возовем continuation.resume - то все будет ок.
 * Смены потока при завершении операции не будет т.к у Main диспатчера только один поток.
 * Код ниже не запустится т.к Main диспатчер есть только в Android.
 * */
private suspend fun getDataSafe(): String =
    suspendCoroutine {
        println("background work ${Thread.currentThread().name}")
        TimeUnit.MILLISECONDS.sleep(3000)
        it.resume("Data")
    }
//
//suspend fun getDataOnMainExample(){
//    val scope = CoroutineScope(Dispatchers.Main)
//
//    scope.launch {
//        val data = getData()
//        updateUI(data)
//    }
//}

suspend fun getDataOnUnconfinedDispatcherExample() {
    val scope = CoroutineScope(Dispatchers.Unconfined)

    scope.launch() {
        println("start coroutine ${Thread.currentThread().name}")
        val data = getData()
        println("end coroutine ${Thread.currentThread().name}")
    }
}

