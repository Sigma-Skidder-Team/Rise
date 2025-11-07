package rise.packet.impl.c2s.protection

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import rise.packet.api.C2SPacket

class C2SPacketFilterEntities(
    val entityList: List<Entity>,
    val players: Boolean,
    val invisibles: Boolean,
    val animals: Boolean,
    val mobs: Boolean,
    val uid: Int
) : C2SPacket(8) {
    override fun dataExport(): JsonObject {
        val json = JsonObject()
        val array = JsonArray()

        for (entity in this.entityList) {
            val entityJson = JsonObject()
            entityJson.addProperty("a", entity.entityId)
            entityJson.addProperty("b", entity.type)
            entityJson.addProperty("c", entity.invisible)
            array.add(entityJson)
        }

        json.add("a", array)
        json.addProperty("b", this.players)
        json.addProperty("c", this.invisibles)
        json.addProperty("d", this.animals)
        json.addProperty("e", this.mobs)
        json.addProperty("f", this.uid)

        return json
    }

    companion object {
        @JvmRecord
        data class Entity(
            val entityId: Int, // 0=player, 1=animal, 2=mob
            val type: Int, val invisible: Boolean
        )
    }
}