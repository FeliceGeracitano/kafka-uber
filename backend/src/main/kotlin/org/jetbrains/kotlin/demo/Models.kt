package org.jetbrains.kotlin.demo


enum class ACTION_TYPE {
    REQUEST_TRIP,

    // Driver
    CONFIRM_RIDE,
    UPDATE_DRIVER_LOCATION,
    START_RIDE,
    END_RIDE,

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
data class User(val id: String, var location: Location?, val type: UserType, val lastTripId: String?)
data class Trip(
    val id: String,
    var status: TripStatus,
    val driverId: String?,
    val riderId: String?,
    val from: Location,
    val to: Location
)

data class Action(val type: ACTION_TYPE, val payload: String?)
data class ConfirmRidePayload(val tripId: String, val driverLocation: Location)
data class RequestRidePayload(val destination: Location, val riderLocation: Location)
