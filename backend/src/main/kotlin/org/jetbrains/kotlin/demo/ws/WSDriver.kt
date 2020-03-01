package org.jetbrains.kotlin.demo.ws


import com.beust.klaxon.Klaxon
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.jetbrains.kotlin.demo.*
import org.jetbrains.kotlin.demo.controllers.DriverController
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
class WSDriverConfig : WebSocketConfigurer {

    @Autowired
    private val myWebSocketHandler: WSDriver? = null

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(myWebSocketHandler, "/ws-driver").setAllowedOrigins("*").withSockJS()
    }
}


@Controller
    class WSDriver : TextWebSocketHandler() {
    private val sessionList = mutableMapOf<String, WebSocketSession>();
    private val jsonParser = Klaxon()

    @Autowired
    private lateinit var driverController: DriverController

    @Throws(Exception::class)
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessionList.remove(session.id)
    }

    @Throws(Exception::class)
    override fun afterConnectionEstablished(session: WebSocketSession) {
        val parameters = parseURlQuery(session.uri.query)
        val driverId = parameters["driverId"] ?: "";
        if (driverId == "") return session.close(CloseStatus(404))
        session.attributes["driverId"] = driverId;
        sessionList[driverId] = session;
        driverController.init(driverId)

        // send SYNC_STATUS message
        val lastTrip = driverController.getLastTripStatus(driverId)
        if (lastTrip !== null) {
            session.sendMessage(TextMessage(jacksonObjectMapper().writeValueAsString(
                Action(ACTION_TYPE.SYNC_STATUS, jacksonObjectMapper().writeValueAsString(lastTrip))
            )))
        }

        // send REQUEST_TRIP message
        val requestingTrip = driverController.getPendingRequests(driverId) ?: return
        session.sendMessage(TextMessage(jacksonObjectMapper().writeValueAsString(
            Action(ACTION_TYPE.REQUEST_TRIP, jacksonObjectMapper().writeValueAsString(requestingTrip))
        )))

    }


    @Throws(Exception::class)
    public override fun handleTextMessage(session: WebSocketSession?, textMessage: TextMessage?) {
        val jsonString = textMessage?.payload.toString()
        val action = jsonParser.parse<Action>(jsonString) as Action
        val driverId = session?.attributes?.get("driverId") as String

        when (action?.type) {
            ACTION_TYPE.CONFIRM_TRIP -> {
                if (action.payload == null) throw Error("Missing Location Payload")
                val payload = jsonParser.parse<ConfirmRidePayload>(action.payload) as ConfirmRidePayload
                driverController.confirmTrip(driverId, payload.tripId, payload.driverLocation)
            }
            ACTION_TYPE.UPDATE_DRIVER_LOCATION -> {
                if (action.payload == null) throw Error("Missing Location Payload")
                val location = jsonParser.parse<Location>(action.payload) as Location
                driverController.updateLocation(driverId, location)
            }
            ACTION_TYPE.START_TRIP -> driverController.startTrip(driverId)
            ACTION_TYPE.END_TRIP -> driverController.endTrip(driverId)
        }
    }

    private fun emit(session: WebSocketSession, msg: Action) =
        session.sendMessage(TextMessage(jacksonObjectMapper().writeValueAsString(msg)))


    fun sendMessage(driverId: String, msg: String) {
        val session = sessionList[driverId]
        if (session === null) return
        session.sendMessage(TextMessage(msg))

    }
}