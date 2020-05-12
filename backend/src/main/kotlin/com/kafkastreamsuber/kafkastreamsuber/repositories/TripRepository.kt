package com.kafkastreamsuber.kafkastreamsuber.repositories


import com.kafkastreamsuber.kafkastreamsuber.models.TripEvent
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository


@Repository
interface TripRepository: CassandraRepository<TripEvent, String>
