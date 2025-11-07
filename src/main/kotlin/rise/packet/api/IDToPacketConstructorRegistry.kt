package rise.packet.api

import com.google.gson.JsonObject
import rise.packet.impl.s2c.community.S2CPacketCommunityInfo
import rise.packet.impl.s2c.community.S2CPacketIRCMessage
import rise.packet.impl.s2c.community.S2CPacketTabIRC
import rise.packet.impl.s2c.community.S2CPacketTitleIRC
import rise.packet.impl.s2c.community.S2CPacketTroll
import rise.packet.impl.s2c.general.S2CPacketKeepAlive
import rise.packet.impl.s2c.management.S2CPacketCrash
import rise.packet.impl.s2c.protection.S2CPacketAuthenticationFinish
import rise.packet.impl.s2c.protection.S2CPacketEntities
import rise.packet.impl.s2c.protection.S2CPacketJoinServer
import rise.packet.impl.s2c.protection.S2CPacketLoadConfig

typealias PacketConstructor = (data: JsonObject) -> S2CPacket

object IDToPacketConstructorRegistry {
    @JvmStatic
    val S2C: Map<Byte, PacketConstructor> = mapOf(
        0.toByte() to { S2CPacketKeepAlive },
        1.toByte() to { S2CPacketAuthenticationFinish(it) },
        2.toByte() to { S2CPacketLoadConfig(it) },
        3.toByte() to { S2CPacketJoinServer(it) },
        4.toByte() to { S2CPacketIRCMessage(it) },
        5.toByte() to { S2CPacketCrash },
        6.toByte() to { S2CPacketTabIRC(it) },
        7.toByte() to { S2CPacketEntities(it) },
        // hello @billionaire!!! you skipped 1 byte fix now!!!
        9.toByte() to { S2CPacketTitleIRC(it) },
        10.toByte() to { S2CPacketTroll(it) },
        11.toByte() to { S2CPacketCommunityInfo(it) }
    )
}
