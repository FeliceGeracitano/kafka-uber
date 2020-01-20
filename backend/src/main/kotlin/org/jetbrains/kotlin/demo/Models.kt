package org.jetbrains.kotlin.demo

enum class ActionType {
    REQUEST_RIDE,
    CONFIRM_RIDE,
    DRIVER_UPDATE_LOCATION,
    START_RIDE,
    END_RIDE
}
enum class UserStatus {
    INACTIVE,
    ACTIVE,
    AVAILABLE,
    REQUESTING,
    BUSY
}
enum class RideStatus {
    ENDED,
    CANCELLED,
    ACTIVE
}

data class Location(val lat: Double, val lng: Double)

class User(val id: String, val location: Location?, val status: UserStatus)

data class Action(val type: ActionType, val payload: ActionPayload)

data class ActionPayload(val driver: User?, val rider: User?, val destination: Location?)

data class Ride(val id: String, val driverId: String?,  val riderId: String?, val destination: Location?)