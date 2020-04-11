package org.jetbrains.kotlin.demo.kafka

import org.apache.kafka.clients.admin.KafkaAdminClient
import org.apache.kafka.clients.admin.NewTopic
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
import org.jetbrains.kotlin.demo.controllers.DriverController
import org.jetbrains.kotlin.demo.controllers.RiderController
import org.jetbrains.kotlin.demo.serde.TripSerde
import org.jetbrains.kotlin.demo.serde.UserSerde
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import javax.annotation.PostConstruct


@Service
class KafkaConsumer {

    @Autowired
    private lateinit var riderCtrl: RiderController
    @Autowired
    private lateinit var driverCtrl: DriverController
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
        buildStreams()
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

    fun buildStreams() {
        val streamsBuilder = StreamsBuilder()

        val userStream: KStream<String, User> = streamsBuilder
            .stream(USER_TOPIC, Consumed.with(Serdes.String(), UserSerde()))

        val driversStream = userStream.filter { _, user -> user.type === UserType.DRIVER }

        val tripStream: KStream<String, Trip> = streamsBuilder
            .stream(TRIP_TOPIC, Consumed.with(Serdes.String(), TripSerde()))

        userStream.groupByKey().reduce({ _, new -> new }, Materialized.`as`(USER_TABLE))
        tripStream.groupByKey().reduce({ _, new -> new }, Materialized.`as`(TRIP_TABLE))

//        streamsBuilder.globalTable(
//            TRIP_TOPIC,
//            Materialized.`as`<String, Trip, KeyValueStore<Bytes, ByteArray>>(TRIP_TABLE)
//                .withKeySerde(Serdes.String())
//                .withValueSerde(TripSerde())
//        )

        driversStream
            .foreach { key, _ ->
                riderCtrl?.handleNewDriverLocation(key)
            }

        tripStream
            .foreach { _, trip ->
                riderCtrl?.handleTripUpdate(trip)
                driverCtrl?.handleTripUpdate(trip)
            }

        val topology = streamsBuilder.build()
        streams = KafkaStreams(topology, kafkaProps)
        streams!!.cleanUp()
        streams!!.start()

        while (streams!!.state() !== KafkaStreams.State.RUNNING) {
            try {
                Thread.sleep(2000)
                userStore = streams?.store(USER_TABLE, QueryableStoreTypes.keyValueStore<String, User>())
                tripStore = streams?.store(TRIP_TABLE, QueryableStoreTypes.keyValueStore<String, Trip>())
            } catch (ex: Exception) {
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