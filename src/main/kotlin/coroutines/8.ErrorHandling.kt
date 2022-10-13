package coroutines

import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

suspend fun main() {
//    errorHandlingExample6()
//    errorHandlingExample7()
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

