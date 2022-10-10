package coroutines

import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

suspend fun main() {

}

/**
 *
 * */
suspend fun coroutineScopeExample(){
    withContext(Job()){

    }
}

//withContext 15.42
//coroutineScope
//Что есть дочерняя корутина? В ппримере где не сработал coroutine exception handler была как раз top level coroutine.

