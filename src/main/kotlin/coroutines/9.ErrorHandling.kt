package coroutines

import kotlinx.coroutines.*

suspend fun main() {
//    example1()
    example2()
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
private suspend fun example1() = coroutineScope {
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
private suspend fun example2() = coroutineScope {
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

