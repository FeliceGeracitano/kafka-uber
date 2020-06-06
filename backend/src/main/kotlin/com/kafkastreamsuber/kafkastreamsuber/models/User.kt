package com.kafkastreamsuber.kafkastreamsuber.models

import com.datastax.driver.core.DataType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import java.util.*

enum class UserType {
    DRIVER,
    RIDER
}

data class User(
    var id: String,
    var location: Location? = null,
    var type: UserType,
    var lastTripId: String? = null
)

@Table("user_event")
class UserEvent(user: User) {
    @PrimaryKey("uid")
    var uid: String = Date().time.toString()

    @Column("timestamp")
    @CassandraType(type = DataType.Name.LIST, typeArguments = [DataType.Name.TIMESTAMP])
    var timestamp: List<Long> = listOf(Date().time)

    @Column("user_uid")
    @CassandraType(type = DataType.Name.LIST, typeArguments = [DataType.Name.TEXT])
    val userUid: List<String> = listOf(user.id)

    @Column("user_type")
    @CassandraType(type = DataType.Name.LIST, typeArguments = [DataType.Name.TEXT])
    var userType: List<String> = listOf(user.type.name)

    @Column("last_trip_id")
    @CassandraType(type = DataType.Name.LIST, typeArguments = [DataType.Name.TEXT])
    var lastTripId: List<String> = listOf(user.lastTripId?:"")

    @Column("location")
    @CassandraType(type = DataType.Name.LIST, typeArguments = [DataType.Name.UDT], userTypeName = "geo_point")
    var location: List<Location?> = listOfNotNull(user.location)
}