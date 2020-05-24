package com.kafkastreamsuber.kafkastreamsuber.ws


import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.kafkastreamsuber.kafkastreamsuber.cassandra.TripStatus
import com.kafkastreamsuber.kafkastreamsuber.cassandra.User
import com.kafkastreamsuber.kafkastreamsuber.cassandra.UserType
import com.kafkastreamsuber.kafkastreamsuber.kafka.Producer
import com.kafkastreamsuber.kafkastreamsuber.kafka.Store
import com.kafkastreamsuber.kafkastreamsuber.models.*
import com.kafkastreamsuber.kafkastreamsuber.parseURLQuery
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
import javax.annotation.PostConstruct


@Configuration
@EnableWebSocket
class WSDriverConfig : WebSocketConfigurer {


    @Autowired
    private val myWebSocketHandler: WSDriver? = null

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        if (myWebSocketHandler != null) {
            registry.addHandler(myWebSocketHandler, "/ws-driver").setAllowedOrigins("*").withSockJS()
        }
    }
}


@Controller
class WSDriver : TextWebSocketHandler() {
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
        val driverId = parameters["driverId"] ?: ""
        if (driverId == "") return session.close(CloseStatus(404))
        session.attributes["driverId"] = driverId
        sessionList[driverId] = session
        producer.produceUser(
            User(
                driverId,
                null,
                UserType.DRIVER,
                null
            )
        )

        // send SYNC_STATUS message
        val lastTrip = store.getLastTrip(driverId)
        if (lastTrip !== null) {
            session.sendMessage(
                    TextMessage(
                            jacksonObjectMapper().writeValueAsString(
                                    Action(ACTION_TYPE.SYNC_STATUS, jacksonObjectMapper().writeValueAsString(lastTrip))
                            )
                    )
            )
        }

        // send REQUEST_TRIP message
        val requestingTrip = store.getPendingRequests(driverId) ?: return
        session.sendMessage(
                TextMessage(
                        jacksonObjectMapper().writeValueAsString(
                                Action(ACTION_TYPE.REQUEST_TRIP, jacksonObjectMapper().writeValueAsString(requestingTrip))
                        )
                )
        )
    }


    @Throws(Exception::class)
    public override fun handleTextMessage(session: WebSocketSession, textMessage: TextMessage) {
        val jsonString = textMessage?.payload.toString()
        val driverId = session?.attributes?.get("driverId") as String
        val action: Action = try {
            jsonParser.readValue(jsonString, Action::class.java)
        } catch (_: java.lang.Exception) {
            return
        }


        when (action.type) {
            ACTION_TYPE.CONFIRM_TRIP -> {
                if (action.payload == null) throw Error("Missing Location Payload")
                val (tripId, driverLocation) = jsonParser.readValue(action.payload, ConfirmRidePayload::class.java)

                // Update Driver
                val driver = store.getUser(driverId)!!
                driver.lastTripId = tripId
                producer.produceUser(driver)

                // Update Trip
                val trip = store.getTrip(tripId)!!
                trip.status = TripStatus.CONFIRMED
                trip.driverId = driverId
                producer.produceTrip(trip)
            }
            ACTION_TYPE.UPDATE_DRIVER_LOCATION -> {
                if (action.payload == null) throw Error("Missing Location Payload")
                val location = jsonParser.readValue(action.payload, Location::class.java)
                var driver = store.getUser(driverId) ?: return
                driver.location = location
                producer.produceUser(driver)
            }
            ACTION_TYPE.START_TRIP -> {
                val trip = store.getLastTrip(driverId) ?: return
                trip.status = TripStatus.STARTED
                producer.produceTrip(trip)
            }
            ACTION_TYPE.END_TRIP -> {
                val driver = store.getUser(driverId) ?: return
                val trip = store.getTrip(driver.lastTripId ?: "") ?: return
                trip.status = TripStatus.ENDED
                producer.produceTrip(trip)
                driver.lastTripId = null
                producer.produceUser(driver)
            }
        }

    }

    private fun emit(session: WebSocketSession, msg: Action) =
            session.sendMessage(TextMessage(jacksonObjectMapper().writeValueAsString(msg)))

    fun sendMessage(driverId: String, msg: String) {
        val session = sessionList[driverId]
        if (session === null || !session.isOpen) return
        synchronized(session) { session.sendMessage(TextMessage(msg)) }
    }
}