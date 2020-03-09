package org.jetbrains.kotlin.demo

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.util.StdDateFormat
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

val USER_TOPIC = "user_topic"
val USER_TABLE = "user_table"
val TRIP_TOPIC = "trip_topic"
val TRIP_TABLE = "trip_table"
val KAFKA_BROKER = "localhost:9092"
val APPLICATION_ID = "kafka_uber_demo"


fun parseURlQuery(query: String): Map<String, String> = query
    .split("&")
    .map { el -> el.split("=") }
    .map { it[0] to it[1] }.toMap()


val jsonMapper = ObjectMapper().apply {
    registerKotlinModule()
    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    dateFormat = StdDateFormat()
}