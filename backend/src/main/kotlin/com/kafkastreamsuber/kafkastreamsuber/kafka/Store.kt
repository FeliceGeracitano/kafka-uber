package com.kafkastreamsuber.kafkastreamsuber.kafka

import com.kafkastreamsuber.kafkastreamsuber.TRIP_STORE
import com.kafkastreamsuber.kafkastreamsuber.USER_STORE
import com.kafkastreamsuber.kafkastreamsuber.getRiderId
import com.kafkastreamsuber.kafkastreamsuber.models.Trip
import com.kafkastreamsuber.kafkastreamsuber.models.User
import org.apache.kafka.streams.state.QueryableStoreTypes.keyValueStore
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService
import org.springframework.stereotype.Component


@Component
class Store {
    private val LOGGER = LoggerFactory.getLogger(Store::class.java)
    @Autowired
    private lateinit var queryService: InteractiveQueryService
    private var userStore: ReadOnlyKeyValueStore<String, User>? = null
    private var tripStore: ReadOnlyKeyValueStore<String, Trip>? = null

    fun getUser(id: String): User? {
        return try {
            if (userStore == null) userStore = queryService.getQueryableStore(USER_STORE, keyValueStore());
            userStore?.get(id)
        } catch (e: Exception) {
            LOGGER.error("User store error: $e")
            null
        }
    }


    fun getTrip(id: String?): Trip? {
        return try {
            if (tripStore == null) tripStore = queryService.getQueryableStore(TRIP_STORE, keyValueStore());
            tripStore?.get(id)
        } catch (e: Exception) {
            LOGGER.error("Trip store error: $e")
            null
        }
    }

    fun getLastTrip(userId: String): Trip? {
        val user = getUser(userId) ?: return null
        return getTrip(user.lastTripId)
    }

    fun getPendingRequests(driverId: String): Trip? {
        val user = getUser(getRiderId(driverId))?: return null
        return getTrip(user.lastTripId)
    }
}