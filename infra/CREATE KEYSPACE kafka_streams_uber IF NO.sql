CREATE KEYSPACE kafka_streams_uber IF NOT EXISTS WITH REPLICATION = {
  'class': 'SimpleStrategy',
  'replication_factor': 1
} AND DURABLE_WRITES = true;