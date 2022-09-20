package Flow

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

suspend fun main() {
    builderExample1()
}

suspend fun builderExample1() {
    val flow = flow {
        emit("a")
        emit("b")
        emit("c")
    }

    val flow2 = listOf("a", "b", "c").asFlow()

    val flow3 = flowOf("a", "b", "c")
}

/**
 * Операторы flow делятся на 2 типа. Промежуточные и терминальные.
 * Промежуточные операторы - это все те, что модифицируют map, filter, take, zip, combine, withIndex, scan, debounce, distinctUntilChanged, drop, sample. Все они присутствуют в Rx.
 * Терминальные операторы. collect, single, reduce, count, first, toList, toSet, fold
 * Intermediate операторы берут Flow, добавляют к нему какое-либо преобразование его данных и возвращают новый Flow. Но они не запускают Flow и не получают результаты его работы.
 * А Terminal операторы запускают Flow так же, как это делает collect. Соответственно, результатом их работы является не Flow, а данные, полученные из Flow и обработанные определенным образом.
 * */