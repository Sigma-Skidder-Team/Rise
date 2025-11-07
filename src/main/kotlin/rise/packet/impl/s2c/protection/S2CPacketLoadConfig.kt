package rise.packet.impl.s2c.protection

import com.google.gson.JsonObject
import rise.packet.api.S2CPacket

class S2CPacketLoadConfig(val config: String) : S2CPacket(2) {

    constructor(json: JsonObject) : this(json.get("a").asString)

    override fun toString(): String {
        return "S2CPacketLoadConfig(config='$config')"
    }
}
