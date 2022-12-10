import io.reactivex.Observable
import java.util.concurrent.TimeUnit

fun main() {
    //    connectableObservableExample()
    connectableObservableExample2()
}

/** Connectable Observable - разновидность hot emitter*/

/** Эмиссия выполнится только после вызова connect() и будет происходить с соблюдением порядка в котором были сделаны подписки.*/
fun connectableObservableExample() {
    val connectableObservable = Observable.interval(1, TimeUnit.SECONDS)
        .publish() // Вернет connectable observable. Закомментируй и посмотри разницу.

    connectableObservable
        .subscribe { println("Subscription 1: $it") }
    connectableObservable
        .subscribe { println("Subscription 2: $it") }

    connectableObservable.connect() //Эмиссия начнется после вызова данного метода

    connectableObservable.subscribe { println("Subscription 3: $it") }

    Thread.sleep(5000)
}

fun connectableObservableExample2() {
    val observable = Observable.interval(1, TimeUnit.SECONDS)
        .doOnDispose { println("Observable dispose") }
        .doOnComplete { println("Observable complete") }
        .publish()

    val sub1 = observable.subscribe({
        println("sub 1 received $it")
        Thread.sleep(500)
    }, {
        println("sub 1 error")
    }, {
        println("sub 1 complete")
    })

    //после выполнения dispose для первой подписки observable продолжает жить и вторая подписка будет получать следующие значения
    val sub2 = observable.subscribe({
        println("sub 2 received $it")
        Thread.sleep(500)
    }, {
        println("sub 2 error")
    }, {
        println("sub 2 complete")
    })

    observable.connect()

    Thread.sleep(3000)

    sub1.dispose()

    Thread.sleep(10000)
}