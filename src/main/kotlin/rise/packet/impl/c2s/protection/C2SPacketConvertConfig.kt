package rise.packet.impl.c2s.protection

import com.google.gson.JsonObject
import rise.packet.api.C2SPacket

class C2SPacketConvertConfig(val config: String) : C2SPacket(2) {
    override fun dataExport(): JsonObject {
        val json = JsonObject()
        json.addProperty("a", config)
        return json
    }
}