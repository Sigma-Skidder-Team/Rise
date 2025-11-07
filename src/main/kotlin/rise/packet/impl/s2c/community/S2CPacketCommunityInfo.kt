package rise.packet.impl.s2c.community

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import rise.packet.api.NetHandler
import rise.packet.api.S2CPacket

class S2CPacketCommunityInfo(
    val type: String,
    val configs: JsonArray?,
    val scripts: JsonArray?
) : S2CPacket(11) {
    constructor(json: JsonObject) : this(
        json.get("c").asString,
        json.get("a")?.asJsonArray,
        json.get("b")?.asJsonArray
    )

    override fun handle(conn: NetHandler) {
    }

    override fun toString(): String {
        return "S2CPacketCommunityInfo(type='$type', configs=$configs, scripts=$scripts)"
    }
}
