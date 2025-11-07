package rise

import kotlin.io.encoding.Base64

const val ENCRYPTION_KEY = "ふふぁふぃふにかんいかじぎざ"

fun encrypt(value: String, encryptionKey: String = ENCRYPTION_KEY): String {
    val keyBytes: ByteArray = encryptionKey.toByteArray()
    val valueBytes: ByteArray = value.toByteArray()
    val encryptedBytes = ByteArray(valueBytes.size)
    for (i in valueBytes.indices) {
        encryptedBytes[i] = (valueBytes[i].toInt() xor keyBytes[i % keyBytes.size].toInt()).toByte()
    }
    return Base64.encode(encryptedBytes)
}

fun decrypt(encryptedValue: String, encryptionKey: String = ENCRYPTION_KEY): String {
    val keyBytes: ByteArray = encryptionKey.toByteArray()
    val encryptedBytes: ByteArray = Base64.decode(encryptedValue)
    val decryptedBytes = ByteArray(encryptedBytes.size)
    for (i in encryptedBytes.indices) {
        decryptedBytes[i] = (encryptedBytes[i].toInt() xor keyBytes[i % keyBytes.size].toInt()).toByte()
    }
    return String(decryptedBytes, Charsets.UTF_8)
}
