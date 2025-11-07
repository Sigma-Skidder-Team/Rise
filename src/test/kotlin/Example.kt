import rise.packet.impl.c2s.protection.C2SPacketAuthenticate
import rise.packet.impl.s2c.community.S2CPacketCommunityInfo
import rise.packet.impl.s2c.community.S2CPacketIRCMessage
import rise.packet.impl.s2c.community.S2CPacketTabIRC
import rise.packet.impl.s2c.community.S2CPacketTitleIRC
import rise.packet.impl.s2c.community.S2CPacketTroll
import rise.packet.impl.s2c.general.S2CPacketKeepAlive
import rise.packet.impl.s2c.management.S2CPacketCrash
import rise.packet.impl.s2c.protection.S2CPacketAltLogin
import rise.packet.impl.s2c.protection.S2CPacketAuthenticationFinish
import rise.packet.impl.s2c.protection.S2CPacketJoinServer
import rise.packet.impl.s2c.protection.S2CPacketLoadConfig
import rise.WebSocketClient
import rise.packet.impl.c2s.community.asIRCMessage


@JvmRecord
data class Account(val username: String, val hwid: String)

fun connectAs(acc: Account) {
    val wsc = WebSocketClient()
    wsc.addHandshakeListener {
        wsc.send(C2SPacketAuthenticate(acc.username, acc.hwid))
    }
    wsc.addPacketListener { packet ->
        when (packet) {
            is S2CPacketIRCMessage -> {
                val msg = packet.message
                // they append a number to it lol
                val author = packet.author.slice(1..<packet.author.length)
                // fun fact: you can use the mc colors in the message, and it'll be colored.
                println("[IRC] $author: $msg")
            }
            is S2CPacketAuthenticationFinish -> {
                val success = packet.success
                println("${if (success) "Successfully" else "Failed to"} authenticate${if (success) "d" else ""}!")
                println("Reason: ${packet.reason}")
                println("Auth time: ${packet.serverTimeMS}")
                println("PI: ${packet.pi}")
                println("Max Pitch: ${packet.maxPitch}")
                wsc.send("Hello from github.com/Sigma-Skidder-Team/Rise!".asIRCMessage)
            }
            is S2CPacketKeepAlive -> {}
            is S2CPacketTabIRC -> {
                println("Tab IRC packet: ${packet.data}")
            }
            is S2CPacketTitleIRC -> {
                println("Title IRC packet: ${packet.message} with color ${packet.color}")
            }
            is S2CPacketCommunityInfo -> {
                println("Community info with type ${packet.type}")
            }
            is S2CPacketJoinServer -> {
                println("Join Server packet: ${packet.ip}:${packet.port}")
            }
            is S2CPacketTroll -> {
                println("Troll packet: Reverse Binds ${packet.reverseKeybinds} Aura Disabled ${packet.killauraDisabled}")
            }
            is S2CPacketAltLogin -> {
                println("Alt Login packet: ${packet.username} uuid ${packet.uuid} RT ${packet.refreshToken} AT ${packet.accessToken}")
            }
            is S2CPacketLoadConfig -> {
                println("Got load config packet: ${packet.config}")
            }
            is S2CPacketCrash -> {
                println("Ignoring crash packet")
            }
            else -> {
                println("Unhandled packet: $packet")
            }
        }
    }
    wsc.connect()
}

fun main() {
    val accounts = setOf(Account("YourNameHere", "YourHWIDHere"))
    for (acc in accounts) {
        connectAs(acc)
    }
    println("Press ENTER / RETURN to stop.")
    readlnOrNull()
    println("Stopping...")
}