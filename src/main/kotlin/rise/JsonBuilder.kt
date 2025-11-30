package rise

import com.google.gson.JsonElement
import com.google.gson.JsonObject

internal class JsonBuilder(private val j: JsonObject = JsonObject()) {
    operator fun set(key: String, value: String) = j.addProperty(key, value)
    operator fun set(key: String, value: Number) = j.addProperty(key, value)
    operator fun set(key: String, value: Boolean) = j.addProperty(key, value)
    operator fun set(key: String, value: Char) = j.addProperty(key, value)
    operator fun set(key: String, value: JsonElement) = j.add(key, value)
    operator fun get(key: String): JsonElement? = j.get(key)

    fun a(key: String, value: String): JsonBuilder {
        j.addProperty(key, value)
        return this
    }
    fun a(key: String, value: Number): JsonBuilder {
        j.addProperty(key, value)
        return this
    }
    fun a(key: String, value: Boolean): JsonBuilder {
        j.addProperty(key, value)
        return this
    }
    fun a(key: String, value: Char): JsonBuilder {
        j.addProperty(key, value)
        return this
    }
    fun a(key: String, value: JsonElement): JsonBuilder {
        j.add(key, value)
        return this
    }

    fun build(): JsonObject {
        return j
    }
}