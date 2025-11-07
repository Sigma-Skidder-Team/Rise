package rise.packet.impl.s2c.protection

import com.google.gson.JsonObject
import rise.packet.api.S2CPacket

class S2CPacketJoinServer(val ip: String, val port: Int) : S2CPacket(3) {
    constructor(json: JsonObject) : this(
        json.get("a").asString,
        json.get("b").asInt
    )

    override fun toString(): String {
        return "S2CPacketJoinServer(ip='$ip', port=$port)"
    }
}
