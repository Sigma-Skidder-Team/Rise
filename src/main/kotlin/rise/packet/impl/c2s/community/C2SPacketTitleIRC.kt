package rise.packet.impl.c2s.community

import com.google.gson.JsonObject
import rise.JsonBuilder
import rise.packet.api.C2SPacket

class C2SPacketTitleIRC(
    val message: String, val fadeInTime: Int = 0,
    val displayTime: Int = 0, val fadeOutTime: Int = 0,
    val color: String, val targets: String
) : C2SPacket(9) {
    override fun dataExport(): JsonObject {
        return JsonBuilder()
            .a("a", message)
            .a("b", fadeInTime)
            .a("c", displayTime)
            .a("d", fadeOutTime)
            .a("e", color)
            .a("f", targets)
            .build()
    }
}