package coroutines

import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun main() {
//    checkThread()
//    setDispatcher()
    dispatcherExample3()
//    getDataOnUnconfinedDispatcherExample()
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

/** Мы сами можем задать диспатчер передав его в билдер*/
suspend fun setDispatcher() = coroutineScope {
    launch(Dispatchers.Default) {
        // явным образом определяем диспетчер Dispatcher.Default
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
 * тормозить UI.
 * Смены потока при завершении операции не будет т.к у Main диспатчера только один поток.
 * Т.е мы запускаем операцию на главном потоке. Например, запрос в сеть. Функция приостановится, дожидаясь результата. В это время UI поток
 * продолжит заниматься отрисовкой UI. После прихода результата - функция возобновляется и выполняет операции на главном потоке.
 * */


/**
 * Unconfined dispatcher
 * При старте корутины происходит проверка isDispatcherNeeded. Для корутин с использованием других диспатчеров этот папраметр установлен в
 * true. Для Unconfined dispatcher данная проверка false. Если isDispatched true то вызов continuation.resume() будет происходить по ссылыке
 * DispatchedContinuation. В этом случае может произойти смена потока т.к каждый Dispatcher использует разный пул ппотоков и в момент
 * вызова resume - поток на котором происходил запуск может быть занят и завершение проихойдет на другом потоке. В случае unconfined
 * завершение будет происходить на том потоке, на котором выполнянась работа.
 * */

/** Методы Dispatcher
 * 1)isDispatcherNeeded() - возвращает true если работа корутины должно быть выполнена с помощью dispatch метода. Все диспатчеры кроме unconfined
 * вернут true. Соответственно выполнение Unconfined диспатчера произойдет в этом же потоке.
 * 2)dispatch() - отвечает за выполнение переданного callback в другом потоке. Он вызовется самостоятельно
 * */
@OptIn(ExperimentalStdlibApi::class)
suspend fun dispatcherExample3() = coroutineScope {
    launch {
        println("${this.coroutineContext[CoroutineDispatcher]?.isDispatchNeeded(this.coroutineContext)}")
    }.join()
}

@OptIn(ExperimentalStdlibApi::class)
suspend fun getDataOnUnconfinedDispatcherExample() {
    val scope = CoroutineScope(Dispatchers.Unconfined)

    scope.launch() {
        println("${this.coroutineContext[CoroutineDispatcher]?.isDispatchNeeded(this.coroutineContext)}")
        println("start coroutine ${Thread.currentThread().name}")
        val data = getData3()
        println("end coroutine ${Thread.currentThread().name}")
    }

    delay(3000)
}

private suspend fun getData3(): String =
    suspendCoroutine {
        println("suspend coroutine start ${Thread.currentThread().name}")
        thread {
            println("background work ${Thread.currentThread().name}")
            TimeUnit.MILLISECONDS.sleep(1000)
            it.resume("Data")
        }
    }

