package rise.packet.api

import com.google.gson.JsonObject

abstract class C2SPacket(val id: Byte) {
    /** exports the actual data of this packet, the [export] function adds the ID for you **/
    protected abstract fun dataExport(): JsonObject
    /**
     * exports this packet's data to a JSON Object.
     * This calls [dataExport] and adds an ID to the object.
     **/
    fun export(): String {
        val dx = dataExport()
        dx.addProperty("id", id)
        return dx.toString()
    }
}