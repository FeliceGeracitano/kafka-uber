package org.jetbrains.kotlin.demo


import com.beust.klaxon.Klaxon
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(WSRider(), "/ws-driver").setAllowedOrigins("*").withSockJS()
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
        val driverUid = parameters["driverUid"] ?: "";
        if (driverUid == "") return session.close(CloseStatus(404))
        session.attributes["driverUid"] = driverUid;
        sessionList[driverUid] = session;
        // Get last status for driverUid
    }


    @Throws(Exception::class)
    public override fun handleTextMessage(session: WebSocketSession?, textMessage: TextMessage?) {
        println(session?.attributes?.get("riderUid"))
        val jsonString = textMessage?.payload.toString()
        val action = jsonParser.parse<Action>(jsonString)
        when (action?.type) {
            ClientActions.CONFIRM_RIDE -> {
                driverController.confirmRider()

            }
            ClientActions.UPDATE_DRIVER_LOCATION -> {
                // read location param from json
                driverController.updateLocation(Location(1.0,1.0))

            }
            ClientActions.START_RIDE -> {
                // write in "trip" topic ["tripId", status: STARTED}]

            }
            ClientActions.END_RIDE -> {
                // write in "trip" topic ["tripId", status: ENDED}]
            }
        }
    }

    private fun emit(session: WebSocketSession, msg: Action) =
        session.sendMessage(TextMessage(jacksonObjectMapper().writeValueAsString(msg)))

    private fun validate(action: Action?) {
        // TODO: validate actions fields.
    }
}