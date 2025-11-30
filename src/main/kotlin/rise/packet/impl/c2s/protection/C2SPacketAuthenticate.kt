package rise.packet.impl.c2s.protection

import com.google.gson.JsonObject
import rise.JsonBuilder
import rise.packet.api.C2SPacket

class C2SPacketAuthenticate @JvmOverloads constructor(
    val username: String,
    val hwid: String,
    val hazeID: String = "",
    val product: Int = 0
) : C2SPacket(1) {
    override fun dataExport(): JsonObject {
        return JsonBuilder()
            .a("a", this.username)
            .a("b", this.hwid)
            .a("c", this.product)
            .a("d", this.hazeID)
            .build()
    }

}