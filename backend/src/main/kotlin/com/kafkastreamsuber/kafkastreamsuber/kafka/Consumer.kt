package com.kafkastreamsuber.kafkastreamsuber.kafka

import com.kafkastreamsuber.kafkastreamsuber.*
import com.kafkastreamsuber.kafkastreamsuber.cassandra.keyspace.trip.TripRepository
import com.kafkastreamsuber.kafkastreamsuber.cassandra.keyspace.user.UserRepository
import com.kafkastreamsuber.kafkastreamsuber.models.*
import com.kafkastreamsuber.kafkastreamsuber.ws.WSDriver
import com.kafkastreamsuber.kafkastreamsuber.ws.WSRider
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.Grouped
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Materialized
import org.jetbrains.kotlin.demo.serde.TripSerde
import org.jetbrains.kotlin.demo.serde.UserSerde
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.Input
import org.springframework.cloud.stream.annotation.StreamListener


@EnableBinding(Consumer.Bindings::class)
class Consumer {
    @Autowired
    lateinit var userRepository: UserRepository
    @Autowired
    lateinit var tripRepository: TripRepository
    @Autowired
    private lateinit var wsRider: WSRider
    @Autowired
    private lateinit var wsDriver: WSDriver

    @StreamListener(Consumer.Bindings.USER_TOPIC)
    private fun processUsers(event: KStream<String, User>) {
        event
            .map { _, value -> KeyValue(value.id, value) }
            .groupByKey(Grouped.with(Serdes.String(), UserSerde()))
            .reduce({ _, new -> new }, Materialized.`as`(USER_STORE))
            .toStream()
            .filter { _, user -> user.type === UserType.DRIVER }
            .foreach { _, driver ->
                wsRider.sendMessageToRider(
                    getRiderId(driver.id),
                    objectMapper.writeValueAsString(buildUpdateLocationAction(driver))
                )
            }

        event
            .foreach { _, user ->
                try {
                    userRepository.insert(UserEvent(user))
                } catch (e: Exception) {
                    println(e)
                }
            }

    }

    @StreamListener(Consumer.Bindings.TRIP_TOPIC)
    private fun processTrips(event: KStream<String, Trip>) {
        event
            .map { _, value -> KeyValue(value.id, value) }
            .groupByKey(Grouped.with(Serdes.String(), TripSerde()))
            .reduce({ _, new -> new }, Materialized.`as`(TRIP_STORE))
            .toStream()
            .foreach { _, trip ->
                if (trip.riderId is String) {
                    wsRider.sendMessageToRider(trip.riderId, objectMapper.writeValueAsString(buildTripUpdateAction(trip)))
                }
                if (trip.driverId is String || trip.status == TripStatus.REQUESTING && trip.riderId is String) {
                    val driverId = trip.driverId ?: getDriverId(trip?.riderId)
                    wsDriver.sendMessage(driverId, objectMapper.writeValueAsString(buildTripUpdateAction(trip)))
                }
            }

        event
            .foreach { _, trip ->
                try {
                    tripRepository.insert(TripEvent(trip))
                } catch (e: Exception) {
                    println(e)
                }
            }
    }

    internal interface Bindings {
        @Input(USER_TOPIC)
        fun user(): KStream<String?, User>?

        @Input(TRIP_TOPIC)
        fun trip(): KStream<String?, Trip?>?

        companion object {
            const val USER_TOPIC = "input_1"
            const val TRIP_TOPIC = "input_2"
        }
    }
}
