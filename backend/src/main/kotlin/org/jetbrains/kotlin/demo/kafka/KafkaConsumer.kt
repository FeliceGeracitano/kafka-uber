package org.jetbrains.kotlin.demo.kafka

import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KGroupedStream
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.state.KeyValueStore
import org.jetbrains.kotlin.demo.*
import org.springframework.stereotype.Service
import java.util.*
import javax.annotation.PostConstruct

@Service
class KafkaConsumer() {

    private val driverStreams = buildRiderStream()


    private fun buildRiderStream(): Any {
        val streamsBuilder = StreamsBuilder()


        val riderJsonStream = streamsBuilder
                .stream(ridersTopic, Consumed.with(Serdes.String(), Serdes.String()))
                .through("test")

        val riderStream: KStream<String, User> = riderJsonStream.mapValues { v ->
            val rider = jsonMapper.readValue(v, User::class.java)
            print(rider);
            rider
        }

        riderStream.groupBy { key, _ -> key, Grouped  }




        val props = Properties()
        props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaBroker
        props[StreamsConfig.APPLICATION_ID_CONFIG] = "my app"
        props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass
        props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.String().javaClass
        val streams = KafkaStreams(streamsBuilder.build(), props)
        streams.cleanUp()
        streams.start()
        return streams


        // val streamsBuilder = StreamsBuilder()
        // val personStream: KStream<String, User> = streamsBuilder
        //         .stream(driversTopic, Consumed.with(Serdes.String(), Serdes.String()))
        //         .mapValues { v ->
        //             jsonMapper.readValue(v, User::class.java)
        //         }
        // personStream.foreach { _, value -> println(value) }

        // streamsBuilder.table(driversTopic, Materialized.`as`<String, User, KeyValueStore<Bytes, ByteArray>>(DRIVERS_TABLE))
        // val topology = streamsBuilder.build()
        // val props = Properties()
        // props["bootstrap.servers"] = kafkaBroker
        // val streams = KafkaStreams(topology, props)
        //  streams.start()
        // return streams
        // GlobalAppState.instance.streams(streams)
    }
}