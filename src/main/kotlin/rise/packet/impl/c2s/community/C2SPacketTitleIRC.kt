package rise.packet.impl.c2s.community

import com.google.gson.JsonObject
import rise.packet.api.C2SPacket

class C2SPacketTitleIRC(
    val message: String, val fadeInTime: Int = 0,
    val displayTime: Int = 0, val fadeOutTime: Int = 0,
    val color: String, val targets: String
) : C2SPacket(9) {
    override fun dataExport(): JsonObject {
        val json = JsonObject()
        json.addProperty("a", this.message)
        json.addProperty("b", this.fadeInTime)
        json.addProperty("c", this.displayTime)
        json.addProperty("d", this.fadeOutTime)
        json.addProperty("e", this.color)
        json.addProperty("f", this.targets)
        return json
    }
}