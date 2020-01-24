package org.jetbrains.kotlin.demo

val ridersTopic = "riders"
val driversTopic = "driver"
val ridesTopic = "rides"
val kafkaBroker = "localhost:31090"


fun parseURlQuery(query: String): Map<String, String> = query
    .split("&")
    .map { el -> el.split("=") }
    .map { it[0] to it[1] }.toMap()