package com.kafkastreamsuber.kafkastreamsuber.cassandra.keyspace.trip

import com.kafkastreamsuber.kafkastreamsuber.cassandra.CassandraConfig
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean
import org.springframework.data.cassandra.core.CassandraAdminOperations
import org.springframework.data.cassandra.core.CassandraAdminTemplate
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories

@Configuration
@EnableCassandraRepositories(cassandraTemplateRef = "TripCassandraTemplate")
class TripCassandraConfig : CassandraConfig() {
    private val keyspace: String = "trip_event"

    override fun getKeyspaceName(): String {
        return keyspace
    }

    @Bean("TripSession")
    override fun session(): CassandraSessionFactoryBean {
        val session = super.session()
        session.setKeyspaceName(keyspaceName)
        return session
    }

    @Bean("TripCassandraTemplate")
    fun cassandraTemplate(@Qualifier("TripSession") session: CassandraSessionFactoryBean): CassandraAdminOperations? {
        return CassandraAdminTemplate(session.getObject(), cassandraConverter())
    }
}
