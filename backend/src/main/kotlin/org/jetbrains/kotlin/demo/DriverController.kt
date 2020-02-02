package org.jetbrains.kotlin.demo
import org.springframework.stereotype.Component
import java.util.*


@Component
class DriverController {

    fun confirmRider() {
        // write in rideTopic ["rideUid", {status: CONFIRMED, driverUid}]
        // write in driverTopic ["driverUid", location}]
    }

    fun updateLocation(location: Location) {
        // write in driverTopic ["driverUid", location}]
    }
}