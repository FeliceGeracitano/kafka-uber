package org.jetbrains.kotlin.demo.kafka
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.state.HostInfo


class GlobalAppState private constructor() {
    var hostPortInfo: HostInfo? = null
    var kafkaStreams: KafkaStreams? = null

    fun hostPortInfo(host: String?, port: String): GlobalAppState {
        hostPortInfo = HostInfo(host, port.toInt())
        return this
    }

    fun streams(ks: KafkaStreams?): GlobalAppState {
        kafkaStreams = ks
        return this
    }

    companion object {
        val instance = GlobalAppState()
    }
}