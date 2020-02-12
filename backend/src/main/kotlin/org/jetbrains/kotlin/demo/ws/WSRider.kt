package org.jetbrains.kotlin.demo.ws

import com.beust.klaxon.Klaxon
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.jetbrains.kotlin.demo.*
import org.jetbrains.kotlin.demo.controllers.RiderController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Controller
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.handler.TextWebSocketHandler



@Configuration
@EnableWebSocket
class WSRiderConfig : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(WSRider(), "/ws-rider").setAllowedOrigins("*").withSockJS()
    }
}


@Controller
class WSRider: TextWebSocketHandler () {
    private val sessionList = mutableMapOf<String, WebSocketSession>();
    private val jsonParser = Klaxon()
    @Autowired
    private var riderController = RiderController()


    @Throws(Exception::class)
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessionList.remove(session.id)
    }

    @Throws(Exception::class)
    override fun afterConnectionEstablished(session: WebSocketSession) {
        val parameters = parseURlQuery(session.uri.query)
        val riderId = parameters["riderId"] ?: "";
        if (riderId == "") return session.close()
        session.attributes["riderId"] = riderId;
            sessionList[riderId] = session;
        // Get last status for riderUid
        session.sendMessage(TextMessage("sync for...${riderId}: "))
        riderController.getLastRiderState(riderId)
    }



    @Throws(Exception::class)
    public override fun handleTextMessage(session: WebSocketSession?, textMessage: TextMessage?) {
        println(session?.attributes?.get("riderUid"))
        val jsonString = textMessage?.payload.toString()
        val riderId = session?.attributes?.get("riderId") as String
        val action = jsonParser.parse<Action>(jsonString) as Action
        if (action.payload == null) throw Error("Missing Location Payload")

        when (action?.type) {
            ClientActions.REQUEST_RIDE -> {
                riderController.getLastRiderState(riderId)
                val payload = jsonParser.parse<RequestRidePayload>(action.payload) as RequestRidePayload
                riderController.requestRide(riderId, payload.riderLocation, payload.destination)
            }

            ClientActions.A -> {
                riderController.getLastRiderState(riderId)
            }
            ClientActions.B -> {
                riderController.produce(riderId)
            }
        }
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