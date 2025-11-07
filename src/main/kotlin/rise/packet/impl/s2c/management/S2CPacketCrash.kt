package rise.packet.impl.s2c.management

import rise.packet.api.NetHandler
import rise.packet.api.S2CPacket

class S2CPacketCrash : S2CPacket(5) {
    override fun handle(conn: NetHandler) {
        println("Not crashing.")
    }
}
