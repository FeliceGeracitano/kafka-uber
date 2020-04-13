package com.kafkastreamsuber.kafkastreamsuber.kafka

import com.kafkastreamsuber.kafkastreamsuber.TRIP_STORE
import com.kafkastreamsuber.kafkastreamsuber.USER_STORE
import com.kafkastreamsuber.kafkastreamsuber.getRiderId
import com.kafkastreamsuber.kafkastreamsuber.models.Trip
import com.kafkastreamsuber.kafkastreamsuber.models.User
import org.apache.kafka.streams.state.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService
import org.springframework.cloud.stream.messaging.Source
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component


@Component
@EnableBinding(Source::class)
class Store {
    @Autowired
    private lateinit var queryService: InteractiveQueryService
    private var userStore: ReadOnlyKeyValueStore<String, User>? = null
    private var tripStore: ReadOnlyKeyValueStore<String, Trip>? = null

    fun getUser(id: String): User? {
        return try {
            if (userStore == null) userStore = queryService?.getQueryableStore(USER_STORE, QueryableStoreTypes.keyValueStore());
            userStore?.get(id)
        } catch (e: Exception) {
            println(e)
            null
        }
    }


    fun getTrip(id: String?): Trip? {
        return try {
            if (tripStore == null) tripStore = queryService?.getQueryableStore(TRIP_STORE, QueryableStoreTypes.keyValueStore());
            tripStore?.get(id)
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    fun getLastTrip(userId: String): Trip? {
        return getTrip(getUser(userId)?.lastTripId)
    }

    fun getPendingRequests(driverId: String): Trip? {
        return getTrip(getUser(getRiderId(driverId))?.lastTripId)
    }
}