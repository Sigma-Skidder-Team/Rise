package rise.packet.impl.s2c.protection

import com.google.gson.JsonObject
import rise.packet.api.S2CPacket

class S2CPacketAuthenticationFinish(
    val success: Boolean,
    val reason: String,
    val pi: Double = Math.PI,
    val maxPitch: Float = 90f,
    val serverTimeMS: Long = System.currentTimeMillis()
) : S2CPacket(1) {
    constructor(json: JsonObject) : this(
        json.get("a").asBoolean,
        json.get("e").asString,
        json.get("b").asDouble,
        json.get("c").asFloat,
        json.get("d").asLong,
    )

    override fun toString(): String {
        return "S2CPacketAuthenticationFinish(success=$success, reason='$reason', pi=$pi, maxPitch=$maxPitch, serverTimeMS=$serverTimeMS)"
    }
}
