import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.state.HostInfo
import org.jetbrains.kotlin.demo.*


class GlobalAppState private constructor() {
    val users = mutableMapOf<String, User>()
    val trip = mutableMapOf<String, Trip>()

    companion object {
        val instance = GlobalAppState()
    }
}