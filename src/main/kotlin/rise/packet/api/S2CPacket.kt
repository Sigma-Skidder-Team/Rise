package rise.packet.api

abstract class S2CPacket(val id: Byte) {
    abstract fun handle(conn: NetHandler): Unit
}