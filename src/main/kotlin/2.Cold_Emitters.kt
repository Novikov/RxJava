import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.io.File
import java.io.FileNotFoundException

fun main() {
    simpleObservable()
//    simpleObservable2()
//    singleExample()
//    singleExample2()
//    completableExample()
//    maybeExample()
//    flowableExampleInColdEmitters()
}

/** Cold Observable example - Выпуск значений произойдет только после подписки */

/** Абстрактный Observable*/
fun simpleObservable() {
    val observable: Observable<String> = Observable.create<String> {
        it.onNext("Emit 1")
        it.onNext("Emit 2")
        it.onNext("Emit 3")
        it.onNext("Emit 4")
        it.onError(Exception("My Custom Exception")) // после вызова onError - onComplete не вызовется
        it.onComplete()
    }

    val observer: Observer<String> = object : Observer<String> {
        override fun onComplete() {
            println("All Completed")
        }

        override fun onNext(item: String) {
            println("Next $item")
        }

        override fun onError(e: Throwable) {
            println("Error Occured ${e.message}")
        }

        override fun onSubscribe(d: Disposable) {
            println("New Subscription ")
        }
    }

    //Данный метод возвращает Unit, а не disposable (Disposable отдаст меотд subscribeBy())
    val value = observable.subscribe(observer)
}

/** Другие способы создания observable */
fun simpleObservable2() {
    val observable = Observable.just(1, 2, 3)
    //так же есть очень много методов создания Observable из других типов данных Observable.from()
    val subscription = observable.subscribeBy(onNext = { println(it) }, onComplete = { println("Complete") })
}

/** Существуют модификации Observable которые более лаконично выражают цели его использования. Observable это более абстрактный тип эмиттера*/

/** SINGLE, COMPLETABLE, MAYBE - РАЗОВЫЕ ОПЕРАЦИИ*/

/**  Выпускают или событие об успешном завершении (success) или об ошибки (error). События типа
success – это комбинация событий next и complete. Этот тип Observable полезен для разовых операций которые
либо будут успешно завершены c каким то РЕЗУЛЬТАТОМ, либо потерпят неудачу. Пример – чтение содержимого файла. */

fun singleExample() {
    val single = Single.create<String> {
        it.onError(Throwable("Error event")) // после onError - onSuccess не придет.
        it.onSuccess("Success event")
    }

    single.subscribeBy(onError = { println(it.message) }, onSuccess = { println(it) })
}

/** Пример - чтение файла. Или успешно с результатом или ошибка*/
fun singleExample2() {
    val subscriptions = CompositeDisposable()
    fun loadText(filename: String): Single<String> {
        return Single.create create@{ emitter ->
            val file = File(filename)
            if (!file.exists()) {
                emitter.onError(FileNotFoundException("Can’t find $filename"))
                return@create
            }
            val contents = file.readText(Charsets.UTF_8)
            emitter.onSuccess(contents)
        }
    }

    val observer = loadText("/Users/igornovikov/Git/RxJava/src/main/kotlin/copyright.txt")
        .subscribeBy(
            onSuccess = { println(it) },
            onError = { println("Error, $it") })
    subscriptions.add(observer)
}

/** Выпускает только событие о завершение или ошибке.Такой издатель не выпускает никаких
значений. Это используется для операций, где нам нужно узнать успешно ли она завершилась или с ошибкой.
Пример запись файла на диск. Нам не нужно знать какие данные записались, а сам факт что это произошло. */
fun completableExample() {
    val completable = Completable.create {
        it.onError(Throwable("Error event")) // после error - onComplete не придет.
        it.onComplete()
    }
    completable.subscribeBy(
        onComplete = { println("Emission has completed") }, onError = { println(it.message) }
    )
}

/** Сочетание Single т Completable. Он может выпускать события success, complete и error. Если вам
нужно реализовать операцию, которая может быть либо успешным, либо неудачным, и при желании вернуть
значение в случае успеха.*/
fun maybeExample() {
    val maybe = Maybe.create<String> {
        it.onError(Throwable("Error event")) // после onError - onSuccess и onComplete не придут.
        it.onSuccess("Success event")
        it.onComplete()
    }

    maybe.subscribeBy(
        onSuccess = { println(it) },
        onError = { println(it.message) },
        onComplete = { println("Complete event") })
}

/** Такой же абстрактный Observable, но с поддержкой backpressure (обработка большого количества событий)*/
private fun flowableExampleInColdEmitters() {
    val flowable = Flowable.create<String>({ emitter ->
        for (i in 1..10) {
            emitter.onNext(i.toString())
        }
        emitter.onComplete()
    }, BackpressureStrategy.BUFFER)

    flowable.subscribeBy(
        onNext = { println(it) },
        onComplete = { println("complete event") },
        onError = { println(it) })
}



