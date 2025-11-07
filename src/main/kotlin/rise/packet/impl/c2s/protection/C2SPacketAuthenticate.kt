package rise.packet.impl.c2s.protection

import com.google.gson.JsonObject
import rise.packet.api.C2SPacket

class C2SPacketAuthenticate(
    val username: String,
    val hwid: String,
    val hazeID: String = "",
    val product: Int = 0
) : C2SPacket(1) {
    override fun dataExport(): JsonObject {
        val data = JsonObject()
        data.addProperty("a", this.username)
        data.addProperty("b", this.hwid)
        data.addProperty("c", this.product)
        data.addProperty("d", this.hazeID)
        return data
    }

}