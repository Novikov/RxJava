import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

fun main() {
//    imperativeStyle()
//    declarativeStyle()
//    pull()
    push()
}

/** Описываем как получить желаемый результат */
fun imperativeStyle() {
    var number = 4
    var isEven = isEven(number)
    println("The number is " + (if (isEven) "Even" else "Odd"))
    number = 9
    isEven = isEven(number)
    println("The number is " + (if (isEven) "Even" else "Odd"))
}

/** Описываем какой результат нам нужен */
fun declarativeStyle() {
    val subject: Subject<Int> = PublishSubject.create()
    subject.map { isEven(it) }.subscribe { println("The number is ${(if (it) "Even" else "Odd")}") }

    subject.onNext(4)
    subject.onNext(9)
}


fun isEven(n: Int): Boolean = ((n % 2) == 0)

/** Pull and Push mechanism
The thing to notice is that we're pulling data from the list while the current thread is
blocked until the data is received and ready.  For example, think of getting that data from a
network call/database query instead of just List and, in that case, how long the thread will
be blocked. You can obviously create a separate thread for those operations, but then also, it
will increase complexity.*/

fun pull() {
    val list: List<Any> = listOf("One", 2, "Three", "Four", 4.5, "Five", 6.0f)
    val iterator = list.iterator()
    while (iterator.hasNext()) {
        println(iterator.next())
    }
}

/** The building blocks of the ReactiveX Framework (be it RxKotlin or RxJava) are the
observables. The observable class is just the opposite of iterator interface. It has an
underlying collection or computation that produces values that can be consumed by a
consumer. However, the difference is that the consumer doesn't pull these values from the
producer, like in the iterator pattern; instead, the producer pushes the values as
notifications to the consumer.*/
fun push() {
    val list: List<Any> = listOf("One", 2, "Three", "Four", 4.5, "Five", 6.0f)
    val observable: Observable<Any> = list.toObservable();
    observable.subscribeBy(
        onNext = { println(it) },
        onError = { it.printStackTrace() },
        onComplete = { println("Done!") }
    )
}



