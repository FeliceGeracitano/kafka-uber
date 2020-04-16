package com.kafkastreamsuber.kafkastreamsuber.models

enum class TripStatus {
    REQUESTING,
    CONFIRMED,
    STARTED,
    ENDED
}

data class Trip(
        val id: String,
        var status: TripStatus,
        var driverId: String?,
        val riderId: String?,
        val from: Location,
        val to: Location,
        var driver: User?
)