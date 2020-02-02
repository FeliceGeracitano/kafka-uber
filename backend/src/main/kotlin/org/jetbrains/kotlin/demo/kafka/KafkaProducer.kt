package org.jetbrains.kotlin.demo.kafka

import com.beust.klaxon.Klaxon
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.StringSerializer
import org.jetbrains.kotlin.demo.*
import java.util.*


class KafkaProducer() {

    private val producer = createProducer()

    private fun createProducer(): Producer<String, String> {
        val props = Properties()
        props["bootstrap.servers"] = kafkaBroker
        props["key.serializer"] = StringSerializer::class.java
        props["value.serializer"] = StringSerializer::class.java
        return KafkaProducer<String, String>(props)
    }

    private fun produce(topic: String, record: String) {
        println("Sending record... $record")
        val futureResult = producer.send(ProducerRecord(topic, record))
        futureResult.get()
        println("Record sent.")
    }

    fun produceRiders(rider: User?) {
        produce(ridersTopic, Klaxon().toJsonString(rider))
    }

    fun produceRides(ride: Trip?) {
        produce(ridesTopic, Klaxon().toJsonString(ride))
    }
}