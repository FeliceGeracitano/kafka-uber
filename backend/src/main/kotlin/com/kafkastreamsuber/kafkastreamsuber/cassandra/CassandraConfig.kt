package com.kafkastreamsuber.kafkastreamsuber.cassandra

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean


abstract class CassandraConfig : AbstractCassandraConfiguration() {
    @Value("\${cassandra.contactpoints}")
    private val contactPoints: String? = null

    @Value("\${cassandra.port}")
    private val port = 0

    override fun getContactPoints(): String {
        return contactPoints!!
    }

    override fun getPort(): Int {
        return port
    }

    override fun cluster(): CassandraClusterFactoryBean {
        val cluster = super.cluster()
        cluster.setJmxReportingEnabled(false)
        return cluster
    }
}