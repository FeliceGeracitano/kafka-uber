package com.kafkastreamsuber.kafkastreamsuber

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper


const val USER_STORE = "user_store"
const val TRIP_STORE = "trip_store"



fun getRiderId(driverId: String): String {
    return driverId.replaceFirst("D", "R")
}

fun getDriverId(riderId: String): String {
    return riderId.replaceFirst("R", "D")
}

val JsonParser = jacksonObjectMapper()
