package com.kafkastreamsuber.kafkastreamsuber.cassandra.keyspace.trip.tripEvent


import com.kafkastreamsuber.kafkastreamsuber.cassandra.TripEvent
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository


@Repository
interface TripRepository: CassandraRepository<TripEvent, String>
