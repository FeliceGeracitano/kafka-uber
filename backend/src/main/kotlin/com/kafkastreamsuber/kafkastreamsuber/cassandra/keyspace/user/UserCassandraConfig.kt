package com.kafkastreamsuber.kafkastreamsuber.cassandra.keyspace.user

import com.kafkastreamsuber.kafkastreamsuber.cassandra.CassandraConfig
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean
import org.springframework.data.cassandra.core.CassandraAdminOperations
import org.springframework.data.cassandra.core.CassandraAdminTemplate
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories


//@Configuration
//@EnableCassandraRepositories
//class CassandraConfig : AbstractCassandraConfiguration() {
//
//    private val keyspaceName = "kafka_uber_user_event";
//    private val userKeySpaceName = "user_event"
//    private val tripKeySpaceName = "trip_event"
//    private val geoPoint = "GEO_POINT"
//
//    override fun getKeyspaceName(): String {
//        return userKeySpaceName
//    }
//
////    @Bean
////    @Throws(Exception::class)
////    override fun session(): CassandraSessionFactoryBean {
////        val session = CassandraSessionFactoryBean()
////        session.setCluster(cluster().getObject())
////        session.setKeyspaceName(keyspaceName)
////        converter<Any, Any>()?.let { session.setConverter(it) }
////        session.schemaAction = SchemaAction.RECREATE
////        return session
////    }
//
////    @Bean
////    @Throws(java.lang.Exception::class)
////    override fun getSchemaAction(): SchemaAction {
////        return SchemaAction.RECREATE
////    }
//
////    @Bean
////    @Throws(java.lang.Exception::class)
////    fun <T, U> converter(): CassandraConverter? {
////        return mappingContext()?.let { MappingCassandraConverter(it) }
////    }
//
////    @Bean
////    @Throws(java.lang.Exception::class)
////    fun mappingContext(): CassandraMappingContext? {
////        val mappingContext = BasicCassandraMappingContext()
////        mappingContext.setUserTypeResolver(SimpleUserTypeResolver(cluster().getObject(), keyspaceName))
////        return mappingContext
////    }
//
//    override fun cluster(): CassandraClusterFactoryBean {
//        val cluster = super.cluster()
//        cluster.setJmxReportingEnabled(false)
//        println("startupScripts: " + cluster.startupScripts)
//        println("keyspaceActions: " + cluster.keyspaceActions)
//        println("keyspaceSpecifications: " + cluster.keyspaceSpecifications)
//        return cluster
//    }
//
//    override fun getStartupScripts(): List<String> {
//
//        val keySpaces = arrayOf(userKeySpaceName, tripKeySpaceName)
//
//        val dropUserKeyspace = "DROP KEYSPACE IF EXISTS $userKeySpaceName;"
//        val dropTripKeyspace = "DROP KEYSPACE IF EXISTS $tripKeySpaceName;"
//
//        val createUserKeyspace =
//            "CREATE KEYSPACE IF NOT EXISTS $userKeySpaceName " +
//                    "  WITH replication = {'class': 'NetworkTopologyStrategy', 'DC1': 1} AND durable_writes = true;"
//
//        val createTripKeyspace =
//            "CREATE KEYSPACE IF NOT EXISTS $tripKeySpaceName" +
//                    "  WITH replication = {'class': 'NetworkTopologyStrategy', 'DC1': 1} AND durable_writes = true;"
//
//        val createGeoPointUser = "CREATE TYPE IF NOT EXISTS ${userKeySpaceName}.${geoPoint} (lat DOUBLE, lon DOUBLE);"
//        val createGeoPointTrip = "CREATE TYPE IF NOT EXISTS ${tripKeySpaceName}.${geoPoint} (lat DOUBLE, lon DOUBLE);"
//
//        val createUserEventTable = "CREATE TABLE $userKeySpaceName.$userKeySpaceName ( " +
//                "  uid TEXT PRIMARY KEY," +
//                "  timestamp LIST<TIMESTAMP>," +
//                "  user_uid LIST<TEXT>," +
//                "  location LIST<FROZEN<GEO_POINT>>," +
//                "  user_type LIST<TEXT>," +
//                "  last_trip_id LIST<TEXT>" +
//                ");";
//
//        val createTripEventTable = "CREATE TABLE $tripKeySpaceName.$tripKeySpaceName( " +
//                "  uid TEXT PRIMARY KEY," +
//                "  timestamp LIST<TIMESTAMP>," +
//                "  status LIST<TEXT>," +
//                "  driverId LIST<TEXT>," +
//                "  riderId LIST<TEXT>," +
//                "  fromLocation LIST<FROZEN<GEO_POINT>>," +
//                "  toLocation LIST<FROZEN<GEO_POINT>>" +
//                ");";
//
//
//        return mutableListOf(
//            dropUserKeyspace,
//            //dropTripKeyspace,
//            createUserKeyspace,
//            //createTripKeyspace,
//            createGeoPointUser,
//            //createGeoPointTrip,
//            createUserEventTable
//            //createTripEventTable
//        )
//    }
//
//    override fun getKeyspaceCreations(): MutableList<CreateKeyspaceSpecification> {
//
//
//        return mutableListOf(CreateKeyspaceSpecification.createKeyspace("userKeySpaceName").withNetworkReplication())
//    }
//}

@Configuration
@EnableCassandraRepositories(cassandraTemplateRef = "UserCassandraTemplate")
class UserCassandraConfig : CassandraConfig() {
    private val keyspace: String? = "user_event"
    private val geoPointType: String = "geo_point"

    override fun getKeyspaceName(): String {
        return keyspace!!
    }

    @Bean("UserSession")
    override fun session(): CassandraSessionFactoryBean {
        return super.session()
    }

    @Bean("UserCassandraTemplate")
    @Throws(Exception::class)
    fun cassandraTemplate(
        @Qualifier("UserSession") session: CassandraSessionFactoryBean
    ): CassandraAdminOperations {
        return CassandraAdminTemplate(session.getObject(), cassandraConverter())
    }

    override fun getStartupScripts(): List<String> {
        val dropKeyspace = "DROP KEYSPACE IF EXISTS $keyspace;"
        val createKeyspace =
            "CREATE KEYSPACE IF NOT EXISTS $keyspace" +
                    "  WITH replication = {'class': 'NetworkTopologyStrategy', 'DC1': 1} AND durable_writes = true;"
        val createGeoPoint = "CREATE TYPE IF NOT EXISTS ${keyspace}.${geoPointType} (lat DOUBLE, lon DOUBLE);"
        val createTable = "CREATE TABLE $keyspace.$keyspace ( " +
                "  uid TEXT PRIMARY KEY," +
                "  timestamp LIST<TIMESTAMP>," +
                "  user_uid LIST<TEXT>," +
                "  location LIST<FROZEN<GEO_POINT>>," +
                "  user_type LIST<TEXT>," +
                "  last_trip_id LIST<TEXT>" +
                ");";

        return listOf(
            dropKeyspace,
            createKeyspace,
            createGeoPoint,
            createTable
        )
    }
}