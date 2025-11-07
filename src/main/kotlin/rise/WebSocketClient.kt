package rise

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import io.netty.bootstrap.Bootstrap
import io.netty.channel.*
import io.netty.channel.nio.NioIoHandler
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http.DefaultHttpHeaders
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.HttpClientCodec
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.websocketx.*
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rise.packet.api.C2SPacket
import rise.packet.api.S2CPacket
import rise.packet.impl.c2s.community.asIRCMessage
import rise.packet.impl.c2s.general.C2SPacketKeepAlive
import rise.packet.impl.c2s.protection.C2SPacketAuthenticate
import rise.packet.impl.s2c.community.S2CPacketIRCMessage
import rise.packet.impl.s2c.general.S2CPacketKeepAlive
import rise.packet.impl.s2c.protection.S2CPacketAuthenticationFinish
import java.net.URI
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds

typealias PacketListener = (packet: S2CPacket) -> Unit

// 98% of this is from Waffler527 lol
// I got blocked and ignored because I forgot this 1 line above...
// I would rewrite it,
// but there's no way to actually rewrite this without niggers complaining about it still not being mine,
// since it's literally just connecting to a WebSocket (which is very simple)...
class WebSocketClient {
    companion object {
        private val gson: Gson = GsonBuilder().create()
//        private const val RECONNECT_DELAY = 3000L
        private const val SERVER_URL = "wss://auth.riseclient.com:8443"
    }
    private val factory = NioIoHandler.newFactory()
    private val group = MultiThreadIoEventLoopGroup(factory)
    var channel: Channel? = null
    private val packetListeners = mutableSetOf<PacketListener>()
    private val handshakeListeners = mutableSetOf<() -> Unit>()

    fun addPacketListener(listener: PacketListener) {
        packetListeners.add(listener)
    }

    fun addHandshakeListener(listener: () -> Unit) {
        handshakeListeners.add(listener)
    }

    fun connect() {
        val uri = URI(SERVER_URL)
        val sslCtx = SslContextBuilder.forClient()
            .trustManager(InsecureTrustManagerFactory.INSTANCE)
            .build()

        val handshaker = WebSocketClientHandshakerFactory.newHandshaker(
            uri,
            WebSocketVersion.V13,
            null,
            true,
            //the awesome billionaire detection method
            //of adding headers (since at most 6.1.30!!!)
            //and only checking for them eons later
            DefaultHttpHeaders().add("gdfg", "fdsgh")
        )
        val handler = WebSocketClientHandler(handshaker)

        val bootstrap = Bootstrap()
            .group(group)
            .channel(NioSocketChannel::class.java)
            .handler(object : ChannelInitializer<Channel>() {
                override fun initChannel(ch: Channel) {
                    val p = ch.pipeline()
                    p.addLast(sslCtx.newHandler(ch.alloc(), uri.host, uri.port))
                    p.addLast(HttpClientCodec(), HttpObjectAggregator(8192), handler)
                    p.addLast(XorEncoder())
                }
            })

        bootstrap.connect(uri.host, uri.port).addListener { f ->
            if (!f.isSuccess) {
                error("Connection failed: ${f.cause()?.message}")
            } else {
                channel = (f as ChannelFuture).channel()
            }
        }
    }

    internal inner class WebSocketClientHandler(private val handshaker: WebSocketClientHandshaker) :
        SimpleChannelInboundHandler<Any>() {

        private var handshakeFuture: ChannelPromise? = null
        private var keepAliveTask: ScheduledFuture<*>? = null

        override fun handlerAdded(ctx: ChannelHandlerContext) {
            handshakeFuture = ctx.newPromise()
        }

        override fun channelActive(ctx: ChannelHandlerContext) {
            handshaker.handshake(ctx.channel())
        }

        override fun channelInactive(ctx: ChannelHandlerContext) {
            keepAliveTask?.cancel(true)
        }

        override fun channelRead0(ctx: ChannelHandlerContext, msg: Any) {
            val ch = ctx.channel()

            if (!handshaker.isHandshakeComplete) {
                try {
                    handshaker.finishHandshake(ch, msg as FullHttpResponse)
                    handshakeFuture!!.setSuccess()
                    scheduleKeepAlive(ctx)
                    this@WebSocketClient.handshakeListeners.forEach(Function0<Unit>::invoke)
                } catch (e: Exception) {
                    println("Handshake failed: ${e.message}")
                    handshakeFuture!!.setFailure(e)
                    ctx.close()
                }
                return
            }

            if (msg is FullHttpResponse) error("Unexpected HTTP response: $msg")

            when (msg) {
                is TextWebSocketFrame -> {
                    val j = gson.fromJson(msg.text(), JsonObject::class.java)
                    val packet = PacketHandler.parse(j)
                    this@WebSocketClient.packetListeners.forEach { it(packet) }
                }
                is CloseWebSocketFrame -> {
                    ch.close()
                }
            }
        }

        private fun scheduleKeepAlive(ctx: ChannelHandlerContext) {
            keepAliveTask = ctx.channel().eventLoop().scheduleAtFixedRate({
                if (ctx.channel().isOpen) {
                    ctx.channel().writeAndFlush(TextWebSocketFrame(C2SPacketKeepAlive.export()))
                }
            }, 5, 5, TimeUnit.SECONDS)
        }

        override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
            println("Error: ${cause.message}")
            ctx.close()
        }
    }

    fun send(packet: C2SPacket) {
        val channel = channel ?: error("WebSocketClient.send called while null!")
        channel.writeAndFlush(TextWebSocketFrame(packet.export()))
    }
}

fun connectAs(username: String) {
    val wsc = WebSocketClient()
    val exempted = username == "billionaire"
    wsc.addHandshakeListener {
        wsc.send(C2SPacketAuthenticate(username, "ok lol"))
    }
    wsc.addPacketListener { packet ->
        when (packet) {
            is S2CPacketIRCMessage -> {
                val msg = packet.message
                // they append a number to it lol
                val author = packet.author.slice(1..<packet.author.length)
                println("[IRC] $author: $msg")
                CoroutineScope(Dispatchers.IO).launch {
                    wsc.send("@$author Proof of $msg? ${(1..3).random()}".asIRCMessage)
                    if (!exempted) delay(3.01e3.milliseconds)
                    wsc.send("@$author 0".asIRCMessage)
                    if (!exempted) delay(3.01e3.milliseconds)
                    if (!exempted) wsc.send("@$author FOLD!".asIRCMessage)
                    delay(3.01e3.milliseconds)
                }
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
            else -> {
                println("Unhandled packet: $packet")
            }
        }
    }
    wsc.connect()
}

fun main() {
    val namesToLock = setOf("billionaire", "billionaire2")
    for (username in namesToLock) {
        connectAs(username)
    }
}