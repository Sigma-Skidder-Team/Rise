package rise

// this is literally just the same as if you serialized it to an array.
// awesome devs!

fun buildTabIRCList(contents: Set<String>): String {
    val result =
        "[${contents.joinToString { "\"$it\"" }}]"
    println(result)
    return result
}