package rise.packet.impl.c2s.community

import com.google.gson.JsonObject
import rise.packet.api.C2SPacket

class C2SPacketLoadCloudConfig(val configID: String) : C2SPacket(10) {
    constructor(json: JsonObject) : this(json.get("a").asString)
    public override fun dataExport(): JsonObject {
        val json = JsonObject()
        json.addProperty("a", this.configID)
        return json
    }
}
