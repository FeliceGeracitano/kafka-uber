package com.kafkastreamsuber.kafkastreamsuber

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper


fun parseURLQuery(query: String): Map<String, String> = query
        .split("&")
        .map { el -> el.split("=") }
        .map { it[0] to it[1] }.toMap()


fun getRiderId(driverId: String?): String {
        return driverId?.replaceFirst("D", "R") ?: ""
}

fun getDriverId(riderId: String?): String {
        return riderId?.replaceFirst("R", "D")?: ""
}

val objectMapper = jacksonObjectMapper()


