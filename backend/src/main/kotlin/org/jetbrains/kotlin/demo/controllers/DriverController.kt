package org.jetbrains.kotlin.demo.controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.jetbrains.kotlin.demo.*
import org.jetbrains.kotlin.demo.kafka.KafkaConsumer
import org.jetbrains.kotlin.demo.kafka.KafkaProducer
import org.jetbrains.kotlin.demo.ws.WSDriver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class DriverController {
    @Autowired
    private lateinit var wsDriver: WSDriver
    @Autowired
    private lateinit var riderCtrl: RiderController
    @Autowired
    private lateinit var kafkaProducer: KafkaProducer
    @Autowired
    private lateinit var kafkaConsumer: KafkaConsumer


    fun initDriver(driverId: String) {
        if (kafkaConsumer.userStore?.get(driverId) !== null) return
        kafkaProducer.produceDriver(
            User(driverId, null, UserType.DRIVER, null)
        )
    }

    fun confirmTrip(driverId: String, tripId: String, driverLocation: Location) {
        // Update Driver
        updateLocation(driverId, driverLocation)
        var driver = kafkaConsumer.userStore?.get(driverId)!!
        driver.lastTripId = tripId
        kafkaProducer.produceDriver(driver)

        // Update Trip
        val trip = kafkaConsumer.tripStore?.get(tripId)!!
        trip.driver =  kafkaConsumer.userStore?.get(driverId)!!
        trip.driverId = driverId
        trip.status = TripStatus.CONFIRMED
        kafkaProducer.produceTrip(trip)
    }

    fun updateLocation(driverId: String, location: Location) {
        var driver = kafkaConsumer.userStore?.get(driverId)!!
        driver.location = location
        // riderCtrl.handleNewDriverLocation(driverId)
        kafkaProducer.produceDriver(driver)
    }

    fun startTrip(driverId: String) {
        val driver = kafkaConsumer.userStore?.get(driverId)!!
        val trip = kafkaConsumer.tripStore?.get(driver.lastTripId)!!
        trip.status = TripStatus.STARTED
        kafkaProducer.produceTrip(trip)
    }

    fun endTrip(driverId: String) {
        val driver = kafkaConsumer.getUser(driverId)!!
        val trip =  kafkaConsumer.getTrip(driver.lastTripId ?: "")!!

        // Update Trip
        trip.status = TripStatus.ENDED
        kafkaProducer.produceTrip(trip)

        // Update Driver
        driver?.lastTripId = null
        kafkaProducer.produceDriver(driver)
    }

    fun getLastTripStatus(driverId: String): Trip? {
        val driver = kafkaConsumer?.getUser(driverId)
        return kafkaConsumer.getTrip(driver?.lastTripId ?: "")
    }


    fun getPendingRequests(driverId: String): Trip? {
        val rider = kafkaConsumer.getUser(getRiderId(driverId)) ?: return null
        return  kafkaConsumer.getTrip(rider?.lastTripId ?: "")
    }

    fun notifyDrivers(riderId: String, trip: Trip) {
        // We should send a request out to all available...
        // This is an hack to reach the driver in the same browser of the driver
        // driverId and riderId from the same browser will generate ids like: Dxxx && Rxxx
        val driverId = riderId.replaceFirst("R", "D")
        wsDriver.sendMessage(driverId, RiderController.objectMapper.writeValueAsString(
            RiderController.tripUpdate(trip)
        ))
    }


    fun handleTripUpdate(trip: Trip) {
        if (!::kafkaConsumer.isInitialized || trip.driverId == null) return;
        wsDriver.sendMessage(
            trip.driverId!!,
            RiderController.objectMapper.writeValueAsString(RiderController.tripUpdate(trip))
        )
    }

    // This is an hack to reach the driver in the same browser of the driver
    // driverId and riderId from the same browser will generate ids like: Dxxx && Rxxx
    private fun getRiderId(driverId: String): String {
        return driverId.replaceFirst("D", "R")
    }


    // TODO: Maybe create a global Action Creator
    companion object {
        val objectMapper = jacksonObjectMapper()
    }

}