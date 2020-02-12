package org.jetbrains.kotlin.demo.kafka

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import org.apache.kafka.streams.state.Stores.inMemoryKeyValueStore
import org.jetbrains.kotlin.demo.kafkaBroker
import org.jetbrains.kotlin.demo.ridersTopic
import org.springframework.stereotype.Service
import java.util.*


@Service
class KafkaConsumer() {

    private val driverStreams: KafkaStreams = buildRiderStream()


    private fun buildRiderStream(): KafkaStreams {
        val streamsBuilder = StreamsBuilder()


/*        val riderJsonStream = streamsBuilder
                .stream(ridersTopic, Consumed.with(Serdes.String(), Serdes.String()))
                .through("test")

        val riderStream: KStream<String, User> = riderJsonStream.mapValues { v ->
            val rider = jsonMapper.readValue(v, User::class.java)
            print(rider);
            rider
        }*/

        val stateStore = inMemoryKeyValueStore("key-value-store")

        streamsBuilder.table(
                ridersTopic,
                Materialized.`as`<String, String>(stateStore)
                        .withKeySerde(Serdes.String())
                        .withValueSerde(Serdes.String())
        )


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





    fun getLastDriver(key:String) {
        val metadata = driverStreams.metadataForKey("keyvaluestore", key,Serdes.String().serializer())
        print(metadata)
        val store: ReadOnlyKeyValueStore<String, String> = driverStreams.store("keyvaluestore",  QueryableStoreTypes.keyValueStore())
        val value = store.get(key);
    }
}