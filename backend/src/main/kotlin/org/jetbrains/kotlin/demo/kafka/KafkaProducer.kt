package org.jetbrains.kotlin.demo.kafka

import com.beust.klaxon.Klaxon
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.jetbrains.kotlin.demo.*
import org.springframework.stereotype.Service
import java.util.*


@Service
class KafkaProducer {
    private val producer = createProducer()


    private fun createProducer(): Producer<String, String> {
        val props = Properties()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = KAFKA_BROKER
        props["application.id"] = "kafka-uber-producer"
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ProducerConfig.ACKS_CONFIG] = "all";
        return KafkaProducer<String, String>(props)
    }

    private fun produce(topic: String, key: String, value: String) {
        val futureResult = producer.send(ProducerRecord(topic, key, value))
        futureResult.get()
    }

    private fun produceX(topic: String, key: String, value: String) {
        val futureResult = producer.send(ProducerRecord(topic, key, value))
        futureResult.get()
        println("[X] PRODUCED TRIP STATUS: $value")
    }

    fun produceTrip(trip: Trip) {
        produceX(TRIP_TOPIC, trip.id, Klaxon().toJsonString(trip))
    }

    fun produceRiders(rider: User) {
        produce(USER_TOPIC, rider.id, Klaxon().toJsonString(rider))
    }

    fun produceDriver(driver: User) {
        produce(USER_TOPIC, driver.id, Klaxon().toJsonString(driver))
    }
}