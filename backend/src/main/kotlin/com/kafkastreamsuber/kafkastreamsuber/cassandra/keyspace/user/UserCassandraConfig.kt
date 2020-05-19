package com.kafkastreamsuber.kafkastreamsuber.cassandra.keyspace.user

import com.kafkastreamsuber.kafkastreamsuber.cassandra.CassandraConfig
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean
import org.springframework.data.cassandra.core.CassandraAdminOperations
import org.springframework.data.cassandra.core.CassandraAdminTemplate
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification
import org.springframework.data.cassandra.core.cql.keyspace.DataCenterReplication
import org.springframework.data.cassandra.core.cql.keyspace.KeyspaceOption
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import javax.annotation.PostConstruct


@Configuration
@ComponentScan
@EnableCassandraRepositories(cassandraTemplateRef = "UserCassandraTemplate")
class UserCassandraConfig : CassandraConfig() {
    private val keyspace: String = "user_event"
    private val geoPointType: String = "geo_point"

    override fun getKeyspaceName(): String {
        return keyspace
    }

    override fun getContactPoints(): String {
        return "127.0.0.1"
    }

    @Bean("UserSession")
    override fun session(): CassandraSessionFactoryBean {
        return super.session()
    }

    @Bean("UserCassandraTemplate")
    fun cassandraTemplate(
        @Qualifier("UserSession") session: CassandraSessionFactoryBean
    ): CassandraAdminOperations {
        return CassandraAdminTemplate(session.getObject(), cassandraConverter())
    }

    @Bean("userCluster")
    override fun cluster(): CassandraClusterFactoryBean {
        val cluster = super.cluster()
        cluster.keyspaceCreations = keyspaceCreations;
        cluster.setJmxReportingEnabled(false)
        return cluster
    }

    @Bean("userGetKeyspaceCreations")
    override fun getKeyspaceCreations(): List<CreateKeyspaceSpecification> {
        return listOf(
            CreateKeyspaceSpecification
                .createKeyspace(keyspace).ifNotExists()
                .with(KeyspaceOption.DURABLE_WRITES, true).withNetworkReplication(
                    DataCenterReplication.of("DC1", 1)
                )
        )
    }

    override fun getStartupScripts(): List<String> {
        val createGeoPoint = "CREATE TYPE IF NOT EXISTS ${keyspace}.${geoPointType} (lat DOUBLE, lon DOUBLE);"
        val createTable = "CREATE TABLE IF NOT EXISTS $keyspace.$keyspace ( " +
                "  uid TEXT PRIMARY KEY," +
                "  timestamp LIST<TIMESTAMP>," +
                "  user_uid LIST<TEXT>," +
                "  location LIST<FROZEN<GEO_POINT>>," +
                "  user_type LIST<TEXT>," +
                "  last_trip_id LIST<TEXT>" +
                ");";
        return listOf(createGeoPoint, createTable)
    }

    @PostConstruct
    private fun initUserIndexes() {
        val elasticUrl = "http://localhost:9200/$keyspace"
        val kibanaUrl = "http://localhost:9200/.kibana/doc/index-pattern:$keyspace"
        val elasticIndex = "{\n" +
                "  \"mappings\": {\n" +
                "    \"trip_event\": {\n" +
                "      \"discover\": \"^((?!location).)*\$\",\n" +
                "      \"properties\": {\n" +
                "        \"from_location\": {\n" +
                "          \"type\": \"geo_point\"\n" +
                "        },\n" +
                "        \"to_location\": {\n" +
                "          \"type\": \"geo_point\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"
        val kibanaIndex = "{\n" +
                " \"type\" : \"index-pattern\",\n" +
                " \"index-pattern\" : {\n" +
                " \"title\": \"$keyspace*\",\n" +
                " \"timeFieldName\": \"timestamp\"\n" +
                " }\n" +
                "}"
        val restTemplate = RestTemplate()
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        try {
            restTemplate.put(elasticUrl, HttpEntity<String>(elasticIndex, headers))
            restTemplate.postForEntity(kibanaUrl, HttpEntity<String>(kibanaIndex, headers), String.javaClass)
        } catch (e: Exception) {
            println(e)
        }
    }
}