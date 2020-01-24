package org.jetbrains.kotlin.demo


import com.beust.klaxon.Klaxon
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.jetbrains.kotlin.demo.kafka.KafkaProducer
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Controller
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.sql.RowId
import java.util.concurrent.atomic.AtomicLong
import java.util.UUID;


@Configuration
@EnableWebSocket
class WSConfig : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(WSDriver(), "/ws-driver").setAllowedOrigins("*").withSockJS()
    }
}


@Controller
class WSDriver : TextWebSocketHandler() {
    private val sessionList = mutableSetOf<WebSocketSession>();
    private var uids = AtomicLong(0)
    // private val kafkaProducer = KafkaProducer();


    @Throws(Exception::class)
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessionList.remove(session)
    }

    @Throws(Exception::class)
    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessionList.add(session)
    }




    @Throws(Exception::class)
    public override fun handleTextMessage(session: WebSocketSession?, textMessage: TextMessage?) {
        val JSONString = textMessage?.payload.toString()
        val action = Klaxon().parse<Action>(JSONString)
        validate(action); // remove nullabile filed for producers
        when (action?.type) {
            ActionType.REQUEST_RIDE -> {
                // kafkaProducer.produceRiders(action.payload?.rider)
//                kafkaProducer.produceRides(
//                    Ride(
//                        "R" + UUID.randomUUID().toString(),
//                        null,
//                        action.payload.rider?.id,
//                        action.payload.destination
//                    )
//                )

            }
            ActionType.CONFIRM_RIDE -> {
//                kafkaProducer.produceRiders(action.payload?.rider)
            }
            ActionType.DRIVER_UPDATE_LOCATION -> {
//                kafkaProducer.produceRiders(Ride(UUID.randomUUID().toString(), action.payload.driver.id))
            }
            else -> {
                println(action?.type)
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

