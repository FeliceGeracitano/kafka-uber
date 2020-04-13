package com.kafkastreamsuber.kafkastreamsuber.models

enum class UserType {
    DRIVER,
    RIDER
}

data class User(
        val id: String,
        var location: Location?,
        val type: UserType,
        var lastTripId: String?
)