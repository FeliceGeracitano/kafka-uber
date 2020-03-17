package org.jetbrains.kotlin.demo

import org.apache.avro.specific.SpecificRecord


enum class ACTION_TYPE {
    REQUEST_TRIP,

    // Driver
    CONFIRM_TRIP,
    UPDATE_DRIVER_LOCATION,
    START_TRIP,
    END_TRIP,

    SYNC_STATUS
}


enum class TripStatus {
    REQUESTING,
    CONFIRMED,
    STARTED,
    ENDED
}

enum class UserType {
    DRIVER,
    RIDER
}


data class Location(val lat: Double, val lon: Double)
data class User(
    val id: String,
    var location: Location?,
    val type: UserType,
    var lastTripId: String?)

data class Trip(
    val id: String,
    var status: TripStatus,
    var driverId: String?,
    val riderId: String?,
    val from: Location,
    val to: Location,
    var driver: User?
)

data class Action(val type: ACTION_TYPE, val payload: String?)
data class ConfirmRidePayload(val tripId: String, val driverLocation: Location)
data class RequestRidePayload(val destination: Location, val riderLocation: Location)


