package org.jetbrains.kotlin.demo.controllers

import GlobalAppState
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.jetbrains.kotlin.demo.*
import org.jetbrains.kotlin.demo.ws.WSDriver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class DriverController {

    @Autowired
    private lateinit var wsDriver: WSDriver
    @Autowired
    private lateinit var riderCtrl: RiderController


    fun init(driverId: String) {
        if (GlobalAppState.instance.users[driverId] !== null) return
        GlobalAppState.instance.users[driverId] = User(driverId, null, UserType.DRIVER, null)
    }

    fun confirmTrip(driverId: String, tripId: String, driverLocation: Location) {
        GlobalAppState.instance.users[driverId]!!.lastTripId = tripId
        GlobalAppState.instance.trip[tripId]!!.driverId = driverId
        GlobalAppState.instance.trip[tripId]!!.status = TripStatus.CONFIRMED
        riderCtrl.tripConfirmed(driverId)
        updateLocation(driverId, driverLocation)
    }

    fun updateLocation(driverId: String, location: Location) {
        GlobalAppState.instance.users[driverId]?.location = location
        riderCtrl.handleNewDriverLocation(driverId)
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


    fun getPendingRequests(driverId: String): Trip? {
        val riderId = driverId.replaceFirst("D", "R")
        val rider = GlobalAppState.instance.users[riderId] ?: return null
        return GlobalAppState.instance.trip[rider.lastTripId]
    }

    fun notifyDrivers(riderId: String, trip: Trip) {
        // We should send a request out to all available...
        // This is an hack to reach the driver in the same browser of the driver
        // driverId and riderId from the same browser will generate ids like: Dxxx && Rxxx
        val driverId = riderId.replaceFirst("R", "D")
        wsDriver.sendMessage(driverId, RiderController.objectMapper.writeValueAsString(
            RiderController.requestRideAction(
                trip
            )
        ))
    }


    // TODO: Maybe create a global Action Creator
    companion object {
        val objectMapper = jacksonObjectMapper()
    }

}