package coroutines

import kotlinx.coroutines.*

suspend fun main() {
    globalScopeExample()
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
suspend fun coroutineScopeExample() {

}


