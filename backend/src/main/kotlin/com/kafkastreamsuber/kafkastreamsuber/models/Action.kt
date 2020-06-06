package com.kafkastreamsuber.kafkastreamsuber.models

import com.kafkastreamsuber.kafkastreamsuber.objectMapper


enum class ACTION_TYPE {
    REQUEST_TRIP,
    CONFIRM_TRIP,
    UPDATE_DRIVER_LOCATION,
    START_TRIP,
    END_TRIP,
    SYNC_STATUS
}

data class Action(val type: ACTION_TYPE, val payload: String?)

data class RequestRidePayload(val riderLocation: Location, val destination: Location)
data class ConfirmRidePayload(val tripId: String, val driverLocation: Location)
data class EndTripPayload(val tripId: String, val amount: Double, val distance: Double)



val buildUpdateLocationAction = {
    user: User -> Action(ACTION_TYPE.UPDATE_DRIVER_LOCATION, objectMapper.writeValueAsString(user))
}

fun buildTripUpdateAction(trip: Trip): Action {
    val actionType = when (trip.status) {
        TripStatus.STARTED -> ACTION_TYPE.START_TRIP
        TripStatus.CONFIRMED -> ACTION_TYPE.CONFIRM_TRIP
        TripStatus.REQUESTING -> ACTION_TYPE.REQUEST_TRIP
        TripStatus.ENDED -> ACTION_TYPE.END_TRIP
    }
    return Action(actionType, objectMapper.writeValueAsString(trip))
}