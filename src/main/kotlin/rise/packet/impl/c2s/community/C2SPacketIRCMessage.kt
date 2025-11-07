package rise.packet.impl.c2s.community

import com.google.gson.JsonObject
import rise.packet.api.C2SPacket

class C2SPacketIRCMessage(val message: String) : C2SPacket(4) {
    companion object {
        const val PRODUCT_RISE = "63d0f9bc46ca6bf7ad9572b7"
    }
    override fun dataExport(): JsonObject {
        val j = JsonObject()
        j.addProperty("a", this.message)
        j.addProperty("b", PRODUCT_RISE)
        return j
    }
}
val String.asIRCMessage
    get() = C2SPacketIRCMessage(this)
