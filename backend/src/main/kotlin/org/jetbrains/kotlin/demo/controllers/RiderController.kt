package org.jetbrains.kotlin.demo.controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.jetbrains.kotlin.demo.*
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

    fun handleRequestTrip(riderId: String, riderLocation: Location, destination: Location) {
        val uuid: UUID = UUID.randomUUID()
        val tripUUID: String = uuid.toString()
        val trip = Trip(tripUUID, TripStatus.REQUESTING, null, riderId, riderLocation, destination, null)
        val rider = User(riderId, riderLocation, UserType.RIDER, tripUUID)
        GlobalAppState.instance.trip[trip.id] = trip
        GlobalAppState.instance.users[rider.id] = rider
        driverCtrl.notifyDrivers(rider.id, trip)
    }

    fun getLastTripStatus(riderId: String): Trip? {
        val rider = GlobalAppState.instance.users[riderId]
        return GlobalAppState.instance.trip[rider?.lastTripId]
    }

    fun tripConfirmed(driverId: String) {
        val riderId = driverId.replaceFirst("D", "R")
        val rider = GlobalAppState.instance.users[riderId]!!
        val trip = GlobalAppState.instance.trip[rider.lastTripId]!!
        wsRider.sendMessageToRider(riderId, objectMapper.writeValueAsString(confirmTrip(trip)))
    }

    fun handleNewDriverLocation(driverId: String) {
        val riderId = driverId.replaceFirst("D", "R")
        val driver = GlobalAppState.instance.users[driverId]!!
        wsRider.sendMessageToRider(riderId, objectMapper.writeValueAsString(driverUpdateLocation(driver)))
    }

    fun handleStartTrip(driverId: String) {
        val riderId = driverId.replaceFirst("D", "R")
        val rider = GlobalAppState.instance.users[riderId]!!
        val trip = GlobalAppState.instance.trip[rider.lastTripId]!!
        wsRider.sendMessageToRider(riderId, objectMapper.writeValueAsString(startTrip(trip)))
    }


    // TODO: Maybe create a global Action Creator
    companion object {
        val objectMapper = jacksonObjectMapper()
        val REQUEST_TRIP = Action(ACTION_TYPE.REQUEST_TRIP, null)
        fun requestRideAction(trip: Trip): Action {
            return Action(ACTION_TYPE.REQUEST_TRIP, objectMapper.writeValueAsString(trip))
        }

        fun confirmTrip(trip: Trip): Action {
            return Action(ACTION_TYPE.CONFIRM_TRIP, objectMapper.writeValueAsString(trip))
        }

        val driverUpdateLocation =
            { user: User -> Action(ACTION_TYPE.UPDATE_DRIVER_LOCATION, objectMapper.writeValueAsString(user)) }


        fun startTrip(trip: Trip): Action {
            return Action(ACTION_TYPE.START_TRIP, objectMapper.writeValueAsString(trip))
        }

    }

}