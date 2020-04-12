package org.jetbrains.kotlin.demo.controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.jetbrains.kotlin.demo.*
import org.jetbrains.kotlin.demo.kafka.KafkaConsumer
import org.jetbrains.kotlin.demo.kafka.KafkaProducer
import org.jetbrains.kotlin.demo.ws.WSRider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*


@Component
class RiderController {
    @Autowired
    private lateinit var wsRider: WSRider
    @Autowired
    private lateinit var driverCtrl: DriverController
    @Autowired
    private lateinit var kafkaProducer: KafkaProducer
    @Autowired
    private lateinit var kafkaConsumer: KafkaConsumer

    fun handleRequestTrip(riderId: String, riderLocation: Location, destination: Location) {

        val uuid: UUID = UUID.randomUUID()
        val tripUUID: String = uuid.toString()
        val trip = Trip(tripUUID, TripStatus.REQUESTING, null, riderId, riderLocation, destination, null)
        val rider = User(riderId, riderLocation, UserType.RIDER, tripUUID)
        kafkaProducer.produceRiders(rider)
        kafkaProducer.produceTrip(trip)
        driverCtrl.notifyDrivers(rider.id, trip)

    }

    fun getLastTripStatus(riderId: String): Trip? {
        val riderFromStore = kafkaConsumer.userStore?.get(riderId)
        val driver = kafkaConsumer.userStore?.get(getDriverId(riderId))
        val trip = kafkaConsumer.tripStore?.get(riderFromStore?.lastTripId ?: "")
        // trip?.driver = driver
        return trip
    }

//    fun tripConfirmed(driverId: String) {
//        val riderId = getRiderId(driverId)
//        val rider = kafkaConsumer.userStore?.get(riderId)!!
//        val trip = kafkaConsumer.tripStore?.get(rider?.lastTripId ?: "")!!
//        wsRider.sendMessageToRider(riderId, objectMapper.writeValueAsString(confirmTrip(trip)))
//    }

    fun handleNewDriverLocation(driverId: String) {
        if (!::kafkaConsumer.isInitialized) return;
        val driver = kafkaConsumer.userStore?.get(driverId) ?: return
        wsRider.sendMessageToRider(getRiderId(driverId), objectMapper.writeValueAsString(driverUpdateLocation(driver)))
    }

    fun handleTripUpdate(trip: Trip) {
        if (!::kafkaConsumer.isInitialized || trip.riderId == null) return;
        println("[X] CONSUMED TRIP STATUS: ${trip.status}")
        wsRider.sendMessageToRider(trip.riderId, objectMapper.writeValueAsString(tripUpdate(trip)))
    }

//    fun handleStartTrip(driverId: String) {
//        val riderId = getRiderId(driverId)
//        val rider = kafkaConsumer.userStore?.get(riderId)!!
//        val trip = kafkaConsumer.tripStore?.get(rider?.lastTripId ?: "")!!
//        wsRider.sendMessageToRider(riderId, objectMapper.writeValueAsString(startTrip(trip)))
//    }

//    fun endTrip(driverId: String) {
//        val rider = kafkaConsumer.userStore?.get(getRiderId(driverId))!!
//        rider.lastTripId = null
//        kafkaProducer.produceRiders(rider)
//    }

    // This is an hack to reach the driver in the same browser of the driver
    // driverId and riderId from the same browser will generate ids like: Dxxx && Rxxx
    private fun getRiderId(driverId: String): String {
        return driverId.replaceFirst("D", "R")
    }

    private fun getDriverId(riderId: String): String {
        return riderId.replaceFirst("R", "D")
    }


    // TODO: Maybe create a global Action Creator
    companion object {
        val objectMapper = jacksonObjectMapper()
        val REQUEST_TRIP = Action(ACTION_TYPE.REQUEST_TRIP, null)
//        fun requestRideAction(trip: Trip): Action {
//            return Action(ACTION_TYPE.REQUEST_TRIP, objectMapper.writeValueAsString(trip))
//        }

//        fun confirmTrip(trip: Trip): Action {
//            return Action(ACTION_TYPE.CONFIRM_TRIP, objectMapper.writeValueAsString(trip))
//        }

        val driverUpdateLocation =
            { user: User -> Action(ACTION_TYPE.UPDATE_DRIVER_LOCATION, objectMapper.writeValueAsString(user)) }


//        fun startTrip(trip: Trip): Action {
//            return Action(ACTION_TYPE.START_TRIP, objectMapper.writeValueAsString(trip))
//        }

        fun tripUpdate(trip: Trip): Action {
            val actionType = when (trip.status) {
                TripStatus.STARTED -> ACTION_TYPE.START_TRIP
                TripStatus.CONFIRMED -> ACTION_TYPE.CONFIRM_TRIP
                TripStatus.REQUESTING -> ACTION_TYPE.REQUEST_TRIP
                TripStatus.ENDED -> ACTION_TYPE.END_TRIP
                else -> ACTION_TYPE.EMPTY
            }
            return Action(actionType, objectMapper.writeValueAsString(trip))
        }

    }

}