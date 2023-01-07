import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.ComputationScheduler
import io.reactivex.schedulers.Schedulers

fun main() {
//    threadingExample1()
//    threadingExample2()
    threadingExample3()
}

/** Вся теория рассказана тут https://habr.com/ru/company/rambler_and_co/blog/280388 */

/** Cold Observable example - Выпуск значений произойдет только после подписки */

/** Без вызова методов observeOn/subscribeOn все элементы эмиссии будут создаваться и потребляться на главном потоке */
fun threadingExample1() {
    val observable: Observable<String> = Observable.create<String> {
        println("Inside start observable [thread] - ${Thread.currentThread().name}")
        it.onNext("Emission Emit 1")
        it.onNext("Emit 2")
        println("Inside intermediate observable [thread] - ${Thread.currentThread().name}")
        it.onNext("Emit 3")
        it.onNext("Emit 4")
        it.onComplete()
    }

    val observer: Observer<String> = object : Observer<String> {
        override fun onComplete() {
            println("Consumption all Completed [thread] - ${Thread.currentThread().name}\"")
        }

        override fun onNext(item: String) {
            println("Consumption next $item [thread] - ${Thread.currentThread().name}")
        }

        override fun onError(e: Throwable) {
            println("Consumption error Occured ${e.message} [thread] - ${Thread.currentThread().name}\"")
        }

        override fun onSubscribe(d: Disposable) {
            println("onSubscribe [thread] - ${Thread.currentThread().name}\"")
        }
    }
    observable.subscribe(observer)

    Thread.sleep(3000)
}

/** subscribeOn() -  с помощью этого оператора можно указать Scheduler, в котором будет выполняться работа Observable
 *
 * При использовании без observeOn() - влияет как на операции эмиссии так и на операции потребления. На main выполнится только onSubscribe()
 */
fun threadingExample2() {
    val observable: Observable<String> = Observable.create<String> {
        println("Inside start observable [thread] - ${Thread.currentThread().name}")
        it.onNext("Emission Emit 1")
        it.onNext("Emit 2")
        println("Inside intermediate observable [thread] - ${Thread.currentThread().name}")
        it.onNext("Emit 3")
        it.onNext("Emit 4")
        it.onComplete()
    }

    val observer: Observer<String> = object : Observer<String> {
        override fun onComplete() {
            println("Consumption all Completed [thread] - ${Thread.currentThread().name}\"")
        }

        override fun onNext(item: String) {
            println("Consumption next $item [thread] - ${Thread.currentThread().name}")
        }

        override fun onError(e: Throwable) {
            println("Consumption error Occured ${e.message} [thread] - ${Thread.currentThread().name}\"")
        }

        override fun onSubscribe(d: Disposable) {
            println("onSubscribe [thread] - ${Thread.currentThread().name}\"")
        }
    }
    observable
        .subscribeOn(Schedulers.io())
        .subscribe(observer)

    Thread.sleep(3000)
}

/** observeOn() -  что применение этого оператора приводит к тому, что последующие операции над “излученными” данными
 * будут выполняться с помощью Scheduler, переданным в этот метод
 *
 * При использовании без subscribeOn() - влияет только на операции потребления данных. Выпуск и onSubscribe будет на main scheduler*/
fun threadingExample3() {
    val observable: Observable<String> = Observable.create<String> {
        println("Inside start observable [thread] - ${Thread.currentThread().name}")
        it.onNext("Emission Emit 1")
        it.onNext("Emit 2")
        println("Inside intermediate observable [thread] - ${Thread.currentThread().name}")
        it.onNext("Emit 3")
        it.onNext("Emit 4")
        it.onComplete()
    }

    val observer: Observer<String> = object : Observer<String> {
        override fun onComplete() {
            println("Consumption all Completed [thread] - ${Thread.currentThread().name}\"")
        }

        override fun onNext(item: String) {
            println("Consumption next $item [thread] - ${Thread.currentThread().name}")
        }

        override fun onError(e: Throwable) {
            println("Consumption error Occured ${e.message} [thread] - ${Thread.currentThread().name}\"")
        }

        override fun onSubscribe(d: Disposable) {
            println("onSubscribe [thread] - ${Thread.currentThread().name}\"")
        }
    }
    observable
        .observeOn(Schedulers.io())
        .subscribe(observer)

    Thread.sleep(3000)
}