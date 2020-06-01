package com.kafkastreamsuber.kafkastreamsuber.cassandra.keyspace.user

import com.kafkastreamsuber.kafkastreamsuber.cassandra.CassandraConfig
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean
import org.springframework.data.cassandra.core.CassandraAdminOperations
import org.springframework.data.cassandra.core.CassandraAdminTemplate
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories
@Configuration
@EnableCassandraRepositories(cassandraTemplateRef = "UserCassandraTemplate")
class UserCassandraConfig : CassandraConfig() {
    private val keyspace: String = "user_event"

    override fun getKeyspaceName(): String {
        return keyspace
    }

    @Bean("UserSession")
    override fun session(): CassandraSessionFactoryBean {
        val session = super.session()
        session.setKeyspaceName(keyspaceName)
        return session
    }


    @Bean("UserCassandraTemplate")
    fun cassandraTemplate(@Qualifier("UserSession") session: CassandraSessionFactoryBean): CassandraAdminOperations? {
        return CassandraAdminTemplate(session.getObject(), cassandraConverter())
    }
}