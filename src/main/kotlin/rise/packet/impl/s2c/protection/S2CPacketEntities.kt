package rise.packet.impl.s2c.protection

import com.google.gson.JsonObject
import rise.packet.api.NetHandler
import rise.packet.api.S2CPacket
import rise.packet.impl.c2s.protection.C2SPacketFilterEntities.Companion.Entity

class S2CPacketEntities(val entities: MutableList<Entity>, val uid: Int) : S2CPacket(7) {
    constructor(json: JsonObject) : this(
        json.getAsJsonArray("a").let { entityArray ->
            val entityList: MutableList<Entity> = ArrayList()
            for (i in 0..<entityArray.size()) {
                val entityObj = entityArray.get(i).asJsonObject
                entityList.add(Entity(entityObj.get("a").asInt, -1, false))
            }
            return@let entityList
        },
        json.get("b").asInt
    )

    override fun handle(conn: NetHandler) {
    }

    override fun toString(): String {
        return "S2CPacketEntities(entities=$entities, uid=$uid)"
    }
}