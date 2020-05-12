package com.kafkastreamsuber.kafkastreamsuber.cassandra.keyspace.user.userEvent


import com.kafkastreamsuber.kafkastreamsuber.cassandra.UserEvent
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository


@Repository
interface UserRepository: CassandraRepository<UserEvent, String>