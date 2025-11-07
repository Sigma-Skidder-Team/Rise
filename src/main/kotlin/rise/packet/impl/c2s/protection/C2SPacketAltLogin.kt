package rise.packet.impl.c2s.protection

import com.google.gson.JsonObject
import rise.packet.api.C2SPacket

class C2SPacketAltLogin(val refreshToken: String, val altSkin: AltSkin) : C2SPacket(4) {
    override fun dataExport(): JsonObject {
        val j = JsonObject()
        j.addProperty("a", refreshToken)
        return j
    }

    @JvmRecord
    data class AltSkin(private val skin: String)
}