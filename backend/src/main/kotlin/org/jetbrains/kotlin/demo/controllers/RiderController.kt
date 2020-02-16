package org.jetbrains.kotlin.demo.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.jetbrains.kotlin.demo.*
import org.jetbrains.kotlin.demo.ws.WSDriver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*


@Component
class RiderController(
    @Autowired
    val wsdriver: WSDriver
) {

    fun handleRequestRide(riderId: String, riderLocation: Location, destination: Location) {
        val uuid: UUID = UUID.randomUUID()
        val tripUUID: String = uuid.toString()
        val trip = Trip(tripUUID, TripStatus.REQUESTING, null, riderId, riderLocation, destination)
        val rider = User(riderId, riderLocation, UserType.RIDER, tripUUID)
        GlobalAppState.instance.trip[trip.id] = trip
        GlobalAppState.instance.users[rider.id] = rider
        notifyDrivers(rider.id, trip)
    }

    fun getLastTripStatus(riderId: String): Trip? {
        val rider = GlobalAppState.instance.users[riderId]
        return GlobalAppState.instance.trip[rider?.lastTripId]
    }



    private fun notifyDrivers(riderId: String, trip: Trip) {
        // We should send a request out to all available...
        // This is an hack to reach the driver in the same browser of the driver
        // driverId and riderId from the same browser will generate ids like: Dxxx && Rxxx
        val driverId= riderId.replaceFirst("R", "D")
        wsdriver.sendMessage(driverId, objectMapper.writeValueAsString(requestRideAction(trip)))
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