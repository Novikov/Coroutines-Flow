package coroutines

import kotlinx.coroutines.*

suspend fun main() {
//    globalScopeExample()
//    coroutineScopeExample()
//    coroutineScopeExample2()
//    supervisorScopeExample()
//    scopesExecutionExample()
    scopesExecutionExample2()
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
 * coroutineScope билдер.
 * Практически во всех примерах данного проекта я получал scope через coroutineScope, но наверно лучше это было бы делать с помощью
 * val scope = CoroutineScope(Job())
 * Ниже есть поведение которого мы можем добиться с помощью coroutineScope билдера, что сделано в coroutineScopeExample2.
 * */
suspend fun coroutineScopeExample() = coroutineScope {
    val job1 = launch {
        println("Start task 1")
        delay(100)
        println("End task 1")
    }
    val job2 = launch {
        println("Start task 2")
        delay(200)
        println("End task 2")
    }

    job1.join()
    job2.join()

    val job3 = launch {
        println("Start task 3")
        delay(300)
        println("End task 3")
    }
}

/**
 * Точно такого же поведения можно добиться с помощью coroutineScope билдера.
 * Есть еще другой вариант - поместить task1 и task2 в launch и вызывать на нем join() до выполнения task3.
 * Данный scope отдаст управление на task3 только когда все его child будут завершены. Про порядок выполнения корутин есть отдельные примеры ниже.
 * */
suspend fun coroutineScopeExample2() = coroutineScope {

    coroutineScope {
        launch {
            println("Start task 1")
            delay(500)
//            throw Exception() посмотри как различается поведение выброса исключений с примером ниже
            println("End task 1")
        }
        launch {
            println("Start task 2")
            delay(500)
            println("End task 2")
        }
    }

    val job3 = launch {
        println("Start task 3")
        delay(300)
        println("End task 3")
    }
}

/**
 * Пример с supervisorScope. Раскоментируй строку с выбросом исключений и посмотри как меняется поведение обработки ошибок.
 * */
suspend fun supervisorScopeExample() = coroutineScope {
    supervisorScope {
        launch {
            println("Start task 1")
            delay(100)
//            throw Exception()
            println("End task 1")
        }
        launch {
            println("Start task 2")
            delay(200)
            println("End task 2")
        }
    }

    val job3 = launch {
        println("Start task 3")
        delay(300)
        println("End task 3")
    }
}

/**
 * Порядок выполнения различных scope.
 * Нет гарантии последовательного выполнения. coroutineScope1 -> coroutineScope2 -> coroutineScope3
 * coroutine scope это просто билдер. Если нет возможности прицепиться к viewmodelScope или lifeCycleScope - то необходимо использовать его.
 * Если необходимо изменить поведение прокидования ошибок (Structured concurency) - используем supervisor scope.
 * */
suspend fun scopesExecutionExample() = coroutineScope {
    val coroutineScope1 = CoroutineScope(Job())
    val coroutineScope2 = CoroutineScope(Job())
    val coroutineScope3 = CoroutineScope(Job())

    coroutineScope1.launch {
        println("Start task 1")
        delay(300)
        println("End task 1")
    }

    coroutineScope2.launch {
        println("Start task 2")
        delay(200)
        println("End task 2")
    }

    coroutineScope3.launch {
        println("Start task 3")
        delay(100)
        println("End task 3")
    }

    Thread.sleep(1000)
}

/**
 * Но нужно помнить что создавая scope - мы выключаем concurent поведение.
 * Блок кода внутри coroutineScope всегда будет выигрывать конкуренцию с другим кодом.
 * */
suspend fun scopesExecutionExample2() = coroutineScope { // TODO: Обсудить

    coroutineScope {
        launch {
            println("Start task 1")
            delay(300)
            println("End task 1")
        }

        launch {
            println("Start task 2")
            delay(300)
            println("End task 2")
        }
    }

    launch {
        println("Start task 3")
        delay(5)
        println("End task 3")
    }

    Thread.sleep(1000)
}


