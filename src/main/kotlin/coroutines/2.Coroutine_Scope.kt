package coroutines

import kotlinx.coroutines.*

suspend fun main() {
//    coroutineScopeExample()
//    coroutineScopeExample2()
    coroutineScopeExample3()
}

/**
 * Корутина может выполняться только в определенной области корутины (coroutine scope).
 * Область корутин представляет пространство, в рамках которого действуют корутины, она имеет определенный жизненный цикл и
 * сама управляет жизненным циклом создаваемых внутри нее корутин.
 *
 * Scope вершина иерархии связи корутин через job. Scope так же имеет свой job.
 *
 * И для создания области корутин в Kotlin может использоваться ряд функций, которые создают объект интерфейса CoroutineScope.
 * Одной из функций является coroutineScope. Она может применяться к любой функции, например:
 * */

suspend fun coroutineScopeExample() = coroutineScope {
    launch {
        for (i in 0..5) {
            println(i)
            delay(400L)
        }
    }
    println("Hello Coroutines")
    println("${this.coroutineContext[Job]}") // распечатать ссылку на job из текущего scope.
}

/**У Global scope отсутствует job, а это значит что не будет формироваться иерархия если мы создадим семейство корутин на данном scope.
 * Время жизни данного scope соответствует времени жизни приложения. Его нужно избегать. Отменить его можно только вручную.
 * */
suspend fun coroutineScopeExample2() {
    val job = GlobalScope.launch {
        for (i in 0..5) {
            println(i)
            delay(400L)
        }
    }.join()
    println("Hello Coroutines")
    println("${GlobalScope.coroutineContext[Job]}") // убедиться что нет job можно вот так
}

/**
 * Функция runBlocking блокирует вызывающий поток, пока все корутины внутри вызова runBlocking { ... } не завершат свое выполнение.
 * Нет необходимости вызывать join()
 * */
fun coroutineScopeExample3() {
    runBlocking {
        launch {
            for (i in 0..5) {
                delay(400L)
                println(i)
            }
        }
    }
    println("Hello Coroutines")
}