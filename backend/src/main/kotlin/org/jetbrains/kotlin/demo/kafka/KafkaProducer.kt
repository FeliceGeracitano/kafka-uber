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

class KafkaProducer() {

    private val producer = createProducer()



    @PostConstruct
    fun init() {
        print("KafkaProducer init")
    }

    private fun createProducer(): Producer<String, String> {
        val props = Properties()
        props["bootstrap.servers"] = kafkaBroker
        props["key.serializer"] = StringSerializer::class.java
        props["value.serializer"] = StringSerializer::class.java
        return KafkaProducer<String, String>(props)
    }

    private fun produce(topic: String, key: String, value: String) {
        val futureResult = producer.send(ProducerRecord(topic, key, value))
        futureResult.get()
    }

    fun produceTrip(trip: Trip) {
        produce(tripTopic, trip.id,  Klaxon().toJsonString(trip))
    }

    fun produceRiders(rider: User) {
        produce(ridersTopic, rider.id,  Klaxon().toJsonString(rider))
    }

    fun produceDriver(driver: User) {
        produce(driversTopic, driver.id,  Klaxon().toJsonString(driver))
    }
}