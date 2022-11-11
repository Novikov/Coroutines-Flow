package coroutines

import kotlinx.coroutines.*
import kotlin.coroutines.*

suspend fun main() {
    coroutineContextExample1()
}

suspend fun contextCreationExample() {
    val context = Job() + Dispatchers.Default
    val scope = CoroutineScope(context) // Требует обязательной передачи хотябы одного параметра
    //Если не передадим Job - он будет создан.
}

suspend fun putMyObjectToContext() {
    val userData = UserData(1, "name1", 10)
    val scope = CoroutineScope(Job() + Dispatchers.Default + userData)
    // а достать его можно вот так:
    val gettingUserData = coroutineContext[UserData]
}

/**
 * Пример создания объекта который можно положить в CoroutineContext
 * */
data class UserData(
    val id: Long,
    val name: String,
    val age: Int
) : AbstractCoroutineContextElement(UserData) {
    companion object Key : CoroutineContext.Key<UserData>
}

/** Передача данных контекста при создании корутин*/

/**
 * Если в родительском scope есть Dispatcher то он передастся в дочерний scope. Иначе создастся новый.
 * Job всегда создатся новый. Потому что у каждой корутины должен быть свой собственный Job,
 * который отвечает за состояние корутины и ее результат.
 * */
suspend fun coroutineContextExample1() {
    val scope = CoroutineScope(Job() + Dispatchers.IO)
    println("scope, ${contextToString(scope.coroutineContext)}")

    scope.launch {
        println("coroutine, level1, ${contextToString(coroutineContext)}")

        launch(Dispatchers.Default) {
            println("coroutine, level2, ${contextToString(coroutineContext)}")

            launch {
                withContext(Dispatchers.IO) {
                    println("coroutine, level3, ${contextToString(coroutineContext)}")
                }
            }
        }
    }

    delay(2000)
}

fun contextToString(context: CoroutineContext): String =
    "Job = ${context[Job]}, Dispatcher = ${context[ContinuationInterceptor]}"

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