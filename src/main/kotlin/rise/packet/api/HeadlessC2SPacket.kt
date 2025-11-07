package rise.packet.api

import com.google.gson.JsonObject

/**
 * A packet without any extra data
 */
abstract class HeadlessC2SPacket(id: Byte) : C2SPacket(id) {
    override fun dataExport(): JsonObject {
        return JsonObject()
    }
}