package org.jetbrains.kotlin.demo.kafka

import com.beust.klaxon.Klaxon
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.StringSerializer
import org.jetbrains.kotlin.demo.*
import java.util.*


class KafkaConsumer() {

    private val consumer = createConsumer()

    private fun  createConsumer(): Producer<String, String> {
        val props = Properties()
        props["bootstrap.servers"] = kafkaBroker
        props["key.serializer"] = StringSerializer::class.java
        props["value.serializer"] = StringSerializer::class.java
        return KafkaProducer<String, String>(props)
    }


}