package org.jetbrains.kotlin.demo.kafka

import com.beust.klaxon.Klaxon
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.StringSerializer
import org.jetbrains.kotlin.demo.*
import org.springframework.stereotype.Service
import java.util.*
import javax.annotation.PostConstruct


@Service
class KafkaProducer {
    private val producer = createProducer()


    private fun createProducer(): Producer<String, String> {
        val props = Properties()
        props["bootstrap.servers"] = KAFKA_BROKER
        props["application.id"] = "kafka-uber-producer"
        props["key.serializer"] = StringSerializer::class.java
        props["value.serializer"] = StringSerializer::class.java
        return KafkaProducer<String, String>(props)
    }

    private fun produce(topic: String, key: String, value: String) {
        val futureResult = producer.send(ProducerRecord(topic, key, value))
        futureResult.get()
    }

    fun produceTrip(trip: Trip) {
        produce(TRIP_TOPIC, trip.id, Klaxon().toJsonString(trip))
    }

    fun produceRiders(rider: User) {
        produce(USER_TOPIC, rider.id, Klaxon().toJsonString(rider))
    }

    fun produceDriver(driver: User) {
        produce(USER_TOPIC, driver.id, Klaxon().toJsonString(driver))
    }
}