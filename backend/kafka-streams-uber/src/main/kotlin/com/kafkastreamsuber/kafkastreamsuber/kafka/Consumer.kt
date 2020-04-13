package com.kafkastreamsuber.kafkastreamsuber.kafka

import com.kafkastreamsuber.kafkastreamsuber.*
import com.kafkastreamsuber.kafkastreamsuber.models.*
import com.kafkastreamsuber.kafkastreamsuber.ws.WSDriver
import com.kafkastreamsuber.kafkastreamsuber.ws.WSRider
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.kstream.Materialized
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.Input
import org.springframework.cloud.stream.annotation.StreamListener


@EnableBinding(ConsumerBinding::class)
class Consumer {
    @Autowired
    private lateinit var wsRider: WSRider
    @Autowired
    private lateinit var wsDriver: WSDriver

    @StreamListener("input_user")
    private fun materializeUsers( event: KStream<String, User>) {
        event
            .groupByKey()
            .reduce({ _, new -> new }, Materialized.`as`(USER_STORE))
            .toStream()
            .filter { _, user -> user.type === UserType.DRIVER }
            .foreach { _, driver ->
                wsRider.sendMessageToRider(
                    getRiderId(driver.id),
                    JsonParser.writeValueAsString(buildUpdateLocationAction(driver))
                )
            }
    }

    @StreamListener("input_trip")
    private fun materializeTrips(event: KStream<String, Trip>) {
        event
            .groupByKey()
            .reduce({ _, new -> new }, Materialized.`as`(TRIP_STORE))
            .toStream()
            .foreach { _, trip ->
                if (trip.riderId is String)
                    wsRider.sendMessageToRider(trip.riderId, JsonParser.writeValueAsString(buildTripUpdateAction(trip)))
                if (trip.driverId is String)
                    wsDriver.sendMessage(trip.driverId!!,
                        JsonParser.writeValueAsString(buildTripUpdateAction(trip))
                    )
                if (trip.status === com.kafkastreamsuber.kafkastreamsuber.models.TripStatus.REQUESTING && trip.riderId is String) {
                    wsDriver.sendMessage(getDriverId(trip.riderId), JsonParser.writeValueAsString(buildTripUpdateAction(trip)))
                }
            }

    }
}


interface ConsumerBinding {
    @Input("input_user")
    fun input_user(): KStream<String, User>?

    @Input("input_trip")
    fun input_trip(): KStream<String, Trip>?
}