package com.kafkastreamsuber.kafkastreamsuber.ws

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.kafkastreamsuber.kafkastreamsuber.kafka.Producer
import com.kafkastreamsuber.kafkastreamsuber.kafka.Store
import com.kafkastreamsuber.kafkastreamsuber.models.*
import com.kafkastreamsuber.kafkastreamsuber.parseURLQuery
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
import java.util.*


@Configuration
@EnableWebSocket
class WSRiderConfig : WebSocketConfigurer {

    @Autowired
    private val myWebSocketHandler: WSRider? = null

    @Override
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        if (myWebSocketHandler != null) {
            registry.addHandler(myWebSocketHandler, "/ws-rider").setAllowedOrigins("*").withSockJS()
        }
    }
}


@Component
class WSRider : TextWebSocketHandler() {
    private val sessionList = mutableMapOf<String, WebSocketSession>();
    private val jsonParser = jacksonObjectMapper()
    @Autowired
    private lateinit var store: Store
    @Autowired
    private lateinit var producer: Producer


    @Throws(Exception::class)
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessionList.remove(session.id)
    }

    @Throws(Exception::class)
    override fun afterConnectionEstablished(session: WebSocketSession) {
        val parameters = parseURLQuery(session.uri?.query ?: "")
        val riderId = parameters["riderId"] ?: "";
        if (riderId == "") return session.close()
        session.attributes["riderId"] = riderId;
        sessionList[riderId] = session;
        val trip = store.getLastTrip(riderId)
        val payload = if (trip !== null) jacksonObjectMapper().writeValueAsString(trip) else null
        val messageString = jacksonObjectMapper().writeValueAsString(
                Action(ACTION_TYPE.SYNC_STATUS, payload)
        )
        session.sendMessage(TextMessage(messageString))
    }

    @Throws(Exception::class)
    override fun handleTextMessage(session: WebSocketSession, textMessage: TextMessage) {
        val jsonString = textMessage?.payload.toString()
        val riderId = session?.attributes?.get("riderId") as String
        val action = try {
            jsonParser.readValue(jsonString, Action::class.java)
        } catch (_: java.lang.Exception) {
            return
        }

        if (action.payload == null) throw Error("Missing Location Payload")
        when (action?.type) {
            ACTION_TYPE.REQUEST_TRIP -> {
                val (riderLocation, destination) = jsonParser.readValue(action.payload, RequestRidePayload::class.java)
                val uuid: UUID = UUID.randomUUID()
                val tripUUID: String = uuid.toString()
                val trip = Trip(tripUUID, TripStatus.REQUESTING, null, riderId, riderLocation, destination, null)
                val rider = User(riderId, riderLocation, UserType.RIDER, tripUUID)
                producer.produceTrip(trip)
                producer.produceUser(rider)
            }
        }
    }

    private fun emit(session: WebSocketSession, msg: Action) =
            session.sendMessage(TextMessage(jacksonObjectMapper().writeValueAsString(msg)))

    fun sendMessageToRider(riderId: String, msg: String) {
        val session = sessionList[riderId]
        if (session === null || !session.isOpen) return
        session?.sendMessage(TextMessage(msg))
    }
}
