package org.jetbrains.kotlin.demo.kafka

import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.state.KeyValueStore
import org.jetbrains.kotlin.demo.*
import java.util.*


class KafkaConsumer() {

    private val consumer = createConsumer()
    private val driverStore = buildDriverStream()

    private fun createConsumer(): Consumer<String, String> {
        val props = Properties()
        props["bootstrap.servers"] = kafkaBroker
        props["group.id"] = "person-processor"
        props["key.deserializer"] = StringDeserializer::class.java
        props["value.deserializer"] = StringDeserializer::class.java
        return KafkaConsumer<String, String>(props)
    }

    private fun buildDriverStream() {
        val streamsBuilder = StreamsBuilder()
        val personStream: KStream<String, User> = streamsBuilder
                .stream(driversTopic, Consumed.with(Serdes.String(), Serdes.String()))
                .mapValues { v ->
                    jsonMapper.readValue(v, User::class.java)
                }
        personStream.foreach { _, value -> println(value)  }


        streamsBuilder.table(driversTopic, Materialized.`as`<String, User, KeyValueStore<Bytes, ByteArray>>(DRIVERS_TABLE)))

        val topology = streamsBuilder.build()
        val props = Properties()
        props["bootstrap.servers"] = kafkaBroker
        props["application.id"] = "kafka-tutorial"
        val streams = KafkaStreams(topology, props)
        streams.start()
    }
}