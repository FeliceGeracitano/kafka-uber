package com.kafkastreamsuber.kafkastreamsuber

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
@EnableAutoConfiguration
class KafkaStreamsUberApplication

fun main(args: Array<String>) {
    runApplication<KafkaStreamsUberApplication>(*args)
}