package rise.packet.impl.s2c.community

import com.google.gson.JsonObject
import rise.packet.api.S2CPacket

class S2CPacketTitleIRC(
    val message: String, val fadeInTime: Int,
    val displayTime: Int, val fadeOutTime: Int,
    val color: String
) : S2CPacket(9) {
    constructor(json: JsonObject) : this(
        json.get("a").asString,
        json.get("b").asInt,
        json.get("c").asInt,
        json.get("d").asInt,
        json.get("e").asString
    )

    override fun toString(): String {
        return "S2CPacketTitleIRC(message='$message', fadeInTime=$fadeInTime, displayTime=$displayTime, fadeOutTime=$fadeOutTime, color='$color')"
    }
}