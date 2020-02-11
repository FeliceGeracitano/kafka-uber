package org.jetbrains.kotlin.demo

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.util.StdDateFormat
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

val ridersTopic = "riders"
val driversTopic = "driver"
val tripTopic = "tripTopic"
val ridesTopic = "rides"
val kafkaBroker = "localhost:31090"
val DRIVERS_TABLE = "drivers_table"


fun parseURlQuery(query: String): Map<String, String> = query
    .split("&")
    .map { el -> el.split("=") }
    .map { it[0] to it[1] }.toMap()


val jsonMapper = ObjectMapper().apply {
    registerKotlinModule()
    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    dateFormat = StdDateFormat()
}