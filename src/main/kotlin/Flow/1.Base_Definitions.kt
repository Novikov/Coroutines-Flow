package Flow

import kotlinx.coroutines.flow.*

suspend fun main() {
    flowExample1()
}

/**
 * Обычно suspend функция возвращает нам одно значение. И пока мы ждем это значение, корутина приостанавливается.
 * Flow позволяет расширить это поведение. Он делает так, что мы можем получать последовательность (поток) данных вместо одного значения.
 * И это будет происходит в suspend режиме.Т.е. корутина будет приостанавливаться на время ожидания каждого элемента.
 * */
suspend fun flowExample1() {
    getData().onEach { println(it) }.collect()
}

fun getData(): Flow<Int> {
    return flow {
        for (i in 0..10) {
            emit(i)
        }
    }
}