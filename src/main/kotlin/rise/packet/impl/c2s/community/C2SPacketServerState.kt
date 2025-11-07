package rise.packet.impl.c2s.community

import com.google.gson.JsonObject
import rise.packet.api.C2SPacket

class C2SPacketServerState(val serverIP: String) : C2SPacket(5) {
    override fun dataExport(): JsonObject {
        val data = JsonObject()
        data.addProperty("a", this.serverIP)
        return data
    }
}