package coroutines

import kotlinx.coroutines.*

suspend fun main() {
//    globalScopeExample()
    coroutineScopeExample()
}

/**У Global scope отсутствует job, а это значит что не будет формироваться иерархия если мы создадим семейство корутин на данном scope.
 * Время жизни данного scope соответствует времени жизни приложения. Его нужно избегать. Отменить его можно только вручную.
 * */
suspend fun globalScopeExample() {
    println("${GlobalScope.coroutineContext[Job]}")
}

/**
 * withContext() используется, чтобы изменить контекст для определенного участка кода.
 * Например, мы выполняем ресурсозатратные вычисления на viewModelScope. По умолчанию данный scope использует Dispatcher UI потока.
 * UI поток начнет фризиться. Чтобы этого не произошло, берем оборачиваем участок кода, отвечающий за вычисления с помощью withContext(Dispatcher.Default)
 */
suspend fun withContextExample() {
    withContext(Dispatchers.Default) {
        // calculations
    }
}

/**
 * coroutineScope билдер
 * */
suspend fun coroutineScopeExample() = coroutineScope {
   val job1 =  launch {
        println("Start task 1")
        delay(100)
        println("End task 1")
    }
    val job2 = launch {
        println("Start task 2")
        delay(200)
        println("End task 2")
    }
    val job3 = launch {
        println("Start task 3")
        delay(300)
        println("End task 3")
    }
}


