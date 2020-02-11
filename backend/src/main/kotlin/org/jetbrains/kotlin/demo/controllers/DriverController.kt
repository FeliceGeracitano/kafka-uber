package org.jetbrains.kotlin.demo.controllers
import org.jetbrains.kotlin.demo.*
import org.jetbrains.kotlin.demo.kafka.KafkaConsumer
import org.jetbrains.kotlin.demo.kafka.KafkaProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class DriverController {

    @Autowired
    private lateinit var kafkaProducer: KafkaProducer
    private lateinit var kafkaConsumer: KafkaConsumer

    fun confirmTrip(driverId: String, tripId: String, driverLocation: Location) {
        updateLocation(driverId, driverLocation, tripId)
        // GET From topic and update
        val trip = Trip("", TripStatus.CONFIRMED, "", "", Location(0.0, 0.0))
        kafkaProducer.produceTrip(trip);
    }

    fun updateLocation(driverId: String, location: Location, lastTripId: String?) {
        // GET From topic and update
        val driver = User("", location, UserType.DRIVER, "")
        kafkaProducer.produceDriver(driver)
    }

    fun startTrip(driverId: String) {
        // getTrip from driver
        val driver = User("", null, UserType.DRIVER, null)
        // getTrip driver.lastTripId
        val trip = Trip("", TripStatus.STARTED, "", "", Location(0.0, 0.0))
        kafkaProducer.produceTrip(trip);
    }

    fun endTrip(driverId: String) {
        // getDriver("driverId")
        val driver = User("", null, UserType.DRIVER, null)
        // getTrip("driverId")
        val trip = Trip("", TripStatus.ENDED, "", "", Location(0.0, 0.0))
        // getTrip driver.lastTripId
        kafkaProducer.produceTrip(trip);
    }
}