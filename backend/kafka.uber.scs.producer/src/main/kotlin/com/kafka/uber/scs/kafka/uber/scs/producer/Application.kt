package com.kafka.uber.scs.kafka.uber.scs.producer

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Source
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.MessageBuilder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@EnableBinding(Source::class)
@RestController
class Application {

    @Autowired
    private lateinit var output: MessageChannel


	@PostMapping("/produce")
    fun produce(@RequestBody book: Book): Book {
        output.send(MessageBuilder.withPayload(book).build())
        return book
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}




