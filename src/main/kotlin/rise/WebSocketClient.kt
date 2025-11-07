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
import rise.packet.api.S2CPacket
import rise.packet.impl.c2s.general.C2SPacketKeepAlive
import rise.packet.impl.c2s.protection.C2SPacketAuthenticate
import java.net.URI
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

typealias PacketListener = (packet: S2CPacket) -> Unit

class WebSocketClient {
    companion object {
        private val gson: Gson = GsonBuilder().create()
//        private const val RECONNECT_DELAY = 3000L
        private const val SERVER_URL = "wss://auth.riseclient.com:8443"
    }
    private val factory = NioIoHandler.newFactory()
    private val group = MultiThreadIoEventLoopGroup(factory)
    private var channel: Channel? = null
    private val packetListeners = mutableSetOf<PacketListener>()
    private val handshakeListeners = mutableSetOf<(ctx: ChannelHandlerContext) -> Unit>()

    fun addPacketListener(listener: PacketListener) {
        packetListeners.add(listener)
    }
    fun addHandshakeListener(listener: (ctx: ChannelHandlerContext) -> Unit) {
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
            //the awesome billionaire detection method of adding headers and only checking for them eons later
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

        println("[RiseIRC] Connecting...")
        bootstrap.connect(uri.host, uri.port).addListener { f ->
            if (!f.isSuccess) {
                println("[RiseIRC] Connection failed: ${f.cause()?.message}")
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
                    println("[RiseIRC] Handshake complete")
                    scheduleKeepAlive(ctx)
                    this@WebSocketClient.handshakeListeners.forEach {l -> l(ctx) }
                } catch (e: Exception) {
                    println("[RiseIRC] Handshake failed: ${e.message}")
                    handshakeFuture!!.setFailure(e)
                    ctx.close()
                }
                return
            }

            if (msg is FullHttpResponse) throw IllegalStateException("Unexpected HTTP response: $msg")

            when (msg) {
                is TextWebSocketFrame -> {
                    val j = gson.fromJson(msg.text(), JsonObject::class.java)
                    val packet = PacketHandler.parse(j)
                    this@WebSocketClient.packetListeners.forEach { it(packet) }
                }
                is CloseWebSocketFrame -> {
                    println("[RiseIRC] Server closed connection")
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
            println("[RiseIRC] Error: ${cause.message}")
            ctx.close()
        }
    }
}

fun main() {
    val wsc = WebSocketClient()
    wsc.addHandshakeListener { ctx ->
        println("Send")
        val json = C2SPacketAuthenticate("billionaire", "segawtaawt").export()
        ctx.writeAndFlush(TextWebSocketFrame(json))
    }
    wsc.addPacketListener { packet ->
        println("got packet: $packet")
    }
    wsc.connect()
}