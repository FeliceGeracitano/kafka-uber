CREATE KEYSPACE IF NOT EXISTS kafka_uber
WITH replication = {'class': 'NetworkTopologyStrategy', 'DC1': '1'} AND durable_writes = true;