package coroutines

import kotlinx.coroutines.*

suspend fun main() {
    coroutineCancellationExample()
}

/**
 * При работе приложения может сложиться необходимость отменить выполнение корутины.
 * Например, в мобильном приложении запущена корутина для загрузки данных с некоторого интернет-ресуса, но пользователь решил перейти к другой странице приложения, и ему больше не нужны эти данные.
 * В этом случае чтобы зря не тратить ресурсу системы, мы можем предусмотреть отмену выполнения корутины.
 * */

suspend fun coroutineCancellationExample() = coroutineScope {

    val downloader: Job = launch {
        println("Начинаем загрузку файлов")
        for (i in 1..5) {
            println("Загружен файл $i")
            delay(500L)
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
            println("Загрузка файлов прервана")
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

suspend fun catchExternalCancellationInAsync()= coroutineScope{

    // создаем и запускаем корутину
    val message = async {
        getMessage2()
    }
    // отмена корутины
    message.cancelAndJoin()

    try {
        // ожидаем получение результата
        println("message: ${message.await()}")
    }
    catch (e:CancellationException){
        println("Coroutine has been canceled")
    }
    println("Program has finished")
}

suspend fun getMessage2() : String{
    delay(500L)
    return "Hello"
}


/**
 * Отмена родительского scope отменяет все дочерние.
 * Отмена рядового job не влияет на его siblings
 * */

