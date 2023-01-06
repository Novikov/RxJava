import io.reactivex.Observable
import java.util.concurrent.TimeUnit

fun main() {
    connectableObservableExample()
//    connectableObservableExample2()
//    connectableObservableExample3()
//    connectableObservableExample4()
//    connectableObservableExample5()
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

/** Life cycle
 * после выполнения dispose для первой подписки observable продолжает жить и вторая подписка будет получать следующие значения*/
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

/** Share - это обертка над publish().refcount()
 * refcount() - возращает Observable(), а не ConnecntableObservable().
 * Этот Observable будет жить до тех пор пока есть хоть одна подписка на него (т.е делаем его cold)
 * Если вызвать dispose на всех подписках, то эмиссия прекратится. При повторной подписке эмиссия начнется с самого начала.
 *
 * TODO Данный пример бросает java.lang.InterruptedException: sleep interrupted. Разобраться почему.
 */
fun connectableObservableExample3() {
    val observable2 = Observable.interval(1, TimeUnit.SECONDS)
        .doOnDispose { println("Observable dispose") }
        .doOnComplete { println("Observable complete") }
        .share()

    val sub1 = observable2.subscribe({
        println("sub 1 received $it")
        Thread.sleep(500)
    }, {
        println("sub 1 error")
    }, {
        println("sub 1 complete")
    })

    Thread.sleep(3000)

    val sub2 = observable2.subscribe({
        println("sub 2 received $it")
        Thread.sleep(500)
    }, {
        println("sub 2 error")
    }, {
        println("sub 2 complete")
    })

    sub1.dispose()

    Thread.sleep(3000)

    sub2.dispose()

    Thread.sleep(10000)
}

/**Данный метод возвращает Observable, который будет жить даже после вызова dispose() на всех подписках
 * Если снова создать новую подписку то эмиссия элементов продолжится с последнего успешно-отправленного!
 */
fun connectableObservableExample4() {

    val observable = Observable.interval(1, TimeUnit.SECONDS)
        .doOnDispose { println("Observable dispose") }
        .doOnComplete { println("Observable complete") }
        .publish().autoConnect()

    val sub1 = observable.subscribe({
        println("sub 1 received $it")
        Thread.sleep(500)
    }, {
        println("sub 1 error")
    }, {
        println("sub 1 complete")
    })

    Thread.sleep(3000)


    sub1.dispose()

    val sub2 = observable.subscribe({
        println("sub 2 received $it")
        Thread.sleep(500)

    }, {
        println("sub 2 error")
    }, {
        println("sub 2 complete")
    })

    Thread.sleep(3000)

    sub2.dispose()

    val sub3 = observable.subscribe({
        println("sub 3 received $it")
        Thread.sleep(500)
    }, {
        println("sub 3 error")
    }, {
        println("sub 3 complete")
    })

    Thread.sleep(3000)

    sub3.dispose()

    Thread.sleep(20000)

}

// TODO: replay().refcount() == share
fun connectableObservableExample5() {
    val observable = Observable.interval(1, TimeUnit.MILLISECONDS)
        .doOnDispose { println("Observable dispose") }
        .doOnComplete { println("Observable complete") }
        .replay(3)
        .refCount()

    val sub1 = observable.subscribe({
        println("sub 1 received $it")
        Thread.sleep(250)
    }, {
        println("sub 1 error")
    }, {
        println("sub 1 complete")
    })

    Thread.sleep(3000)

    //Если отписка произошла, то новый подписчик будет принимать значения с самого начала
//    sub1.dispose()

    val sub2 = observable.subscribe({
        println("sub 2 received $it")
        Thread.sleep(250)
    }, {
        println("sub 2 error")
    }, {
        println("sub 2 complete")
    })

    Thread.sleep(10000)
}