package rise

import com.google.gson.JsonObject
import io.netty.channel.Channel
import rise.packet.impl.s2c.community.*
import rise.packet.impl.s2c.protection.S2CPacketAuthenticationFinish
import rise.packet.impl.s2c.protection.S2CPacketEntities
import rise.packet.impl.s2c.protection.S2CPacketJoinServer
import rise.packet.impl.s2c.protection.S2CPacketLoadConfig

object PacketHandler {
    fun handle(channel: Channel, j: JsonObject) {
        when (val id = j.get("id").asByte) {
            0.toByte() -> {
                val self = "Testing"
                println("Keep alive")
            }
            1.toByte() -> {
                val packet = S2CPacketAuthenticationFinish(j)
                println("Auth finish: $packet")

            }
            2.toByte() -> {
                val packet = S2CPacketLoadConfig(j)
                println("Load config: $packet")
            }
            3.toByte() -> {
                val packet = S2CPacketJoinServer(j)
                println(packet)
            }
            4.toByte() -> {
                val packet = S2CPacketIRCMessage(j)
                println("IRC message: $packet")
            }
            5.toByte() -> {
                println("Ignoring crash packet")
            }
            6.toByte() -> {
                val packet = S2CPacketTabIRC(j)
                println("Tab IRC: $packet")
            }
            7.toByte() -> {
                val packet = S2CPacketEntities(j)
                println("Entities: $packet")
            }
            9.toByte() -> {
                val packet = S2CPacketTitleIRC(j)
                println("Title IRC: $packet")
            }
            10.toByte() -> {
                val packet = S2CPacketTroll(j)
                println("Ignoring troll packet: $packet")
            }
            11.toByte() -> {
                val packet = S2CPacketCommunityInfo(j)
                println("Community info: $packet")
            }
            else -> {
                println("Unknown packet with ID $id")
            }
        }
    }
}