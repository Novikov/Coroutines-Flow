package coroutines

import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

suspend fun main() {
//    coroutineCancellationExample()
//    catchExternalCancellationInLaunch()
    blockingTest()
}

/**
 * Есть 2 варианта отмены корутины:
 * 1.Через job. 1.1Отменится моментально если внутри есть suspend функция, например delay. Это случится потому что в этом случае будет генериться CancelationException.
 * Данный exception можно поймать с помощью try catch, но это не обязательно. Если не обработаем - краша не будет т.к данный тип исключения обрабатывается
 * с помощью внутренних механизмов.
 * 1.2 Если вместо delay будет не suspend функция, например TimeUnit.MILLISECONDS.sleep(1000), -
 * отмена не произойдет. В это случае поменяется только статус у coroutineScope. Чтобы работа внутри launch не выполнялась -
 * необходимо вручную проверять статус coroutineScope. Это можно посмотреть в Android примере.
 * 2.Через scope. В этом случае произойдет моментальная отмена.
 * */

suspend fun coroutineCancellationExample() = coroutineScope {
    val downloader: Job = launch {
        println("Начинаем загрузку файлов")
        for (i in 1..5) {
                println("Загружен файл $i")
                TimeUnit.MILLISECONDS.sleep(1000) //поменять на delay(1000)
        }
    }
    delay(800L)     // установим задержку, чтобы несколько файлов загрузились
    println("Надоело ждать, пока все файлы загрузятся. Прерву-ка я загрузку...")
    downloader.cancel()    // отменяем корутину
    downloader.join()      // ожидаем завершения корутины (пробовал удалять - никак не влияет на вывод)
    println("Работа программы завершена")
}

/**
 * Методы cancel() и join() можно заменить одним методом - cancelAndJoin()
 * */

/**
 * Обработка исключения CancellationException
 * Все suspend-функции в пакете kotlinx.coroutines являются прерываемыми (cancellable).
 * Это значит, что они проверяют, прервана ли корутина. И если ее выполнение прервано, они генерируют исключение типа CancellationException.
 * И в самой корутине мы можем перехватить это исключение, чтобы обработать отмену корутины.
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
 * Выше как раз то о чем говорил Миша. Прерывание корутины можно обработать внутри билдера с помощью оборота кода корутины в try-catch
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
 * Корутины никак не блокируют друг друга.
 * */
private fun blockingTest() {
    val scope = CoroutineScope(Job())
    println("onRun, start")

    scope.launch {
        println("coroutine, start ${Thread.currentThread().name}")
        TimeUnit.MILLISECONDS.sleep(1000)
        println("coroutine, end ${Thread.currentThread().name}")
    }

    println("onRun, middle")

    scope.launch {
        println("coroutine2, start ${Thread.currentThread().name}")
        TimeUnit.MILLISECONDS.sleep(1500)
        println("coroutine2, end ${Thread.currentThread().name}")
    }

    println("onRun, end")
}

/**
 * При вызове метода cancel по ссылке Job - мы не отменяем корутину, а меняем ее статус. Она продолжит выполняться.
 * Для того, чтобы перестать выполнять работу через данную корутину необходимо отслеживать ее статус.
 * Т.е пишется условие на проверку текущий scope isActive. Пример можно увидеть вот тут https://startandroid.ru/ru/courses/kotlin/29-course/kotlin/602-urok-8-korutiny-otmena.html
 * или в app модуле андройд проекта.
 * */

/**
 * Так же можно сделать cancel по ссылке Scope. Тогда отменяться все дочерние job которые лежат внутри данного scope. В это случае
 * Все корутины будут отменены. Проверять статус не придется.
 * */