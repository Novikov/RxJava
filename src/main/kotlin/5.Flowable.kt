import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.util.concurrent.TimeUnit

fun main() {
    backpressureProblemExample()
//    backpressureProblemExample2()
//    flowableExample()
//    subscriberExample()
//    subscriberExample2()
//    flowableExample2()
//    flowableExample3()
//    flowableStrategiesExample1()
//    flowableStrategiesExample2()
//    flowableStrategiesExample3()
//    flowableStrategiesExample4()
//    flowableStrategiesExample5()
//    connectableFlowableExample()
//    flowableSlowingDownExample1()
//    flowableSlowingDownExample2()
//    flowableSlowingDownExample3()
}

/**Первая проблема - если в каком то одном подписчике вычисления будут занимать длительное время - то весь поток событий пойдет сначала на более свободный подписчик */
fun backpressureProblemExample() {
    val observable = Observable
        .just(1, 2, 3, 4, 5, 6, 7, 8, 9)

    val subject = BehaviorSubject.create<Int>()
    subject.observeOn(Schedulers.computation())
        .subscribe {
            println("Subs 1 Received $it")
            Thread.sleep(200)
        }

    subject.observeOn(Schedulers.computation())
        .subscribe {
            println("Subs 2 Received $it")
        }

    observable.subscribe(subject)
    Thread.sleep(2000)
}

/** Создание и отправка событий происходит намного быстрее, чем их потребление и обработка */
fun backpressureProblemExample2() {
    val observable = Observable.interval(1, TimeUnit.MILLISECONDS)
    observable.map { MyItem(it.toInt()) }
        //интересно то что если включить Schedulers.io, то все будет работать как надо. Одна отправка один прием todo Разобраться почему.
        .observeOn(Schedulers.io())
        .subscribe {
            println("Received $it on ${Thread.currentThread().name}")
            Thread.sleep(200)
        }
    Thread.sleep(200000)
}

data class MyItem(val id: Int) {
    init {
        println("MyItem Created $id")
    }
}

/** Теперь отправка и прием событий происходит в интервальной манере.
По умолчанию размер буфера 128 элементов. Это значит что будет именно выпуск 128 элементов. Обработка всегда будет отставать.
Обрабатываться будет менее 100 событий, но в конечном счете обработаются все события. Чем ближе к концу последовательности тем меньше будет
количество выпущенных элементов и тем больше будет количество обработанных событий.*/
fun flowableExample() {
    Flowable.range(1, 1000)
        .map { MyItem(it) }
        .observeOn(Schedulers.computation())
        .subscribe({
            print("Received $it;\n")
            Thread.sleep(50)
        }, { it.printStackTrace() })
    Thread.sleep(70000)
}

/**
 * Flowable нужно использовать в тех случаях:
 * 1)Большое количество событий (от 10000)
 * 2)Чтение файлов, данных из бд
 * 3)Работа с сетью, StreamAPI

 * Observables нужно использовать так же в трех случаях:
 * 1)Небольшое количество событий (до 10000)
 * 2)Когда выполняем синхронные операции
 * 3)Когда эмитим ui события
 */

/** Работа через Subscriber*/
fun subscriberExample() {
    val subscriber = object : Subscriber<MyItem> {
        override fun onSubscribe(subscription: Subscription) {
            subscription.request(128) //Тут нужно указывать сколько Flowable может принять. Но после этого прием стопорится. todo Разобраться почему?
        }

        override fun onNext(s: MyItem?) {
            Thread.sleep(50)
            println("Subscriber received " + s!!)
        }

        override fun onError(e: Throwable) {
            e.printStackTrace()
        }

        override fun onComplete() {
            println("Done!")
        }
    }

    Flowable.range(1, 1000)
        .map { MyItem(it) }
        .observeOn(Schedulers.io())
        .subscribe(subscriber)

    Thread.sleep(50000)
}

/** Переписанный выше пример через лямду. Интересно то, что тут не стопорится обработка событий в отличие от предыдущего */
fun subscriberExample2() {
    Flowable.range(1, 1000)
        .map { MyItem(it) }
        .observeOn(Schedulers.io())
        .doOnSubscribe { it.request(128) }
        .subscribe {
            Thread.sleep(50)
            println("Subscriber received $it")
        }
    Thread.sleep(50000)
}

/** Метод по управлению эмиссией данных*/

fun flowableExample2() {
    val flowable = Flowable.generate<Int> {
        it.onNext(GenerateFlowableItem.item)
    }

    flowable
        .map { MyItem(it) }
        .observeOn(Schedulers.io())
        .subscribe {
            Thread.sleep(100)
            println("Next $it")
        }

    Thread.sleep(70000)
}

object GenerateFlowableItem {
    var item: Int = 0
        get() {
            field += 1
            return field
        }
}

/** Генерировать данные так же можно с помощью функции create*/
fun flowableExample3() {
    Flowable.create<Int>({ emitter ->
        for (i in 1..10) {
            emitter.onNext(i)
        }
        emitter.onComplete()
    }, BackpressureStrategy.BUFFER)
        .observeOn(Schedulers.io())
        .subscribe { println(it) }

    Thread.sleep(3000)
}

/**Данная стратегия работает на выпуск данных. Накапливает все события в буфер и выгружает порциями в Subscriber. Насколько я понял - дефолтная стратегия*/
fun flowableStrategiesExample1() {
    val source = Observable.range(1, 300)
    source.toFlowable(BackpressureStrategy.BUFFER)
        .map { MyItem(it) }
        .observeOn(Schedulers.computation())
        .subscribe {
            print("Rec. $it;\t")
            Thread.sleep(100)
        }
    Thread.sleep(70000)
}

/** Данная стратегия работает на выпуск данных. При переполнении буфера (более 128 эмитов) над обработанными событиями выбрасывается OnErrorNotImplementedException*/
fun flowableStrategiesExample2() {
    val source = Observable.range(1, 1000) // Если написать 128 то ошибки не будет
    source.toFlowable(BackpressureStrategy.ERROR)
        .map { MyItem(it) }
        .observeOn(Schedulers.computation())
        .subscribe {
            print("Rec $it;\t")
            Thread.sleep(100)

        }
    Thread.sleep(70000)
}

/**Так же работает на выпуск данных. То что успело выйти в subscriber - выйдет. Затем произойдет переключение на обработку */
// TODO: Непонятно почему после обработки emitter стопорится. Ведь даже если стратегия drop - должна происходить интервальная работа (Выпуск/Обработка)
fun flowableStrategiesExample3() {
    val source = Observable.interval(1, TimeUnit.MILLISECONDS)
    source.toFlowable(BackpressureStrategy.DROP)
        .map { MyItem(it.toInt()) }
        .observeOn(Schedulers.computation())
        .subscribe {
            print("Rec $it;\t")
            Thread.sleep(10)
        }
    Thread.sleep(70000)
}

/** Так же произойдет пропуск событий которые не успели пройти, но в отличие от Drop последний элемент обязательно отрисуется */
fun flowableStrategiesExample4() {
    val source = Observable.create<Int> {
        for (i in 0..1000){
            it.onNext(i)
            Thread.sleep(1)
        }
    }
    source.toFlowable(BackpressureStrategy.LATEST)
        .map { MyItem(it) }
        .observeOn(Schedulers.computation())
        .subscribe {
            print("Rec $it;\t")
            Thread.sleep(100)
        }
    Thread.sleep(70000)
}

/** Если я понял правильно, то данная стратегия снимает дефолтную стратегию, но после этого наш Flowable перестает работать ожидаемым образом и бросает
 * исключение. Чтобы это побороть - нужно использовать методы onBackPressureXXX(). Они снова устанавливают стратегию.
 * Всего их 3:
 * onBackpressureBuffer()
 * onBackpressureDrop()
 * onBackpressureLatest()
 * Добавить вызов методов можно после toFlowable(BackpressureStrategy.MISSING). Пример ниже.
 * */

// TODO: Разобрать подробнее. Не уверен что делают методы onBackPressureXXX()
fun flowableStrategiesExample5() {
    val source = Observable.create<Int> {
        for (i in 0..1000){
            it.onNext(i)
            Thread.sleep(1)
        }
    }
    source.toFlowable(BackpressureStrategy.MISSING)
        .onBackpressureBuffer() // При отсутствии данного метода будет бросаться exception.
        .map { MyItem(it) }
        .observeOn(Schedulers.io())
        .subscribe {
            print("Rec $it;\t")
            Thread.sleep(100)
        }
    Thread.sleep(60000)
}

fun connectableFlowableExample() {
    val connectableFlowable =
        listOf("String 1", "String 2", "String 3", "String 4", "String 5")
            .toFlowable()
            .publish()

    connectableFlowable.subscribe {
        println("Subscription 1: $it")
        Thread.sleep(100)
        println("Subscription 1 delay")
    }
    connectableFlowable
        .subscribe { println("Subscription 2 $it") }

    connectableFlowable.connect()
}

/** Управление замедлением эмиссии*/

/** Метод buffer возвращает Flowable<List<T>>*/
fun flowableSlowingDownExample1() {
    val flowable = Flowable.range(1, 111)
    flowable.buffer(10)
        .subscribe { println(it) }
}

/** buffer со сдвигом. Всего есть 3 способа его использования:
 * skip > buffer
 * skip == buffer
 * skip < buffer
 */
fun flowableSlowingDownExample2() {
    val flowable = Flowable.range(1, 111)
//        flowable.buffer(10,1)
//    flowable.buffer(10, 10)
    flowable.buffer(1, 10)
        .subscribe { println("Subscription $it") }
}

/** Buffer с временной задержкой*/
fun flowableSlowingDownExample3() {
    val flowable = Flowable.interval(100, TimeUnit.MILLISECONDS)
    flowable.buffer(1, TimeUnit.SECONDS)
        .subscribe { println(it) }
    Thread.sleep(5000)
}

fun flowableSlowingDownExample4() {
    val boundaryFlowable = Flowable.interval(350, TimeUnit.MILLISECONDS)

    val flowable = Flowable.interval(100, TimeUnit.MILLISECONDS)
    flowable.buffer(boundaryFlowable)
        .subscribe { println(it) }

    Thread.sleep(500)
}

// TODO: Разобрать методы window() и throttleFirst()
