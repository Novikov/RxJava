import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.blockingSubscribeBy
import io.reactivex.rxkotlin.toObservable
import io.reactivex.rxkotlin.zipWith
import java.util.*
import java.util.concurrent.TimeUnit

fun main() {
    //zipping
    zippingExample1()
//    zippingExample2()
//    zippingExample3()
//    zippingExample4()
//    zippingExample5()

    //merging
//    mergingExample1()
//    mergingExample2()
//    mergingExample3()

    //grouping
//    groupingExample()

    //concatenating
//    concatenatingExample1()
//    concatenatingExample2()

    //scanning
//    scanningExample1()
//    scanningExample2()
//    scanningExample3()

    //flatting
//    flatmapExample()
//    flatmapExample1()
//    flatmapExample2()
//    concatMapExample()
//    switchMapExample()
//    switchMapExample2()
//    switchMapExample3()
}

/** Zipping */

/** Zip выдаст результирующее событие только когда все внутренние observable, использует Bifunction для указания того как соединять элементы двух эмиссий.*/
fun zippingExample1() {
    val observable1 = Observable.range(1, 10)
    val observable2 = Observable.range(11, 10)
    Observable.zip(observable1, observable2) { emissionO1, emissionO2 ->
        emissionO1 + emissionO2
    }.subscribe {
        println("Received $it")
    }
}

fun zippingExample2() {
    val observable1 = Observable.range(1, 10)
    val observable2 = listOf(
        "String 1",
        "String 2",
        "String 3",
        "String 4",
        "String 5",
        "String 6",
        "String 7",
        "String 8",
        "String 9",
        "String 10"
    ).toObservable()

    // Это котлиновская функция. Использует не bifunction, а функцию расширения/
    observable1.zipWith(observable2) { e1: Int, e2: String -> "$e2 $e1" }.subscribe {
        println("Received $it")
    }
}

/** Выпуск события когда вышла 2 события из каждого Observable */
fun zippingExample3() {
    val observable1 = Observable.interval(100, TimeUnit.MILLISECONDS)
    val observable2 = Observable.interval(250, TimeUnit.MILLISECONDS)

    Observable.zip(observable1, observable2, BiFunction { t1: Long, t2: Long -> "t1: $t1, t2: $t2" }).subscribe {
        println("Received $it")
    }

    Thread.sleep(1000)
}

/** Каждый новый элемент из первой эмиссии комбинируются с последним элементом из второй эмиссии (нет приоритета на один из observable)*/
fun zippingExample4() {
    val observable1 = Observable.interval(100, TimeUnit.MILLISECONDS)
    val observable2 = Observable.interval(250, TimeUnit.MILLISECONDS)

    Observable.combineLatest(observable1, observable2, BiFunction { t1: Long, t2: Long -> "t1: $t1, t2: $t2" })
        .subscribe {
            println("Received $it")
        }

    Thread.sleep(1000)
}

/** В результате имеем событие на каждое событие из onservable1, а из observable2 будет браться последнее значение. Отличается от combineLatest тем то что
 * тут есть приоритет к observable на котором применен оператор. */
fun zippingExample5() {
    val observable1 = Observable.interval(100, TimeUnit.MILLISECONDS)
    val observable2 = Observable.interval(250, TimeUnit.MILLISECONDS)

    observable1.withLatestFrom(observable2, BiFunction { t1: Long, t2: Long -> "t1: ${t1 + 100}, t2: $t2" })
        .subscribe { println("Received $it") }

    Thread.sleep(1000)
}


/** Merging*/

/** Merge выдаст результирующее событие когда один из внутренние observable выпусутит событие. Этим и отличается от zip */
fun mergingExample1() {
    val observable1 = Observable.interval(100, TimeUnit.MILLISECONDS)
    val observable2 = Observable.interval(250, TimeUnit.MILLISECONDS)

    Observable.merge(observable1, observable2).subscribe {
        println("Received $it")
    }

    Thread.sleep(1500)
}

/** перегрузка mergeWith*/
fun mergingExample2() {
    val observable1 = listOf("Kotlin", "Scala", "Groovy").toObservable()
    val observable2 = listOf("Python", "Java", "C++", "C").toObservable()
    observable1.mergeWith(observable2).subscribe {
        println("Received $it")
    }
}

/** merge для n observables*/
fun mergingExample3() {
    val observable1 = listOf("A", "B", "C").toObservable()
    val observable2 = listOf("D", "E", "F", "G").toObservable()
    val observable3 = listOf("I", "J", "K", "L").toObservable()
    val observable4 = listOf("M", "N", "O", "P").toObservable()
    val observable5 = listOf("Q", "R", "S", "T").toObservable()
    val observable6 = listOf("U", "V", "W", "X").toObservable()
    val observable7 = listOf("Y", "Z").toObservable()

    //принимает VarArgs
    Observable.mergeArray(
        observable1, observable2, observable3, observable4, observable5, observable6, observable7
    ).subscribe {
        println("Received $it")
    }
}

/** Grouping*/

/** Иногда возникает необходимость отправить сгруппированные данные
 *В коде сначала выделяется целевая группа с числами которые делятся на 5
 * затем выделяются другие группы*/
fun groupingExample() {
    val observable = Observable.range(1, 30)

    observable.groupBy {
        it % 5
    }.blockingSubscribe {
        println("Key ${it.key} ")
        it.subscribe {
            println("Received $it")
        }
    }
}

/** Concatenating*/

/** Эмиссия из второго observable начнется только после выпуска concat из первого observable.
 * Как и merge данный оператор имеет concatArray и concatWith варианты */
fun concatenatingExample1() {
    val observable1 = Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
//            .take(2)
        .map { "X $it" }

    val observable2 = Observable.just(11, 12, 13, 14, 15, 16, 17, 18, 19, 20).map { "Y $it" }

    Observable.concat(observable1, observable2).subscribe {
        println("Received $it")
    }

    Thread.sleep(50000)
}

/** Иногда работая с несколькими источниками данных возникает необходимость принимать события только от одного.
 * Для этого есть оператор amb() в переводе ambiguous(двусмысленный)
 * Будет работать тот эмиттер, который указан первым параметром.
 * Т.е если есть несколько источников мы можем переключаться между ними по нашему желанию.
 * Сначала принять и обработать данные с первого ситочника, затем еще раз зающать этот оператор и сделать тоже самое, но уже для второгоё
 * источника.
 * */

fun concatenatingExample2() {
    val observable1 = Observable.range(1, 10).map { "X $it" }
    val observable2 = Observable.range(50, 100).map { "Y $it" }

    Observable.amb(listOf(observable1, observable2)).subscribe {
        println("Received $it")
    }

    Thread.sleep(1500)
}

/** Scanning */

/** Оч похоже на котлиновскую функцию fold */
fun scanningExample1() {
    Observable.range(1, 10).scan { previousAccumulation, newEmission -> previousAccumulation + newEmission }
        .subscribe { println("Received $it") }
}

fun scanningExample2() {
    listOf("String 1", "String 2", "String 3", "String 4").toObservable()
        .scan { previousAccumulation, newEmission -> previousAccumulation + " " + newEmission }
        .subscribe { println("Received $it") }
}

fun scanningExample3() {
    Observable.range(1, 5).scan { previousAccumulation, newEmission -> previousAccumulation * 10 + newEmission }
        .subscribe { println("Received $it") }
}

/** flatting */
/** map возвращает Collection с примененной лямбдой на каждый элемент этой Collection */
/** flatMap возвращает Observable c 0,1 или n данными на каждый элемент эмиссии по цепочке выше, не поддерживает порядок эмиссии */

//empty
fun flatmapExample() {
    Observable.range(1, 10).flatMap {
        return@flatMap Observable.empty<Int>()
    }.blockingSubscribe {
        println("Received $it")
    }
}

//1 элемент на каждую эмиссию
fun flatmapExample1() {
    Observable.range(1, 10).flatMap {
        val randDelay = Random().nextInt(10)
        return@flatMap Observable.just(it).delay(randDelay.toLong(), TimeUnit.MILLISECONDS)
    }.blockingSubscribe {
        println("Received $it")
    }
}

//Несколько элементов на каждую эмиссию
fun flatmapExample2() {
    Observable.range(1, 10).flatMap {
        val randDelay = Random().nextInt(10)
        return@flatMap Observable.just("A", it).delay(randDelay.toLong(), TimeUnit.MILLISECONDS)
    }.blockingSubscribe {
        println("Received $it")
    }
}

/** Сoncat map брат близнец flatmap, но с поддержкой порядка эмиссии. Выполняется медленнее чем flatmap*/
fun concatMapExample() {
    Observable.range(1, 10).concatMap {
        val randDelay = Random().nextInt(10)
        return@concatMap Observable.just(it).delay(randDelay.toLong(), TimeUnit.MILLISECONDS)
    }.blockingSubscribe {
        println("Received $it")
    }
}

/**
 * Содержит в себе поведение flatmap и concatmap. Т.е может вернуть Observable с 0, 1 ... N количеством данных на каждый элемент эмиссии по цепочке выше.
 * + Поддерживает порядок эмиссии. Но если возвращаемый observable будет с задержкой - вернется последний, а предыдущие пропустятся.
 *
 * Он лучше всего подходит, для ситуаций где необходимо проигнорировать промежуточные результаты и рассмотреть последний (поиск).
 * SwitchMap отписывается от предыдущего источника Observable всякий раз, когда новый элемент начинает излучать данные,
 * тем самым всегда эмитит данные из текущего Observable */
fun switchMapExample() {
    println("Without delay \n")
    Observable.range(1, 10) //данная эмиссия будет происходить последовательно, как в concatMap.
        .switchMap {
            return@switchMap Observable.just(it, "ASD")
        }.blockingSubscribe { println("Received $it") }
}

fun switchMapExample2() {
    println("\nWith delay \n")
    Observable.range(1, 10).switchMap {
        val randDelay = Random().nextInt(10)
        return@switchMap Observable.just(it, "XYZ").delay(randDelay.toLong(), TimeUnit.MILLISECONDS)
    }.blockingSubscribe {
        println("Received $it")
    }
}

fun switchMapExample3() {
    println("\nWith delay and error \n")
    Observable.range(1, 10).switchMap { counter ->
        val randDelay = Random().nextInt(10)
        return@switchMap Observable.create {
            it.onNext(counter)
            it.onError(RuntimeException("Error"))
            it.onNext("ASD")
        }
            .onErrorReturn { "Error from onErrorReturn" } // обязательно должен присутствовать иначе приложение крашнится даже если есть subscbe с обработкой ошибок.
            .delay(randDelay.toLong(), TimeUnit.MILLISECONDS)
    }.blockingSubscribe({
        println("Received $it")
    }, {
        println("Error")
    })
}