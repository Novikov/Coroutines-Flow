package coroutines.continuation

import kotlinx.coroutines.delay

suspend fun main(){
    funExample()
}

suspend fun funExample(){
    println("Start work")
    delay(1000)
    println("End work")
}

/**Для того, чтобы посмотреть biteCode -> tools -> kotlin -> show kotlin bytecode +
Нажать кнопку decompile для того чтобы посмотреть упрощенную версию в формате java
т.к байткод не прочитаешью */