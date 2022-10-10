package coroutines.continuation

import kotlin.coroutines.resume

suspend fun main() {
    val lambda: suspend () -> Unit = {
        suspendMe()
        println(1)
        suspendMe()
        println(2)
    }
    builder {
        lambda()
    }
    c?.resume(Unit)
    c?.resume(Unit)
//    c?.resume(Unit) // при resume coroutine, которая завершила свою работу будет брошен Exception.

// Вся эта работа с Continuation и возобновлением работы suspend функции нужна в большей части для разработчиков SDK,
// которые хотят подружить свой API с API корутин. Поэтому можно сильно не зарываться.
}


