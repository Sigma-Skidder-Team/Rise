package rise.packet.impl.s2c.community

import com.google.gson.JsonObject
import rise.packet.api.NetHandler
import rise.packet.api.S2CPacket

class S2CPacketTabIRC(val data: String) : S2CPacket(6) {
    constructor(json: JsonObject) : this(
        json.get("a").asString
    )

    override fun handle(conn: NetHandler) {
    }

    override fun toString(): String {
        return "S2CPacketTabIRC(data='$data')"
    }
}
