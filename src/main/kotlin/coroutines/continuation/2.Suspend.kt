package coroutines.continuation

import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

var c: Continuation<Unit>? = null

//Вызов suspendCoroutine вернет специальный маркер COROUTINE_SUSPENDED, а не Unit. Для того, чтобы продолжить выполнение кода необходимо использовать билдер ниже
suspend fun suspendMe() = suspendCoroutine<Unit> {
    println("Suspended")
    c = it
}

//Вернуть управление на main()
fun builder(c: suspend () -> Unit) {
    c.startCoroutine(Continuation(EmptyCoroutineContext) { it.getOrThrow() })
}