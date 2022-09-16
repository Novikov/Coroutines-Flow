package coroutines

import kotlinx.coroutines.*
import kotlin.coroutines.*

suspend fun main() {
    passingContextDataAtCoroutineCreation()
}

suspend fun contextCreationExample() {
    val context = Job() + Dispatchers.Default
    val scope = CoroutineScope(context) // Требует обязательной передачи хотябы одного параметра
    //Если не передадим Job - он будет создан.
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

suspend fun putMyObjectToContext() {
    val userData = UserData(1, "name1", 10)
    val scope = CoroutineScope(Job() + Dispatchers.Default + userData)
    // а достать его можно вот так:
    val gettingUserData = coroutineContext[UserData]
}


/** Передача данных контекста при создании корутин*/

private fun contextToString(context: CoroutineContext): String =
    "Job = ${context[Job]}, Dispatcher = ${context[ContinuationInterceptor]}"


/**
 * Если в родительском scope есть Dispatcher то он передастся в дочерний scope. Иначе создастся новый.
 * Job всегда создатся новый. Потому что у каждой корутины должен быть свой собственный Job,
 * который отвечает за состояние корутины и ее результат.
 * */
suspend fun passingContextDataAtCoroutineCreation() {
    val scope = CoroutineScope(Job() + Dispatchers.IO)
    println("scope, ${contextToString(scope.coroutineContext)}")

    scope.launch {
        println("coroutine, level1, ${contextToString(coroutineContext)}")

        launch(Dispatchers.Default) {
            println("coroutine, level2, ${contextToString(coroutineContext)}")

            launch {
                println("coroutine, level3, ${contextToString(coroutineContext)}")
            }
        }
    }

    delay(2000)
}