package com.kafkastreamsuber.kafkastreamsuber

fun parseURLQuery(query: String): Map<String, String> = query
        .split("&")
        .map { el -> el.split("=") }
        .map { it[0] to it[1] }.toMap()