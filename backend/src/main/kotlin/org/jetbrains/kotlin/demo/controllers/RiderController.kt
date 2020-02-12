package org.jetbrains.kotlin.demo.controllers

import org.jetbrains.kotlin.demo.*
import org.jetbrains.kotlin.demo.kafka.KafkaConsumer
import org.jetbrains.kotlin.demo.kafka.KafkaProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*


@Component
class RiderController {

    @Autowired
    private var kafkaProducer = KafkaProducer()

    @Autowired
    private var kafkaConsumer = KafkaConsumer()

    fun requestRide(riderId: String, riderLocation: Location, destination: Location) {
        val uuid: UUID = UUID.randomUUID()
        val tripUUID: String = uuid.toString()

        val trip = Trip(tripUUID, TripStatus.REQUESTING, null, riderId, destination)
        kafkaProducer.produceTrip(trip)
        val rider = User(riderId, riderLocation, UserType.RIDER, tripUUID)
        kafkaProducer.produceRiders(rider)
    }



    fun getLastRiderState(key: String) {
        kafkaConsumer.getLastDriver(key)
    }

    fun produce(key: String) {
        kafkaProducer.produceRiders(User(key, null, UserType.RIDER, null))
    }

}