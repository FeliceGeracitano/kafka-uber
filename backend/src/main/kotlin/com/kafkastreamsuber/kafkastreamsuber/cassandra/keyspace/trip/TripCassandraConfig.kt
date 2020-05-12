package com.kafkastreamsuber.kafkastreamsuber.cassandra.keyspace.trip

import com.kafkastreamsuber.kafkastreamsuber.cassandra.CassandraConfig
import org.springframework.context.annotation.Configuration
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories


//@Configuration
//@EnableCassandraRepositories(cassandraTemplateRef = "tripCassandraTemplate")
//class KeyspaceBCassandraConfig : CassandraConfig() {
//    private val keyspace: String = "trip_event"
//
//    override fun getKeyspaceName(): String {
//        return keyspace
//    }
//
//    @Bean("tripSession")
//    override fun session(): CassandraSessionFactoryBean {
//        val session = super.session()
//        session.setKeyspaceName(keyspaceName)
//        return session
//    }
//
//    @Bean("tripCassandraTemplate")
//    @Throws(Exception::class)
//    fun cassandraTemplate(
//        @Qualifier("tripSession") session: CassandraSessionFactoryBean
//    ): CassandraAdminOperations {
//        return CassandraAdminTemplate(session.getObject(), cassandraConverter())
//    }
//
//    override fun getStartupScripts(): List<String> {
//        val dropTripKeyspace = "DROP KEYSPACE IF EXISTS $keyspace;"
//        val createTripKeyspace =
//            "CREATE KEYSPACE IF NOT EXISTS $keyspace" +
//                    "  WITH replication = {'class': 'NetworkTopologyStrategy', 'DC1': 1} AND durable_writes = true;"
//
//        return listOf(
//            dropTripKeyspace,
//            createTripKeyspace
//        )
//    }
//}

@Configuration
@EnableCassandraRepositories
class TripCassandraConfig : CassandraConfig() {
    private val keyspace: String = "trip_event"
    private val geoPointType: String = "geo_point"

    override fun getKeyspaceName(): String {
        return keyspace
    }

    override fun getStartupScripts(): List<String> {
        val dropTripKeyspace = "DROP KEYSPACE IF EXISTS $keyspace;"
        val createTripKeyspace =
            "CREATE KEYSPACE IF NOT EXISTS $keyspace" +
                    "  WITH replication = {'class': 'NetworkTopologyStrategy', 'DC1': 1} AND durable_writes = true;"
        val createGeoPoint = "CREATE TYPE IF NOT EXISTS ${keyspace}.${geoPointType} (lat DOUBLE, lon DOUBLE);"
        val createTable = "CREATE TABLE $keyspace.$keyspace( " +
                "  uid TEXT PRIMARY KEY," +
                "  timestamp LIST<TIMESTAMP>," +
                "  status LIST<TEXT>," +
                "  driver_id LIST<TEXT>," +
                "  rider_id LIST<TEXT>," +
                "  from_location LIST<FROZEN<GEO_POINT>>," +
                "  to_location LIST<FROZEN<GEO_POINT>>" +
                ");";

        return listOf(
            dropTripKeyspace,
            createTripKeyspace,
            createGeoPoint,
            createTable
        )
    }
}
