package org.jetbrains.kotlin.demo.kafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import org.jetbrains.kotlin.demo.RIDERS_TABLE
import org.jetbrains.kotlin.demo.RIDER_TOPIC
import org.jetbrains.kotlin.demo.User
import org.jetbrains.kotlin.demo.serde.UserSerde
import org.springframework.stereotype.Service
import java.util.*


@Service
class KafkaConsumer {

    private val bootstrapServers = "localhost:9092"
    private val DEFAULT_REST_ENDPOINT_HOSTNAME = "localhost"
    val streams: KafkaStreams? = null


    private fun streamsConfig(
        bootstrapServers: String?,
        applicationServerPort: Int,
        stateDir: String?,
        host: String
    ): Properties? {
        val streamsConfiguration = Properties()
        streamsConfiguration[StreamsConfig.APPLICATION_ID_CONFIG] = "kafka-uber"
        streamsConfiguration[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        streamsConfiguration[StreamsConfig.APPLICATION_SERVER_CONFIG] = "$host:$applicationServerPort"
        streamsConfiguration[StreamsConfig.STATE_DIR_CONFIG] = stateDir
        streamsConfiguration[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        streamsConfiguration[StreamsConfig.COMMIT_INTERVAL_MS_CONFIG] = 500
        val metadataMaxAgeMs = System.getProperty(ConsumerConfig.METADATA_MAX_AGE_CONFIG)
        if (metadataMaxAgeMs != null) {
            try {
                val value = metadataMaxAgeMs.toInt()
                streamsConfiguration[ConsumerConfig.METADATA_MAX_AGE_CONFIG] = value
                println("Set consumer configuration " + ConsumerConfig.METADATA_MAX_AGE_CONFIG.toString() + " to " + value)
            } catch (ignored: NumberFormatException) {
            }
        }
        return streamsConfiguration
    }

    init {
        val streams = KafkaStreams(
            buildTopology(),
            streamsConfig(bootstrapServers, 7070, "/tmp/kafka-streams", DEFAULT_REST_ENDPOINT_HOSTNAME)
        )

        streams.cleanUp();
        streams.start();
    }

    private fun buildTopology(): Topology? {
        val builder = StreamsBuilder()
        val userSerde = UserSerde()

        val riderStream: KStream<String, User> = builder.stream(
            RIDER_TOPIC,
            Consumed.with(Serdes.String(), userSerde)
        )

        riderStream.foreach { _, value -> println("[rider stream] $value") }

        val riderTable: KTable<Long, User> = builder.table(
            RIDER_TOPIC, Materialized.`as`<Long, User, KeyValueStore<Bytes, ByteArray>>(RIDERS_TABLE)
                .withKeySerde(Serdes.Long())
                .withValueSerde(userSerde)
        )

        return builder.build();
    }


//    private fun buildRiderStream(): KafkaStreams {
//        val streamsBuilder = StreamsBuilder()
//        val riderJsonStream = streamsBuilder
//                .stream(ridersTopic, Consumed.with(Serdes.String(), Serdes.String()))
//                .through("test")
//
//        val riderStream: KStream<String, User> = riderJsonStream.mapValues { v ->
//            val rider = jsonMapper.readValue(v, User::class.java)
//            print(rider);
//            rider
//        }
//
//                val stateStore = inMemoryKeyValueStore("key-value-store")
//
//                streamsBuilder.table(
//                    ridersTopic,
//                    Materialized.`as`<String, String>(stateStore)
//                        .withKeySerde(Serdes.String())
//                        .withValueSerde(Serdes.String())
//                )
//                val props = Properties()
//                props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaBroker
//                props[StreamsConfig.APPLICATION_ID_CONFIG] = "my app"
//                props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass
//                props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.String().javaClass
//                val streams = KafkaStreams(streamsBuilder.build(), props)
//                streams.cleanUp()
//                streams.start()
//                return streams
//         val streamsBuilder = StreamsBuilder()
//         val personStream: KStream<String, User> = streamsBuilder
//                 .stream(driversTopic, Consumed.with(Serdes.String(), Serdes.String()))
//                 .mapValues { v ->
//                     jsonMapper.readValue(v, User::class.java)
//                 }
//         personStream.foreach { _, value -> println(value) }
//         streamsBuilder.table(driversTopic, Materialized.`as`<String, User, KeyValueStore<Bytes, ByteArray>>(DRIVERS_TABLE))
//         val topology = streamsBuilder.build()
//         val props = Properties()
//         props["bootstrap.servers"] = kafkaBroker
//         val streams = KafkaStreams(topology, props)
//          streams.start()
//         return streams
//         GlobalAppState.instance.streams(streams)
//    }

    //
    fun getRider(key: String) {
        val songStore: ReadOnlyKeyValueStore<Long, User> = streams!!.store(RIDERS_TABLE, QueryableStoreTypes.keyValueStore());

    }


}