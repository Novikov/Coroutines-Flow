package coroutines

import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun main() {
//    withoutCoroutines()
//    withCoroutines()
    lostCoroutineExample()
}

suspend fun withoutCoroutines() {
    for (i in 0..5) {
        delay(400L)
        println(i)
    }

    println("Hello Coroutines") //распечатается в последней позиции
}

suspend fun withCoroutines() = coroutineScope {
    launch {
        for (i in 0..5) {
            delay(400L)
            println(i)
        }
    }

    println("Hello Coroutines") //распечатается в первой позиции
}

/** 1.Модификатор suspend определяет функцию, которая может приостановить свое выполнение и возобновить его через некоторый период времени
 * 2.Прежде всего для определения и выполнения корутины нам надо определить для нее контекст, так как корутина может вызываться только в контексте корутины (coroutine scope).
 * Для этого применяется функция coroutineScope() - создает контекст корутины. Кроме того, эта функция ожидает выполнения всех определенных внутри нее корутин.
 * Стоит отметить, что coroutineScope() может применяться только в функции с модификатором suspend, коей является функция main.

Сама корутина определяется и запускается с помощью билдера корутин - функции launch. Она создает корутину в виде блока кода - в данном случае это:
{
    for(i in 0..5){
    delay(400L)
    println(i)
    }
}
и запускает эту корутину параллельно с остальным кодом. То есть данная корутина выполняется независимо от прочего кода, определенного в функции main.

Все что находится внутри launch - можно вынести в отдельную функцию и тогда внутри launch будет только вызов данной функции {doWork()}

Важный ньюанс - функция main должна возвращать значение Unit поэтому удаление привести к ошибке, но у меня все отрабатывает нормально
 * */


/** Корутины и потоки
В ряде языков программирования есть такие структуры, которые позволяют использовать потоки.
Однако между корутинами и потоками нет прямого соответствия.
Корутина не привязана к конкретному потоку. Она может быть приостановить выполнение в одном потоке, а возобновить выполнение в другом.

Когда корутина приостанавливает свое выполнение, например, как в случае выше при вызове задержки с помощью функции delay(),
эта корутина освобождает поток, в котором она выполнялась, и сохраняется в памяти. А освобожденный поток может быть зайдествован для других задач.
А когда завершается запущенная задача (например, выполнение функции delay()), корутина возобновляет свою работу в одном из свободных потоков.

 * */


/** Потерянная корутина
 * Если нижестоящую функцию вызвать из другой suspend функции то она никогда не завершится
 * Если мы работаем с кодом, который выполняет асинхронные запросы - необходимо вызывать continuation.resume()
 * иначе корутина не продолжит свое выполнение.  Это используется когда мы оборачиваем какой либо внешний api в suspend функции*/
suspend fun getData2(): Int = suspendCoroutine {
    println("suspend function, start")
    thread {
        println("suspend function, background work")
        TimeUnit.MILLISECONDS.sleep(1000)
//        it.resume(5)
    }
}

suspend fun lostCoroutineExample() {
    val scope = CoroutineScope(Dispatchers.Unconfined)

    scope.launch() {
        println("start coroutine ${Thread.currentThread().name}")
        val data = getData2()
        println("end coroutine ${Thread.currentThread().name}")
    }

    delay(2000)
}

/**
 * Корутины никак не блокируют друг друга.
 * */
private fun blockingTest() {
    val scope = CoroutineScope(Job())
    println("onRun, start")

    scope.launch {
        println("coroutine, start ${Thread.currentThread().name}")
        TimeUnit.MILLISECONDS.sleep(1000)
        println("coroutine, end ${Thread.currentThread().name}")
    }

    println("onRun, middle")

    scope.launch {
        println("coroutine2, start ${Thread.currentThread().name}")
        TimeUnit.MILLISECONDS.sleep(1500)
        println("coroutine2, end ${Thread.currentThread().name}")
    }

    println("onRun, end")
}