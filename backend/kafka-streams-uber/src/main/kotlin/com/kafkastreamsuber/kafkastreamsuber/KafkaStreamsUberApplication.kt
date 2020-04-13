package com.kafkastreamsuber.kafkastreamsuber

import com.kafkastreamsuber.kafkastreamsuber.models.User
import com.kafkastreamsuber.kafkastreamsuber.models.UserType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.messaging.MessageChannel
import javax.annotation.PostConstruct

@SpringBootApplication
class KafkaStreamsUberApplication

fun main(args: Array<String>) {
    runApplication<KafkaStreamsUberApplication>(*args)
}
