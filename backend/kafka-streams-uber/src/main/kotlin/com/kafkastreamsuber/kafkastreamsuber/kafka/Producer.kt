package com.kafkastreamsuber.kafkastreamsuber.kafka

import com.kafkastreamsuber.kafkastreamsuber.models.Trip
import com.kafkastreamsuber.kafkastreamsuber.models.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.Output
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.MessageBuilder


@EnableBinding(ProducerInterface::class)
class Producer {
    @Autowired
    lateinit var ouput_user: MessageChannel

    @Autowired
    lateinit var ouput_trip: MessageChannel

    fun produceUser(user: User) {
        ouput_user.send(MessageBuilder.withPayload(user).setHeader(KafkaHeaders.MESSAGE_KEY, user.id).build())
    }

    fun produceTrip(trip: Trip) {
        ouput_trip.send(MessageBuilder.withPayload(trip).setHeader(KafkaHeaders.MESSAGE_KEY, trip.id).build())
    }
}

interface ProducerInterface {
    @Output("ouput_user")
    fun ouput_user(): MessageChannel

    @Output("ouput_trip")
    fun ouput_trip(): MessageChannel
}
