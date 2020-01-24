package org.jetbrains.kotlin.demo


import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Controller
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.net.URL
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;


@Configuration
@EnableWebSocket
class WSRiderConfig : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(WSRider(), "/ws-rider").setAllowedOrigins("*").withSockJS()
    }
}


@Controller
class WSRider : TextWebSocketHandler() {
    private val sessionList = mutableMapOf<String, WebSocketSession>();


    @Throws(Exception::class)
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessionList.remove(session.id)
    }

    @Throws(Exception::class)
    override fun afterConnectionEstablished(session: WebSocketSession) {
        val parameters = parseURlQuery(session.uri.query)
        val riderUid = parameters["riderUid"] ?: "";
        if (riderUid == "") return session.close()
        session.attributes["riderUid"] = riderUid;
        sessionList[riderUid] = session;
    }


    @Throws(Exception::class)
    public override fun handleTextMessage(session: WebSocketSession?, textMessage: TextMessage?) {
        println(session?.attributes?.get("riderUid"))
    }

    private fun emit(session: WebSocketSession, msg: Action) =
        session.sendMessage(TextMessage(jacksonObjectMapper().writeValueAsString(msg)))
//    fun broadcast(msg: Message) = sessionList.forEach { emit(it.key, msg) }
//    fun broadcastToOthers(me: WebSocketSession, msg: Message) =
//        sessionList.filterNot { it.key == me }.forEach { emit(it.key, msg) }

    private fun validate(action: Action?) {
        // TODO: validate actions fields.
    }
}