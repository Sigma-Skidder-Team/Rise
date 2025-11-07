package rise.packet.impl.s2c.community

import com.google.gson.JsonObject
import rise.packet.api.S2CPacket

class S2CPacketTroll(val killauraDisabled: Boolean, val reverseKeybinds: Boolean) : S2CPacket(10) {
    constructor(json: JsonObject) : this(
        json.get("a").asBoolean,
        json.get("b").asBoolean
    )

    override fun toString(): String {
        return "S2CPacketTroll(killauraDisabled=$killauraDisabled, reverseKeybinds=$reverseKeybinds)"
    }
}