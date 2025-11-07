package rise

// this is literally just the same as if you serialized it to an array.
// awesome devs!

fun buildTabIRCList(contents: Set<String>): String {
    val result =
        "[${contents.joinToString { "\"$it\"" }}]"
    println(result)
    return result
}

fun <R> Iterable<Function0<R>>.callEach(): Iterable<R> {
    val returns = mutableListOf<R>()
    forEach { returns.add(it()) }
    return returns
}

fun <A1, R> Iterable<Function1<A1, R>>.callEach1(a1: A1): Iterable<R> {
    val returns = mutableListOf<R>()
    forEach { returns.add(it(a1)) }
    return returns
}