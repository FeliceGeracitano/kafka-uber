package org.jetbrains.kotlin.demo


enum class ClientActions {
    // Rider
    REQUEST_RIDE,

    // Driver
    CONFIRM_RIDE,
    UPDATE_DRIVER_LOCATION,
    START_RIDE,
    END_RIDE,



    //
    A,
    B
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


data class Location(val lat: Double, val lng: Double)
data class User(val id: String, val location: Location?, val type: UserType, val lastTripId: String?)
data class Trip(
    val id: String,
    val status: TripStatus,
    val driverId: String?,
    val riderId: String?,
    val destination: Location
)

data class Action(val type: ClientActions, val payload: String?)
data class ConfirmRidePayload(val tripId: String, val driverLocation: Location)
data class RequestRidePayload(val destination: Location, val riderLocation: Location)
