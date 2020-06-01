package com.kafkastreamsuber.kafkastreamsuber.kafka

import com.kafkastreamsuber.kafkastreamsuber.models.Trip
import com.kafkastreamsuber.kafkastreamsuber.models.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.Output
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component

@Component
@EnableBinding(Producer.Bindings::class)
class Producer {
    @Autowired
    lateinit var output_1: MessageChannel
    @Autowired
    lateinit var output_2: MessageChannel

    fun produceUser(user: User) {
        output_1.send(MessageBuilder.withPayload(user).build())
    }

    fun produceTrip(trip: Trip) {
        output_2.send(MessageBuilder.withPayload(trip).build())
    }



    internal interface Bindings {
        @Output(USER_TOPIC)
        fun output_1(): MessageChannel

        @Output(TRIP_TOPIC)
        fun output_2(): MessageChannel

        companion object {
            const val USER_TOPIC = "output_1"
            const val TRIP_TOPIC = "output_2"
        }
    }

}

