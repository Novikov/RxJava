import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.ComputationScheduler
import io.reactivex.schedulers.Schedulers

fun main() {
    baseExample()
}

/** Cold Observable example - Выпуск значений произойдет только после подписки */

fun baseExample() {
    val observable: Observable<String> = Observable.create<String> {
        println("Inside start observable - ${Thread.currentThread().name}")
        it.onNext("Emission Emit 1")
        it.onNext("Emit 2")
        println("Inside intermediate observable - ${Thread.currentThread().name}")
        it.onNext("Emit 3")
        it.onNext("Emit 4")
        it.onComplete()
    }

    val observer: Observer<String> = object : Observer<String> {
        override fun onComplete() {
            println("Consumption all Completed thread - ${Thread.currentThread().name}\"")
        }

        override fun onNext(item: String) {
            println("Consumption next $item thread - ${Thread.currentThread().name}")
        }

        override fun onError(e: Throwable) {
            println("Consumption error Occured ${e.message} thread - ${Thread.currentThread().name}\"")
        }

        override fun onSubscribe(d: Disposable) {
            println("onSubscribe")
        }
    }

    //Данный метод возвращает Unit, а не disposable (Disposable отдаст меотд subscribeBy())
    val value = observable
        .subscribeOn(Schedulers.io())
        .subscribeOn(Schedulers.computation())
        .subscribe(observer)

    Thread.sleep(3000)
}