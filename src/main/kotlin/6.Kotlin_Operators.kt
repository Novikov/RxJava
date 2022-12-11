fun main() {
//    mapFun()
//    flatmapFun()
    foldFun()
}

//отображения
fun mapFun() {
    val arr = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
    val result = arr.map { it * 10 }

    println(result)
}

//На каждый элемент выпускает iterable c 1, несколькими ил нулевым количеством элементов
//Array это не iterable
fun flatmapFun() {
    val arr1 = mutableListOf(1, 2, 3)
    val arr2 = mutableListOf(1, 2, 3)
    val arr3 = mutableListOf(1, 2, 3)
    val listOList = mutableListOf(arr1, arr2, arr3)

    println(listOList) //не сглаженная коллекция
    println(listOList.flatten()) //сглаживание коллекции
    println(listOList.map { it.subList(0, 1) }.flatten()) //одновременно сглаживаем и трансформируем
    println(listOList.flatMap  //одновременно сглаживаем и трансформируем
    {
        it.subList(0, 1)
    })
}

//работа с накопителем
fun foldFun() {
    val total = listOf(1, 2, 3, 4, 5).fold(0, { total, next -> total + next })
    println("total: " + total)

    val mul = listOf(1, 2, 3, 4, 5).fold(1, { mul, next -> mul * next })
    println("mul: " + mul)
}