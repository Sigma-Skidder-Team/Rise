package rise.packet.impl.c2s.protection

import com.google.gson.JsonObject
import rise.JsonBuilder
import rise.packet.api.C2SPacket

class C2SPacketConfirmServer(val ip: String, val port: Int, val username: String) : C2SPacket(3) {
    override fun dataExport(): JsonObject {
        return JsonBuilder()
            .a("a", this.ip)
            .a("b", this.port)
            .a("c", this.username)
            .build()
    }
}