import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import java.lang.RuntimeException

fun main() {
    errorHandlingExample1()
//    errorHandlingExample2()
//    errorHandlingExample3()
//    errorHandlingExample4()
//    errorHandlingExample5()
}

/** Если не указать обработчик ошибок, то при получении ошибки в цепи -
 * приложение свалится по io.reactivex.exceptions.OnErrorNotImplementedException: Error*/

fun errorHandlingExample1() {
    Observable.create<String> { emitter ->
        emitter.onNext("Hello")
        emitter.onNext("World")
        emitter.onError(RuntimeException("Error"))
        emitter.onComplete()
    }
        .subscribe {
            println(it)
        }
    //если заменить subscribe перегрузкой со вторым аргументом throwable то ошибка перехватится и краша не будет, но это не подойдет для flat операторов. Смотри примеры в RxOperators (Последний switchmap).
}

/** Вернет предопределенное значение если произойдет ошибка. Обрати внимание - оператор принимает Function.*/
fun errorHandlingExample2() {
    Observable.create<String> { emitter ->
        emitter.onNext("Hello")
        emitter.onNext("World")
        emitter.onError(RuntimeException("Error"))
        emitter.onComplete()
    }.onErrorReturn { "Error happens" }
        .subscribe {
            println(it)
        }
}

/*При возникновении ошибки - произойдет переподписка на другой Observable*/
fun errorHandlingExample3() {
    Observable.create<String> { emitter ->
        emitter.onNext("Hello")
        emitter.onNext("World")
        emitter.onError(RuntimeException("Error"))
        emitter.onComplete()
    }.onErrorResumeNext(Observable.just("String from new Observable"))
        .subscribe {
            println(it)
        }
}

/** Будет переподпиcываться несколько раз всякий раз когда будет приходить ошибка
 * Если 3 раза эта ошибка повторится - прокинет ее дальше по цепочки
 * */
fun errorHandlingExample4() {
    Observable.just(1, 2, 3, 4, 5)
        .map { it / (3 - it) }
        .retry(3)
        .subscribeBy(
            onNext = { println("Received $it") },
            onError = { println("Error") })
}

/** Переподписка будет происходить всякий раз пока условие удовлетворяет предикату */
fun errorHandlingExample5() {
    var retryCount = 0
    Observable.just(1, 2, 3, 4, 5)
        .map { it / (3 - it) }
        .retry { _, _ ->
            (++retryCount) < 3
        }
        .subscribeBy(onNext = { println("Received $it") }, onError = { println("Error") })
}