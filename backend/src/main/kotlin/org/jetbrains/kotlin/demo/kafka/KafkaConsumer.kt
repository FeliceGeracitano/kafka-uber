package org.jetbrains.kotlin.demo.kafka

import org.apache.kafka.clients.admin.*
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import org.jetbrains.kotlin.demo.*
import org.jetbrains.kotlin.demo.serde.*
import org.springframework.stereotype.Service
import java.util.*
import javax.annotation.PostConstruct


@Service
class KafkaConsumer {
    // @Autowired
    // private lateinit var riderCtrl: RiderController
    var streams: KafkaStreams? = null
    var userStore: ReadOnlyKeyValueStore<String, User>? = null
    var tripStore: ReadOnlyKeyValueStore<String, Trip>? = null
    val kafkaProps = Properties()
    init {
        kafkaProps[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = KAFKA_BROKER
        kafkaProps[StreamsConfig.APPLICATION_ID_CONFIG] = "kafka-uber-consumer"
        kafkaProps[StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG] = 0
        kafkaProps[StreamsConfig.COMMIT_INTERVAL_MS_CONFIG] = 100
    }

    @PostConstruct
    fun run() {
        createTopics()
        buildStrems()
    }

    fun createTopics() {
        val kafkaClient = KafkaAdminClient.create(kafkaProps)
        val topics = kafkaClient.listTopics().names().get()
        if (!topics.contains(USER_TOPIC)) {
            kafkaClient.createTopics(listOf(NewTopic(USER_TOPIC, 1, 1)))
        }
        if (!topics.contains(TRIP_TOPIC)) {
            kafkaClient.createTopics(listOf(NewTopic(TRIP_TOPIC, 1, 1)))
        }
    }


    fun buildStrems() {
        val streamsBuilder = StreamsBuilder()

        val userStream: KStream<String, User> = streamsBuilder
            .stream(USER_TOPIC, Consumed.with(Serdes.String(), UserSerde()))

        val tripStream: KStream<String, Trip> = streamsBuilder
            .stream(TRIP_TOPIC, Consumed.with(Serdes.String(), TripSerde()))
        tripStream.foreach { key, trip ->
            println("[KafkaConsumer] [tripStream]: $trip")
        }

        userStream.groupByKey().reduce({ _, new -> new }, Materialized.`as`(USER_TABLE))
        tripStream.groupByKey().reduce({ _, new -> new }, Materialized.`as`(TRIP_TABLE))

//
//        userStream
//            .filter { key, _ -> key.startsWith("D") }
//            .groupByKey()
//            .windowedBy(TimeWindows.of(Duration.ofSeconds(3)).grace(Duration.ZERO))
//            .reduce { value1, value2 -> value2  }
//            .suppress(untilWindowCloses(Suppressed.BufferConfig.unbounded()))
//            .toStream()
//            .foreach { key, user ->
//                val _key = key.key()
//                println("[windowedBy]: $_key $user")
//                riderCtrl?.handleNewDriverLocation(key.key())
//            }

        val topology = streamsBuilder.build()

        streams = KafkaStreams(topology, kafkaProps)
        streams!!.cleanUp()
        streams!!.start()

        var waiting = true
        while (waiting) {
            try {
                Thread.sleep(1000)
                userStore = streams?.store(USER_TABLE, QueryableStoreTypes.keyValueStore<String, User>())
                tripStore = streams?.store(TRIP_TABLE, QueryableStoreTypes.keyValueStore<String, Trip>())
                waiting = false
            } catch (ex: Exception) {
                println("Stores NOT ready keep waiting...")
                println(ex)
            }
        }

        // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
        Runtime.getRuntime().addShutdownHook(Thread(Runnable { streams!!.close() }))
    }

    fun getUser(userId: String): User? {
        return userStore?.get(userId)
    }

    fun getTrip(tripId: String): Trip? {
        return tripStore?.get(tripId)
    }

}