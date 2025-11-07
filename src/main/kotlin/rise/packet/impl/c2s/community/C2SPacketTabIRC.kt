package rise.packet.impl.c2s.community

import com.google.gson.JsonObject
import rise.packet.api.C2SPacket

class C2SPacketTabIRC(val data: String) : C2SPacket(6) {
    override fun dataExport(): JsonObject {
        val json = JsonObject()
        json.addProperty("a", this.data)
        return json
    }
}