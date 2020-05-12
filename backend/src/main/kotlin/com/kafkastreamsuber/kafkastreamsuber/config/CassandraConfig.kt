package com.kafkastreamsuber.kafkastreamsuber.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean
import org.springframework.data.cassandra.config.SchemaAction
import org.springframework.data.cassandra.core.convert.CassandraConverter
import org.springframework.data.cassandra.core.convert.MappingCassandraConverter
import org.springframework.data.cassandra.core.mapping.BasicCassandraMappingContext
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext
import org.springframework.data.cassandra.core.mapping.SimpleUserTypeResolver
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories


@Configuration
@EnableCassandraRepositories
class CassandraConfig : AbstractCassandraConfiguration() {

    private val userKeySpaceName = "kafka_uber_user_event"
    private val tripKeySpaceName = "kafka_uber_trip_event"
    private val geoPoint = "GEO_POINT"

    override fun getKeyspaceName(): String {
        return keyspaceName
    }

//    @Bean
//    @Throws(Exception::class)
//    override fun session(): CassandraSessionFactoryBean {
//        val session = CassandraSessionFactoryBean()
//        session.setCluster(cluster().getObject())
//        session.setKeyspaceName(keyspaceName)
//        converter<Any, Any>()?.let { session.setConverter(it) }
//        session.schemaAction = SchemaAction.RECREATE
//        return session
//    }

    @Bean
    @Throws(java.lang.Exception::class)
    override fun getSchemaAction(): SchemaAction {
        return SchemaAction.RECREATE
    }

//    @Bean
//    @Throws(java.lang.Exception::class)
//    fun <T, U> converter(): CassandraConverter? {
//        return mappingContext()?.let { MappingCassandraConverter(it) }
//    }

//    @Bean
//    @Throws(java.lang.Exception::class)
//    fun mappingContext(): CassandraMappingContext? {
//        val mappingContext = BasicCassandraMappingContext()
//        mappingContext.setUserTypeResolver(SimpleUserTypeResolver(cluster().getObject(), keyspaceName))
//        return mappingContext
//    }

    override fun cluster(): CassandraClusterFactoryBean {
        val cluster = super.cluster()
        cluster.setJmxReportingEnabled(false)
        return cluster
    }

    override fun getStartupScripts(): List<String> {

        val keySpaces = arrayOf(userKeySpaceName, tripKeySpaceName)


//        val createUserKeySpaces =
//            "CREATE KEYSPACE IF NOT EXISTS ${userKeySpaceName} " +
//                    "  WITH replication = {'class': 'NetworkTopologyStrategy', 'DC1': 1} AND durable_writes = true;"
//
//        val createTripKeySpaces =
//            "CREATE KEYSPACE IF NOT EXISTS ${tripKeySpaceName}" +
//                    "  WITH replication = {'class': 'NetworkTopologyStrategy', 'DC1': 1} AND durable_writes = true;"

//        val dropTables = "DROP TABLE IF EXISTS ${userKeySpaceName}.doc;" +
//                "DROP TABLE IF EXISTS ${tripKeySpaceName}.doc;"

//        val dropGeoPointType = "DROP TYPE IF EXISTS ${userKeySpaceName}.${geoPoint}; " +
//                "DROP TYPE IF EXISTS ${tripKeySpaceName}.${geoPoint};"

//        val createGeoPointTypes = "CREATE TYPE IF NOT EXISTS ${userKeySpaceName}.${geoPoint} (lat DOUBLE, lon DOUBLE);" +
//                "CREATE TYPE IF NOT EXISTS ${tripKeySpaceName}.${geoPoint} (lat DOUBLE, lon DOUBLE);"

        val createUserTable = "CREATE TABLE ${userKeySpaceName}.doc( " +
                "  uid TEXT PRIMARY KEY," +
                "  timestamp LIST<TIMESTAMP>," +
                "  user_uid LIST<TEXT>," +
                "  location LIST<FROZEN<GEO_POINT>>," +
                "  user_type LIST<TEXT>," +
                "  last_trip_id LIST<TEXT>" +
                ");";

        val createTripEventTable = "CREATE TABLE ${tripKeySpaceName}.doc( " +
                "  uid TEXT PRIMARY KEY," +
                "  timestamp LIST<TIMESTAMP>," +
                "  status LIST<TEXT>," +
                "  driverId LIST<TEXT>," +
                "  riderId LIST<TEXT>," +
                "  fromLocation LIST<FROZEN<GEO_POINT>>," +
                "  toLocation LIST<FROZEN<GEO_POINT>>" +
                ");";


        return  keySpaces.map { it -> "DROP KEYSPACE IF EXISTS ${it}; " } +
                keySpaces.map { it ->
                    "CREATE KEYSPACE IF NOT EXISTS ${it} WITH replication = {'class': 'NetworkTopologyStrategy', 'DC1': 1} AND durable_writes = true;"
                } +
                keySpaces.map { it -> "CREATE TYPE IF NOT EXISTS ${it}.${geoPoint} (lat DOUBLE, lon DOUBLE);" } +
                createUserTable +
                createTripEventTable

    }
}