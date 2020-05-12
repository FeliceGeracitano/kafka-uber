package com.kafkastreamsuber.kafkastreamsuber.models

import com.datastax.driver.core.DataType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import java.util.*

enum class TripStatus {
    REQUESTING,
    CONFIRMED,
    STARTED,
    ENDED
}

data class Trip(
        val id: String,
        var status: TripStatus,
        var driverId: String?,
        val riderId: String?,
        val from: Location,
        val to: Location,
        var driver: User? = null,
        var prince: Double? = null
)

@Table
class TripEvent(trip: Trip) {
    @PrimaryKey("uid")
    var uid: String = Date().time.toString()

    @Column("timestamp")
    @CassandraType(type = DataType.Name.LIST, typeArguments = [DataType.Name.TIMESTAMP])
    var timestamp: List<Long> = listOf(Date().time)

    @Column("status")
    @CassandraType(type = DataType.Name.LIST, typeArguments = [DataType.Name.TEXT])
    var status: List<String> = listOf(trip.status.name)

    @Column("driverId")
    @CassandraType(type = DataType.Name.LIST, typeArguments = [DataType.Name.TEXT])
    var driverId: List<String> = listOf(trip.driverId?:"")

    @Column("riderId")
    @CassandraType(type = DataType.Name.LIST, typeArguments = [DataType.Name.TEXT])
    var riderId: List<String> = listOf(trip.riderId?:"")

    @Column("fromLocation")
    @CassandraType(type = DataType.Name.LIST, typeArguments = [DataType.Name.UDT], userTypeName = "geo_point")
    var fromLocation: List<Location?> = listOf(trip.from)

    @Column("toLocation")
    @CassandraType(type = DataType.Name.LIST, typeArguments = [DataType.Name.UDT], userTypeName = "geo_point")
    var toLocation: List<Location?> = listOf(trip.to)
}