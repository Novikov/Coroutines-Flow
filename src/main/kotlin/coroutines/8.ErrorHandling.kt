package coroutines

import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

suspend fun main() {
//    errorHandlingExample1()
//    errorHandlingExample2()
//    errorHandlingExample3()
//    errorHandlingExample4()
//    errorHandlingExample5()
    errorHandlingExample6()
//    errorHandlingExample7()
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
 *
 * Код ниже не перехватит Exception потому что coroutine exception handler установлен в дочерний cope. Для того, чтобы это заработало - нужно создать
 * scope и передать coroutine Exception handler в конструктор вторым параметром, после Job(). В таком случае это будет работать.
 * Все это очень хорошо расписано вот тут https://www.lukaslechner.com/why-exception-handling-with-kotlin-coroutines-is-so-hard-and-how-to-successfully-master-it/
 * */
private suspend fun errorHandlingExample3() = coroutineScope {
    val handler = CoroutineExceptionHandler { context, exception ->
        println("handled $exception")
    }

    println("onRun start")
    launch(handler) {
        Integer.parseInt("a")
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
 * Разные scope никак не связаны между собой. Если в первом будет ошибка, второй продолжит работать.
 * Данный handler передастся во все дочерние корутины т.к он является частью coroutine context.
 * */
suspend fun errorHandlingExample5() {
    val handler = CoroutineExceptionHandler { context, exception ->
        println("first coroutine exception $exception")
    }

    val scope1 = CoroutineScope(Dispatchers.Default)
    val scope2 = CoroutineScope(Dispatchers.Default)

    scope1.launch(handler) {
        delay(1000)
        Integer.parseInt("a")
    }

    scope2.launch {
        repeat(5) {
            delay(300)
            println("second coroutine isActive ${isActive}")
        }
    }

    delay(10000)
}

/**
 * Supervisor job
 * В примерах с CoroutineExceptionHandler мы убедились, что scope отменяет своих детей, когда в одном из них происходит ошибка.
 * Пусть даже эта ошибка и была передана в обработчик. Такое поведение родителя далеко не всегда может быть удобным. Поэтому у нас есть возможность это отключить.
 * Для этого надо в scope вместо обычного Job() использовать SupervisorJob(). Он отличается от Job() тем, что не отменяет всех своих детей при возникновении ошибки в одном из них.
 * */

suspend fun errorHandlingExample6() {
    val handler = CoroutineExceptionHandler { context, exception ->
        println("first coroutine exception $exception")
    }

    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default + handler)

    scope.launch() {
        TimeUnit.MILLISECONDS.sleep(1000)
        Integer.parseInt("a")
    }

    scope.launch {
        repeat(5) {
            TimeUnit.MILLISECONDS.sleep(300)
            println("second coroutine isActive ${isActive}")
        }
    }

    delay(10000)
}

/**
 * Обработка исключений во вложенных корутинах.
 * 1. Если обернуть место выброса исключения в try внутри launch как в прошлом уроке - то поведение не изменится.
 * 2. Отличие возникает при передаче ошибки родителю.
 * Если родитель - scope то ошибка передается в coroutine exception handler. Если такого обработчика нет, то ошибка передастся в глобальный обработчик.
 * Если родитель корутина то ошибка передается ему. Текущая корутна спрашивает сможет ли родитель обработать. Если родитель так же корутина, то
 * ошибка уходит вверх по иерархии. Таким образом эта ошибка поднимается наверх по иерархии вложенных корутин, пока не достигнет самой верхней корутины.
 * Ее родитель - это скоуп. Когда самая верхняя корутина спросит его, он скажет, что ошибку обработать не сможет (Если нет обработчика).
 * */

/**
 * Из объяснений выше следует, что обработчик CoroutineExceptionHandler сработает только в самой верхней корутине.
 * Потому что только она получит от своего родителя отрицательный ответ и попытается обработать ошибку сама.
 * Остальные корутины просто передают ошибку родительской корутине и сами ничего с ней делают
 * */
suspend fun errorHandlingExample7() {
    val handler = CoroutineExceptionHandler { context, exception ->
        println("$exception was handled in Coroutine_${context[CoroutineName]?.name}")
    }

    val scope = CoroutineScope(Job() + Dispatchers.IO + handler)

    scope.launch(CoroutineName("1")) {

        launch(CoroutineName("1_1")) {
            repeatIsActive()
            TimeUnit.MILLISECONDS.sleep(500)
            Integer.parseInt("a")
        }

        launch(CoroutineName("1_2")) {
            TimeUnit.MILLISECONDS.sleep(1000)
            repeatIsActive()
        }
    }

    scope.launch(CoroutineName("2")) {

        launch(CoroutineName("2_1")) {
            TimeUnit.MILLISECONDS.sleep(1000)
            repeatIsActive()
        }

        launch(CoroutineName("2_2")) {
            TimeUnit.MILLISECONDS.sleep(1000)
            repeatIsActive()
        }
    }

    delay(5000)
}

/**
 *  Ниже расширение для того чтоб наглядно убедиться в том, что ошибка по пути наверх отменит все другие корутины
 * */

fun CoroutineScope.repeatIsActive() {
    repeat(5) {
        TimeUnit.MILLISECONDS.sleep(500)
        println("Coroutine_${coroutineContext[CoroutineName]?.name} isActive $isActive")
    }
}

/**
 * Т.е. ошибка поднимается наверх до scope, отменяя все, что можно.
 * И далее распространяется на остальные корутины этого scope, каскадно отменяя в них все дочерние корутины.
 * */

/**
 * SuperVisorJob и вложенность
 * На самом последнем скрине https://startandroid.ru/ru/courses/kotlin/29-course/kotlin/609-urok-14-korutiny-obrabotka-isklyucheniy-vlozhennye-korutiny.html
 * SupervisorJob помогает лиш частично. Мы сможем сохранить выпоплнение других корутин на уровне 1. Но Если ошибка произошла на уровне 2 - то
 * отменятся так же siblings корутины.
 * Мы не можем передать SuperVisorJob на уровне 1 т.к сломается механисм связи между родительскими и дочерними корутинами.
 * Говорит решение данной проблемы SuperVisorScope.
 * */

/**
 * Async/Await()
 * Вызов await всегда идет после тела async. Выброс исключения произойдет в await. Оно пойдет вверх по иерархии отменяя все siblings.
 * Если ниже await есть какой то важный код. То await имеет смысл оборачивать в try-catch. Иначе данный код не будет выполнен.
 * Там есть способ обработки ошибки с помощью костыля. Не посчитал нужным его разбирать.
 * */

