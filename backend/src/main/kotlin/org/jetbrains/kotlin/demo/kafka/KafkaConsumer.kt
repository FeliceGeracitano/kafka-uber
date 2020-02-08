package org.jetbrains.kotlin.demo.kafka

import com.beust.klaxon.Klaxon
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.jetbrains.kotlin.demo.*
import java.util.*


class KafkaConsumer() {

    private val consumer = createConsumer()

    private fun createConsumer(): Consumer<String, String> {
        val props = Properties()
        props["bootstrap.servers"] = kafkaBroker
        props["group.id"] = "person-processor"
        props["key.deserializer"] = StringDeserializer::class.java
        props["value.deserializer"] = StringDeserializer::class.java
        return KafkaConsumer<String, String>(props)
    }
    
    private fun process() {

    }
}