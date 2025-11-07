package rise

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageEncoder
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketFrame

class XorEncoder : MessageToMessageEncoder<WebSocketFrame>() {

    override fun encode(ctx: ChannelHandlerContext, msg: WebSocketFrame, out: MutableList<Any>) {
        when (msg) {
            is TextWebSocketFrame -> {
                val encrypted = encrypt(msg.text())

                out.add(TextWebSocketFrame(encrypted))
            }
            else -> {
                out.add(msg.retain())
            }
        }
    }
}