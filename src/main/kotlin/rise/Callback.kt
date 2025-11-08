package rise

/**
 * I made this instead of a `() -> Unit` because with that type, you'd have to do `return Unit.INSTANCE`
 * with this, Kotlin doesn't have to return anything explicitly, nor Java!
 **/
fun interface Callback {
    operator fun invoke()
}