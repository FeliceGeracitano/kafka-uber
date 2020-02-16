package org.jetbrains.kotlin.demo.ws

import com.beust.klaxon.Klaxon
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.jetbrains.kotlin.demo.*
import org.jetbrains.kotlin.demo.controllers.RiderController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
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

    @Autowired
    private val myWebSocketHandler: WSRider? = null

    @Override
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(myWebSocketHandler, "/ws-rider").setAllowedOrigins("*").withSockJS()
    }
}


@Component
class WSRider : TextWebSocketHandler() {
    private val sessionList = mutableMapOf<String, WebSocketSession>();
    private val jsonParser = Klaxon()

    @Autowired
    private lateinit var riderController: RiderController


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
        val trip = riderController.getLastTripStatus(riderId);
        val payload = if (trip !== null) jacksonObjectMapper().writeValueAsString(trip) else null
        val messageString = jacksonObjectMapper().writeValueAsString(
            Action(ACTION_TYPE.SYNC_STATUS, payload)
        )
        session.sendMessage(TextMessage(messageString))
    }


    @Throws(Exception::class)
    public override fun handleTextMessage(session: WebSocketSession?, textMessage: TextMessage?) {
        println("riderId" + session?.attributes?.get("riderId"))
        val jsonString = textMessage?.payload.toString()
        val riderId = session?.attributes?.get("riderId") as String
        val action = jsonParser.parse<Action>(jsonString) as Action
        if (action.payload == null) throw Error("Missing Location Payload")

            when (action?.type) {
                ACTION_TYPE.REQUEST_TRIP -> {
                val (destination, riderLocation) = jsonParser.parse<RequestRidePayload>(action.payload) as RequestRidePayload
                riderController.handleRequestRide(riderId, destination, riderLocation);
            }
        }

    }

    private fun emit(session: WebSocketSession, msg: Action) =
        session.sendMessage(TextMessage(jacksonObjectMapper().writeValueAsString(msg)))
//    fun broadcast(msg: Message) = sessionList.forEach { emit(it.key, msg) }
//    fun broadcastToOthers(me: WebSocketSession, msg: Message) =
//        sessionList.filterNot { it.key == me }.forEach { emit(it.key, msg) }

    public fun sendMessageToRider(riderId: String, msg: String) {
        val session = sessionList[riderId]
        session?.sendMessage(TextMessage(msg))
    }

}