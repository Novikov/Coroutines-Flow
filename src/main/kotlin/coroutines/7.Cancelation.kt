package coroutines

import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.ContinuationInterceptor

suspend fun main() {
//    coroutineCancellationExample()
//    catchExternalCancellationInLaunch()
//    catchExternalCancellationInAsync()
    timeOutExample()
}

/**
 * Есть 2 варианта отмены корутины:
 * 1.Через job. 1.1Отменится моментально если внутри есть suspend функция, например delay. Это случится потому что в этом случае будет генериться CancelationException.
 * Данный exception можно поймать с помощью try catch, но это не обязательно. Если не обработаем - краша не будет т.к данный тип исключения обрабатывается
 * с помощью внутренних механизмов.
 *
 * 1.2 Если внутри нет функций который генерят cancelable exception -
 * отмена не произойдет. В это случае поменяется только статус у coroutineScope. Чтобы работа внутри launch не выполнялась -
 * необходимо вручную проверять статус coroutineScope. Это можно посмотреть в Android примере.
 *
 * 2.Через scope. В этом случае произойдет моментальная отмена.
 * */

suspend fun coroutineCancellationExample() = coroutineScope {
    val downloader: Job = launch {
        println("Начинаем загрузку файлов")
        for (i in 1..10) {
            if (isActive) { //другой способ вызвать yield() вместо условия
                println("Загружен файл $i ${coroutineContext[ContinuationInterceptor]}")
                TimeUnit.MILLISECONDS.sleep(1000) //поменять на delay(1000)
            }
        }
    }
    delay(800L)     // установим задержку, чтобы несколько файлов загрузились
    println("Надоело ждать, пока все файлы загрузятся. Прерву-ка я загрузку...")
    downloader.cancel()    // отменяем корутину
    downloader.join()      // ожидаем завершения корутины (в данном случае эта строчка кода никак не повлияет на результата потому что операция не ресурсозатратная)
    println("Работа программы завершена")
}

/**
 * CancelAndJoin() нужен для того чтобы подождать пока завершится корутина. В офф доке есть пример где эта строчка кода заставляет ждать пока выполнится
 * finaly блок у try-catch. https://kotlinlang.org/docs/cancellation-and-timeouts.html#closing-resources-with-finally
 * */

/**
 * Методы cancel() и join() можно заменить одним методом - cancelAndJoin()
 * */

/**
 * Обработка исключения CancellationException
 * Все suspend-функции в пакете kotlinx.coroutines являются прерываемыми (cancellable).
 * Это значит, что они проверяют, прервана ли корутина. И если ее выполнение прервано, они генерируют исключение типа CancellationException.
 * И в самой корутине мы можем перехватить это исключение, чтобы обработать отмену корутины.
 * Именно поэтому в данном случае не нужно делать проверку на isActive.
 *
 * Обработать внешнее прирывание операции можно следующим образом:
 * */

suspend fun catchExternalCancellationInLaunch() = coroutineScope {
    val downloader: Job = launch {
        try {
            println("Начинаем загрузку файлов")
            for (i in 1..5) {
                println("Загружен файл $i")
                delay(500L)
            }
        } catch (e: CancellationException) {
            println("Загрузка файлов прервана потому что ${e.message}")
        } finally {
            println("Загрузка завершена")
        }
    }
    delay(800L)
    println("Надоело ждать. Прерву-ка я загрузку...")
    downloader.cancelAndJoin()    // отменяем корутину и ожидаем ее завершения
    println("Работа программы завершена")
}

/**
 * Если в примере выше в блоке finally вызвать какую cancelable функцию - например delay - она вызовет cancelable exception внутри finally.
 * Из-за этого может не выполнится некоторая работа которую мы ожидаем. Чтобы этого не произошло можно обернуть выполняемый внутри finaly код
 * withContext(NonCancellable)
 * */

/**
 * Подобным образом можно отменять выполнение и корутин, создаваемых с помощью функции async().
 * В этом случае обычно вызов метода await() помещается в блок try
 * */

suspend fun catchExternalCancellationInAsync() = coroutineScope {

    // создаем и запускаем корутину
    val message = async {
        getMessage2()
    }
    // отмена корутины
    message.cancelAndJoin()

    try {
        // ожидаем получение результата
        println("message: ${message.await()}")
    } catch (e: CancellationException) {
        println("Coroutine has been canceled")
    }
    println("Program has finished")
}

suspend fun getMessage2(): String {
    delay(500L)
    return "Hello"
}

/**
 * Отмена родительского scope отменяет все дочерние.
 * Отмена рядового job не влияет на его siblings
 * */

/**
 * Существует 2 типа suspend функций: обычные и отменяемые.
 * Первая создается через suspendCoroutine билдер и не реагируют на отмену корутины. Насколько я понял речь идет о launch.
 * Вторая создается через suspendCancellableCoroutine и имеет внутренний колбэк для прекращения работы suspend фукнции который вызовется
 * в случае отмены. Подробности можно посмотреть здесь https://startandroid.ru/ru/courses/kotlin/29-course/kotlin/611-urok-16-korutiny-otmena-kak-oshibka.html
 * */


/**
 * Есть специальная функция которая генерит наследника cancelation exception если выполнение корутины занимает больше по времени, чем наши ограничения.
 * Есть аналог который возвращает вместо CancelationException - null (withTimeoutOrNull())
 * */
suspend fun timeOutExample() = coroutineScope {
    launch {
        withTimeout(1300L) {
            repeat(1000) { i ->
                println("I'm sleeping $i ...")
                delay(500L)
            }
        }
    }
}

