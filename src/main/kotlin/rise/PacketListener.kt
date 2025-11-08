package rise

import rise.packet.api.S2CPacket

fun interface PacketListener {
    operator fun invoke(packet: S2CPacket)
}
