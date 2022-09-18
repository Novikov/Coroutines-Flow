package coroutines

import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

suspend fun main() {
//    errorHandlingExample1()
//    errorHandlingExample2()
//    errorHandlingExample4()
    errorHandlingExample5()
}

/** Исключение не обработается
Билдер launch, используется чтобы создать и запустить корутину, и сам после этого сразу завершается.
А корутина живет своей жизнью в отдельном потоке. Вот именно поэтому try-catch здесь и не срабатывает.
Билдер launch формирует контекст, создает пару Continuation+Job, и отправляет Continuation диспетчеру, который помещает его в очередь.
Ни в одном из этих шагов не было никакой ошибки, поэтому try-catch ничего не поймал.
Билдер завершил свою работу, и метод onRun успешно завершился.
У диспетчера есть свободный поток, который постоянно мониторит очередь.
Он обнаруживает там Continuation и начинает его выполнение. И вот тут уже возникает NumberFormatException.
Но наш try-catch до него никак не мог дотянуться. Т.к. он покрывал только создание и запуск корутины, но не выполнение корутины, т.к. выполнение ушло в отдельный поток.
 * */
private suspend fun errorHandlingExample1() = coroutineScope {
    println("onRun start")
    try {
        launch {
            Integer.parseInt("a")
        }
    } catch (e: Exception) {
        println("error $e")
    }

    println("onRun end")
}

/** Тоже самое произойдет если мы поменяем launch на thread. Генерится другой код в котором происходит Exception.
 * Поэтому try его не отловит*/

/**Следующий код отловит исключение потому что мы оборачиваем в catch непосредственно в месте вызова.*/
private suspend fun errorHandlingExample2() = coroutineScope {
    println("onRun start")
    launch {
        try {
            Integer.parseInt("a")
        } catch (e: Exception) {
            println("error $e")
        }
    }
    println("onRun end")
}

/**
 * Re-throw
 * Но на самом деле под капотом происходила определенная работа по обработке этого исключения. Оно было перехвачено, но потом снова выброшено.
 * При возникновении Exception в дочерней корутине - происходит отмена родительского job() и всех дочерних корутин.
 * Далее Job корутины пытается самостоятельно обработать исключение, которое он получил из Continuation.
 * Для этого он проверяет свой контекст, есть ли там объект CoroutineExceptionHandler.
 * Если Job находит этот объект, он передает ему исключение и крэша не будет.
 * Если же такого объекта в контексте не было, то Job отправит исключение в глобальный обработчик ошибок, который завершит работу приложения с крэшем.
 * */
private suspend fun errorHandlingExample3() = coroutineScope {
    val handler = CoroutineExceptionHandler { context, exception ->
        println("handled $exception")
    }

    println("onRun start")
    launch(handler) {
        try {
            Integer.parseInt("a")
        } catch (e: Exception) {
            println("error $e")
        }
    }
    println("onRun end")
}

/**
 * Важный момент. Даже если мы создадим coroutineExceptionHandler - при возникновении ошибки всеровно отменятся все дочерние корутины.*/
suspend fun errorHandlingExample4() = coroutineScope {
    val handler = CoroutineExceptionHandler { context, exception ->
        println("first coroutine exception $exception")
    }

    launch(handler) {
        TimeUnit.MILLISECONDS.sleep(1000)
        Integer.parseInt("a")
    }

    launch {
        repeat(5) {
            TimeUnit.MILLISECONDS.sleep(300)
            println("second coroutine isActive ${isActive}")
        }
    }
}

/**
 * Разные scope никак не связаны между собой.
 * */
suspend fun errorHandlingExample5() = coroutineScope {
    val handler = CoroutineExceptionHandler { context, exception ->
        println("first coroutine exception $exception")
    }

    val scope = CoroutineScope(Dispatchers.Default)

    launch(handler) {
        TimeUnit.MILLISECONDS.sleep(1000)
        Integer.parseInt("a")
    }

    scope.launch {
        repeat(5) {
            TimeUnit.MILLISECONDS.sleep(300)
            println("second coroutine isActive ${isActive}")
        }
    }
}