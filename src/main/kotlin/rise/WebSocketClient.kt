package rise

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import jakarta.websocket.ClientEndpoint
import jakarta.websocket.CloseReason
import jakarta.websocket.OnClose
import jakarta.websocket.OnMessage
import jakarta.websocket.OnOpen
import jakarta.websocket.Session
import org.glassfish.tyrus.client.ClientManager
import rise.packet.api.C2SPacket
import rise.packet.impl.c2s.general.C2SPacketKeepAlive
import java.net.URI
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

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

    private val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val packetListeners = mutableSetOf<PacketListener>()
    private val handshakeListeners = mutableSetOf<Callback>()
    var session: Session? = null
    inline val connected
        get() = session != null

    fun addPacketListener(listener: PacketListener) {
        if (connected)
            error("addPacketListener called after connecting")
        packetListeners.add(listener)
    }

    fun addHandshakeListener(listener: Callback) {
        if (connected)
            error("addHandshakeListener called after connecting")
        handshakeListeners.add(listener)
    }

    fun connect() {
        val cm = ClientManager.createClient()

        cm.connectToServer(this, SERVER_URL)
    }

    fun disconnect(): Boolean = runCatching {
        session?.close() ?: return@runCatching false
        return@runCatching true
    }.getOrElse { false }

    fun disconnect(reason: CloseReason): Boolean = runCatching {
        session?.close(reason) ?: return@runCatching false
        return@runCatching true
    }.getOrElse { false }

    @OnOpen
    @Suppress("unused")
    fun onOpen(session: Session) {
        this.session = session
        println("Connection opened!")
        scheduler.scheduleAtFixedRate({
            try {
                send(C2SPacketKeepAlive)
            } catch (e: Exception) {
            }
        }, 1, 1, TimeUnit.SECONDS)
        handshakeListeners.forEach { it() }
    }

    @OnClose
    @Suppress("unused")
    fun onClose() {
        scheduler.shutdownNow()
    }

    @OnMessage
    @Suppress("unused")
    fun onMessage(msg: String) {
        if (session == null) {
            println("Server tried to send a message while session is null..?")
            return
        }
        val j = gson.fromJson(msg, JsonObject::class.java)

        val parsed = PacketHandler.parse(j)
        packetListeners.forEach { it(parsed) }
    }

    private fun sendRaw(data: String) {
        val session = session ?: error("WebSocketClient.sendRaw(String) called while session is null!")
        session.asyncRemote.sendText(encrypt(data))
    }

    @Suppress("unused")
    fun sendBinary(data: ByteArray) {
        val session = session ?: error("WebSocketClient.sendRaw(ByteArray) called while session is null!")
        session.asyncRemote.sendBinary(ByteBuffer.wrap(data))
    }

    fun send(packet: C2SPacket) {
        if (session == null)
            error("WebSocketClient.send(C2SPacket) called while session is null!")
        sendRaw(packet.export())
    }
}