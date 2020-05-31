package com.kafkastreamsuber.kafkastreamsuber.cassandra

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean
import org.springframework.data.cassandra.config.CassandraCqlClusterFactoryBean
import org.springframework.data.cassandra.config.SchemaAction


abstract class CassandraConfig : AbstractCassandraConfiguration() {
    val geoPointType: String = "geo_point"

    @Value("\${cassandra.contactpoints}")
    private val contactPoints: String? = null


    @Value("\${cassandra.port}")
    private val port = 0

    override fun getContactPoints(): String {
        return contactPoints!!
    }

    override fun getSchemaAction(): SchemaAction {
        return SchemaAction.NONE
    }

    override fun getPort(): Int {
        return port
    }

    override fun cluster(): CassandraClusterFactoryBean {
        val cluster = CassandraCqlClusterFactoryBean()
        cluster.setJmxReportingEnabled(false)
        return cluster
    }
}