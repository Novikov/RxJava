import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable

fun main() {
//    disposableExample()
//    compositeDisposableExample()
    compositeDisposable2Example()
}

fun disposableExample() {
    val list: List<Any> = listOf(1, 2, 3)
    val observable: Observable<Any> = list.toObservable();
    val disposable = observable.subscribeBy(
        onNext = { println(it) },
        onError = { it.printStackTrace() },
        onComplete = { println("Done!") }
    )
    disposable.dispose() //Метод по очистке подписки. У disposable доступен только он.

    //Вторая подписка отработает
    val disposable2 = observable.subscribeBy(
        onNext = { println(it) },
        onError = { it.printStackTrace() },
        onComplete = { println("Done!") }
    )
}

/**
 * Unlike Disposable, CompositeDisposable has two ways of disposing subscriptions:
 * dispose()
 * clear()
 * Both operations dispose the previously contained subscriptions, but dispose() operation assigns true to the disposed variable,
 * which later does not let you add, remove,or delete any other disposables.
 * */

fun compositeDisposableExample() {

    val list: List<Any> = listOf(1, 2, 3)
    val observable: Observable<Any> = list.toObservable();
    val disposable = observable.subscribeBy(
        onNext = { println(it) },
        onError = { it.printStackTrace() },
        onComplete = { println("Done!") }
    )

    val compositeDisposable = CompositeDisposable()
    compositeDisposable.add(disposable)

    println("number of disposable before dispose - ${compositeDisposable.size()}")
    compositeDisposable.dispose()
    println("number of disposable after dispose - ${compositeDisposable.size()}")

    //Вторая подписка отработает, но disposable2 не добавится в compositeDisposable
    val disposable2 = observable.subscribeBy(
        onNext = { println(it) },
        onError = { it.printStackTrace() },
        onComplete = { println("Done!") }
    )

    compositeDisposable.add(disposable2)
    println("number of disposable after adding one more disposable - ${compositeDisposable.size()}")
}

fun compositeDisposable2Example() {
    val list: List<Any> = listOf(1, 2, 3)
    val observable: Observable<Any> = list.toObservable();
    val disposable = observable.subscribeBy(
        onNext = { println(it) },
        onError = { it.printStackTrace() },
        onComplete = { println("Done!") }
    )

    val compositeDisposable = CompositeDisposable()
    compositeDisposable.add(disposable)

    println("number of disposable before dispose - ${compositeDisposable.size()}")
    compositeDisposable.clear()
    println("number of disposable after dispose - ${compositeDisposable.size()}")

    //Вторая подписка отработает, и после clear -  disposable2 добавится в compositeDisposable
    val disposable2 = observable.subscribeBy(
        onNext = { println(it) },
        onError = { it.printStackTrace() },
        onComplete = { println("Done!") }
    )

    compositeDisposable.add(disposable2)
    println("number of disposable after adding one more disposable - ${compositeDisposable.size()}")
}