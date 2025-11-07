package rise.packet.impl.c2s.protection

import com.google.gson.JsonObject
import rise.packet.api.C2SPacket

class C2SPacketConfirmServer(val ip: String, val port: Int, val username: String) : C2SPacket(3) {
    override fun dataExport(): JsonObject {
        val json = JsonObject()
        json.addProperty("a", this.ip)
        json.addProperty("b", this.port)
        json.addProperty("c", this.username)
        return json
    }
}