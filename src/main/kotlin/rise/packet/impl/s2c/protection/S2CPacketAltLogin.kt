package rise.packet.impl.s2c.protection

import com.google.gson.JsonObject
import rise.packet.api.S2CPacket
import rise.packet.impl.c2s.protection.C2SPacketAltLogin

class S2CPacketAltLogin(
    val username: String,
    val uuid: String,
    val accessToken: String,
    val refreshToken: String,
    val altSkin: C2SPacketAltLogin.AltSkin? = null
) : S2CPacket(4) {

    constructor(json: JsonObject) : this(
        json.get("a").asString,
        json.get("b").asString,
        json.get("c").asString,
        json.get("d").asString,
        json.get("e")?.let {
            // they don't declare it as null, but in the docs they say it can be null...
            // Java users fml
            if (it == null)
                return@let null
            C2SPacketAltLogin.AltSkin(it.asString)
        }
    )
}
