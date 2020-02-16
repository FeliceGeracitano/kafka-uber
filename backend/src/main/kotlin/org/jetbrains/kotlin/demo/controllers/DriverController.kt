package org.jetbrains.kotlin.demo.controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.jetbrains.kotlin.demo.*
import org.jetbrains.kotlin.demo.controllers.RiderController.Companion.objectMapper
import org.jetbrains.kotlin.demo.ws.WSDriver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class DriverController() {


    fun confirmTrip(driverId: String, tripId: String, driverLocation: Location) {
        updateLocation(driverId, driverLocation)
        GlobalAppState.instance.trip[tripId]?.status = TripStatus.CONFIRMED;

    }

    fun updateLocation(driverId: String, location: Location) {
        GlobalAppState.instance.users[driverId]?.location = location
    }

    fun startTrip(driverId: String) {
        val driver = GlobalAppState.instance.users[driverId]
        GlobalAppState.instance.trip[driver?.lastTripId]?.status = TripStatus.STARTED
    }

    fun endTrip(driverId: String) {
        val driver = GlobalAppState.instance.users[driverId]
        GlobalAppState.instance.trip[driver?.lastTripId]?.status = TripStatus.ENDED
    }

    fun getLastTripStatus(driverId: String): Trip? {
        val driver = GlobalAppState.instance.users[driverId]
        return GlobalAppState.instance.trip[driver?.lastTripId]
    }


    fun getPedingRequests(driverId: String): Trip? {
        val riderId = driverId.replaceFirst("D", "R")
        val rider = GlobalAppState.instance.users[riderId] ?: return null
        return GlobalAppState.instance.trip[rider.lastTripId]
    }

    // TODO: Maybe create a global Action Creator
    companion object {
        val objectMapper = jacksonObjectMapper()
        val REQUEST_TRIP = Action(ACTION_TYPE.REQUEST_TRIP, null)
        fun requestRideAction(trip: Trip): Action {
            return Action(ACTION_TYPE.REQUEST_TRIP, objectMapper.writeValueAsString(trip))
        }
    }

}