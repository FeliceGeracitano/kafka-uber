package org.jetbrains.kotlin.demo
import org.springframework.stereotype.Component
import java.util.*


@Component
class RiderController {

    fun requestRide() {
        // -- kafka actions
        val uuid: UUID = UUID.randomUUID()
        val randomUUIDString: String = uuid.toString()  // Generate tripId
        // write in "trip" topic ["tripId", {riderId, status: REQUESTING, from: Location, to: Location}]
        // write in "rider" topic, ["riderId" {currentTripId, location}]
    }
}