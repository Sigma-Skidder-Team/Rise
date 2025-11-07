package rise.packet.api

interface NetHandler {
    fun sendPacket(pkt: C2SPacket): Unit
}