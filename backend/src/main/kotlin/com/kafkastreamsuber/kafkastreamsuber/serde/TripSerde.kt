package org.jetbrains.kotlin.demo.serde

import com.kafkastreamsuber.kafkastreamsuber.models.Trip
import com.kafkastreamsuber.kafkastreamsuber.objectMapper
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer

class TripSerde : Serde<Trip> {
    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {}
    override fun close() {}
    override fun deserializer(): Deserializer<Trip> = TripDeserializer()
    override fun serializer(): Serializer<Trip> = TripSerializer()
}

class TripSerializer : Serializer<Trip> {
    override fun serialize(topic: String, data: Trip?): ByteArray? {
        if (data == null) return null
        return objectMapper.writeValueAsBytes(data)
    }
    override fun close() {}
    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {}
}

class TripDeserializer : Deserializer<Trip> {
    override fun deserialize(topic: String, data: ByteArray?): Trip? {
        if (data == null) return null
        return objectMapper.readValue(data, Trip::class.java)
    }
    override fun close() {}
    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {}
}