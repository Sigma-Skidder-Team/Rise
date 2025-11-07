package rise

import com.google.gson.JsonObject
import rise.packet.api.IDToPacketConstructorRegistry
import rise.packet.api.S2CPacket

object PacketHandler {
    fun parse(j: JsonObject): S2CPacket {
        val id = j.get("id").asByte
        return IDToPacketConstructorRegistry.S2C[id]?.invoke(j) ?: error("Unknown packet (data $j)")
    }
}