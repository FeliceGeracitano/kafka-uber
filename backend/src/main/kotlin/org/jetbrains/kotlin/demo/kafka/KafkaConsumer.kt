package org.jetbrains.kotlin.demo.kafka

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import org.jetbrains.kotlin.demo.*
import org.jetbrains.kotlin.demo.serde.TripSerde
import org.jetbrains.kotlin.demo.serde.UserSerde
import org.springframework.stereotype.Service
import java.util.*
import javax.annotation.PostConstruct


@Service
class KafkaConsumer {
    var streams: KafkaStreams? = null
    var userStore: ReadOnlyKeyValueStore<String, User>? = null
    var tripStore: ReadOnlyKeyValueStore<String, Trip>? = null

    @PostConstruct
    fun init() {
        process()
    }

    fun process() {
        val streamsBuilder = StreamsBuilder()

        val userStream: KStream<String, User> = streamsBuilder
            .stream(USER_TOPIC, Consumed.with(Serdes.String(), UserSerde()))

        userStream.foreach { key, user ->
            println("[KafkaConsumer] [userStream]: $user")
        }


        val tripStream: KStream<String, Trip> = streamsBuilder
            .stream(TRIP_TOPIC, Consumed.with(Serdes.String(), TripSerde()))
        tripStream.foreach { key, trip ->
            println("[KafkaConsumer] [tripStream]: $trip")
        }

        userStream.groupByKey().reduce({ _, new -> new }, Materialized.`as`(USER_TABLE))
        tripStream.groupByKey().reduce({ _, new -> new }, Materialized.`as`(TRIP_TABLE))

        val topology = streamsBuilder.build()

        val props = Properties()
        props["bootstrap.servers"] = KAFKA_BROKER
        props["application.id"] = "kafka-uber-consumer"
        streams = KafkaStreams(topology, props)

        streams!!.cleanUp()
        streams!!.start()

        var waiting  = true
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