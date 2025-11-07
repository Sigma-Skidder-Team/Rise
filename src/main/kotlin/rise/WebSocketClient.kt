package rise

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import jakarta.websocket.ClientEndpoint
import jakarta.websocket.OnMessage
import jakarta.websocket.OnOpen
import jakarta.websocket.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.glassfish.tyrus.client.ClientManager
import rise.packet.api.C2SPacket
import rise.packet.api.S2CPacket
import rise.packet.impl.c2s.community.asIRCMessage
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
import java.net.URI
import java.nio.ByteBuffer
import kotlin.time.Duration.Companion.milliseconds

typealias PacketListener = (packet: S2CPacket) -> Unit

// 98% of this WAS from Waffler527, before I rewrote it in a different library in order to stop the complaints.
// I got blocked and ignored because I forgot this 1 line above before I rewrote it...
@ClientEndpoint(configurator = ClientConfigurator::class)
class WebSocketClient {
    companion object {
        private val gson: Gson = GsonBuilder().create()
//        private const val RECONNECT_DELAY = 3000L
        private const val SERVER = "wss://auth.riseclient.com:8443"
        private val SERVER_URL = URI.create(SERVER)
    }
    private val packetListeners = mutableSetOf<PacketListener>()
    private val handshakeListeners = mutableSetOf<() -> Unit>()
    private var session: Session? = null

    fun addPacketListener(listener: PacketListener) {
        packetListeners.add(listener)
    }

    fun addHandshakeListener(listener: () -> Unit) {
        handshakeListeners.add(listener)
    }

    fun connect() {
        val cm = ClientManager.createClient()

        cm.connectToServer(this, SERVER_URL)
    }

    @OnOpen
    fun onOpen(session: Session) {
        this.session = session
        println("Connection opened!")
        handshakeListeners.callEach()
    }

    @OnMessage
    fun onMessage(msg: String) {
        if (session == null) {
            println("Server tried to send a message while session is null..?")
            return
        }
        val j = gson.fromJson(msg, JsonObject::class.java)

        val parsed = PacketHandler.parse(j)
        packetListeners.callEach1(parsed)
    }

    internal fun sendRaw(data: String) {
        val session = session ?: error("WebSocketClient.sendRaw(String) called while session is null!")
        session.asyncRemote.sendText(encrypt(data))
    }

    internal fun sendRaw(data: ByteArray) {
        val session = session ?: error("WebSocketClient.sendRaw(ByteArray) called while session is null!")
        session.asyncRemote.sendBinary(ByteBuffer.wrap(data))
    }

    fun send(packet: C2SPacket) {
        if (session == null)
            error("WebSocketClient.send(C2SPacket) called while session is null!")
        sendRaw(packet.export())
    }
}


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