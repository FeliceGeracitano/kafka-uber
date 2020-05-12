package org.jetbrains.kotlin.demo.serde


import com.kafkastreamsuber.kafkastreamsuber.JsonParser
import com.kafkastreamsuber.kafkastreamsuber.cassandra.User
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer

class UserSerde : Serde<User> {
    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {}
    override fun close() {}
    override fun deserializer(): Deserializer<User> = UserDeserializer()
    override fun serializer(): Serializer<User> = UserSerializer()
}

class UserSerializer : Serializer<User> {
    override fun serialize(topic: String, data: User?): ByteArray? {
        if (data == null) return null
        return JsonParser.writeValueAsBytes(data)
    }
    override fun close() {}
    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {}
}

class UserDeserializer : Deserializer<User> {
    override fun deserialize(topic: String, data: ByteArray?): User? {
        if (data == null) return null
        return JsonParser.readValue(data, User::class.java)
    }
    override fun close() {}
    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {}
}