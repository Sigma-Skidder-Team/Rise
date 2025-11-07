package rise.packet.impl.s2c.general

import rise.packet.api.S2CPacket

object S2CPacketKeepAlive : S2CPacket(11) {
    override fun toString(): String {
        return "S2CPacketKeepAlive()"
    }
}