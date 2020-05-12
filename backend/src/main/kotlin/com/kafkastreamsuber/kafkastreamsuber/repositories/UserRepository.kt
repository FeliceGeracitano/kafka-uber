package com.kafkastreamsuber.kafkastreamsuber.repositories


import com.kafkastreamsuber.kafkastreamsuber.models.UserEvent
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository


@Repository
interface UserRepository: CassandraRepository<UserEvent, String>