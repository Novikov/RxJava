import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.AsyncSubject
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject

fun main() {
//    publishSubjectExample()
//    behaviourSubjectExample()
//    replaySubjectExample()
//    asyncSubjectExample()
}

/** Hot emitter способен распространять данные без активных подписок + является не только поставщиком, но и потребителем событий. */

/**Отправит следующее событие в каждую подписку. После onComplete прекращает отправку событий и любая новая подписка ничего не получит. */
fun publishSubjectExample() {
    val subject = PublishSubject.create<Int>()
    subject.onNext(1)
    subject.onNext(2)

    subject.subscribeBy(onError = { println(it.message) },
        onComplete = { println("subscriber 1 onComplete") },
        onNext = { println("subscriber 1 onNext - $it") })

    subject.onNext(3)
    subject.onNext(4)

    subject.subscribeBy(onError = { println(it.message) },
        onComplete = { println("subscriber 2 onComplete") },
        onNext = { println("subscriber 2 onNext - $it") })

    subject.onNext(5)
    subject.onNext(6)

    subject.onComplete() //После complete event - subscriber 3 более не получит событий

    subject.subscribeBy(onError = { println(it.message) },
        onComplete = { println("subscriber 3 onComplete") },
        onNext = { println("subscriber 3 onNext - $it") })

    subject.onNext(7)
    subject.onNext(8)
}

/** Каждая новая подписка получит предыдущее значение и текущее. Предыдущее значение будет получено подписчиком только один раз.
 * Далее данный subject будет отправлять данные в этот subscriber, получивший ранее данные, как publishSubject. */
fun behaviourSubjectExample() {
    val subject = BehaviorSubject.create<Int>()
    subject.onNext(1)

    subject.subscribeBy(onError = { println(it.message) },
        onComplete = { println("subscriber 1 onComplete") },
        onNext = { println("subscriber 1 onNext - $it") })
    subject.onNext(2)

    subject.subscribeBy(onError = { println(it.message) },
        onComplete = { println("subscriber 2 onComplete") },
        onNext = { println("subscriber 2 onNext - $it") })
    subject.onNext(3)

    subject.onComplete() //После complete event - subscriber 3 более не получит событий

    subject.subscribeBy(onError = { println(it.message) },
        onComplete = { println("subscriber 3 onComplete") },
        onNext = { println("subscriber 3 onNext - $it") })
}


/** Для нового подписчика воспроизводит всю предыдущую последовательность событий. Аналогичное поведение как в Behaviour subject. После отправки
 * последовательности в подписчик - начинает работать как Publish subject.*/
fun replaySubjectExample() {
    val subject = ReplaySubject.create<Int>()
    subject.onNext(1)

    subject.subscribeBy(onError = { println(it.message) },
        onComplete = { println("subscriber 1 onComplete") },
        onNext = { println("subscriber 1 onNext - $it") })
    subject.onNext(2)

    subject.subscribeBy(onError = { println(it.message) },
        onComplete = { println("subscriber 2 onComplete") },
        onNext = { println("subscriber 2 onNext - $it") })
    subject.onNext(3)

    subject.onComplete() //После complete event - subscriber 3 более не получит событий

    subject.subscribeBy(onError = { println(it.message) },
        onComplete = { println("subscriber 3 onComplete") },
        onNext = { println("subscriber 3 onNext - $it") })
}

/** Отправит последнее событие перед Complete и сам Complete втч для новых подписок*/
fun asyncSubjectExample() {
    val subject = AsyncSubject.create<Int>()
    subject.onNext(1)
    subject.onNext(2)
    subject.onNext(3)
    subject.onNext(4)

    subject.subscribeBy(onError = { println(it.message) },
        onComplete = { println("subscriber 1 onComplete") },
        onNext = { println("subscriber 1 onNext - $it") })
    subject.onNext(5)

    subject.subscribeBy(onError = { println(it.message) },
        onComplete = { println("subscriber 2 onComplete") },
        onNext = { println("subscriber 2 onNext - $it") })

    subject.onComplete()
}