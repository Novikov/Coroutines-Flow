package coroutines

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
 *
 * */
suspend fun viewModelScopeExample() {

}
suspend fun coroutineScopeExample() {
    withContext(Job()) {

    }
}

//withContext 15.42
//coroutineScope
//Что есть дочерняя корутина? В ппримере где не сработал coroutine exception handler была как раз top level coroutine.

