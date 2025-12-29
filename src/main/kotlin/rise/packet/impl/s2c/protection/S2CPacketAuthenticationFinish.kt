package rise.packet.impl.s2c.protection

import com.google.gson.JsonObject
import rise.packet.api.S2CPacket

class S2CPacketAuthenticationFinish(
    val success: Boolean,
    val reason: String,
    val aod: AuthenticatedOnlyData?
) : S2CPacket(1) {

    @JvmRecord
    data class AuthenticatedOnlyData(
        val pi: Double = Math.PI,
        val maxPitch: Float = 90f,
        val serverTimeMS: Long = System.currentTimeMillis()
    ) {}
    constructor(json: JsonObject) : this(
        json.get("a").asBoolean,
        json.get("e").asString,
        if (json.get("a").asBoolean) AuthenticatedOnlyData(
            json.get("b").asDouble,
            json.get("c").asFloat,
            json.get("d").asLong,
        ) else null,
    )

    override fun toString(): String {
        return "S2CPacketAuthenticationFinish(success=$success, reason='$reason', pi=$pi, maxPitch=$maxPitch, serverTimeMS=$serverTimeMS)"
    }
}
