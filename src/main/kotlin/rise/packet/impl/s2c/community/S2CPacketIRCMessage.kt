package rise.packet.impl.s2c.community

import com.google.gson.JsonObject
import rise.packet.api.S2CPacket

class S2CPacketIRCMessage(val author: String, val product: Int, val message: String) : S2CPacket(4) {
    constructor(j: JsonObject) : this(
        j.get("a").asString,
        j.get("b").asInt,
        j.get("c").asString
    )

    override fun toString(): String {
        return "S2CPacketIRCMessage(author='$author', product=$product, message='$message')"
    }
}