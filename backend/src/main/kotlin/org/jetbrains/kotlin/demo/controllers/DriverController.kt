package org.jetbrains.kotlin.demo.controllers
import org.jetbrains.kotlin.demo.*
import org.springframework.stereotype.Service


@Service
class DriverController {


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
}