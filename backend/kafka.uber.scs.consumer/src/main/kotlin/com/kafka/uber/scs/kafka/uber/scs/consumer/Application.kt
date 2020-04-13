package com.kafka.uber.scs.kafka.uber.scs.consumer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.messaging.Sink
import org.springframework.cloud.stream.messaging.Source

@SpringBootApplication
@EnableBinding(Sink::class)
class Application {

	@StreamListener("input")
	private fun consumeMessage (book: Book) {
		println("consumeMessage $book ${book.id} ${book.name}")

	}
}

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
