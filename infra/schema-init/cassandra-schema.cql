
DROP KEYSPACE IF EXISTS user_event;
DROP KEYSPACE IF EXISTS trip_event;

CREATE KEYSPACE user_event WITH replication = {'class': 'NetworkTopologyStrategy', 'DC1': 1} AND durable_writes = true;
CREATE KEYSPACE trip_event WITH replication = {'class': 'NetworkTopologyStrategy', 'DC1': 1} AND durable_writes = true;

CREATE TYPE user_event.geo_point (lat DOUBLE, lon DOUBLE);
CREATE TYPE trip_event.geo_point (lat DOUBLE, lon DOUBLE);

CREATE TABLE user_event.user_event (
  uid TEXT PRIMARY KEY,
  timestamp LIST<TIMESTAMP>,
  user_uid LIST<TEXT>,
  location LIST<FROZEN<GEO_POINT>>,
  user_type LIST<TEXT>,
  last_trip_id LIST<TEXT>
);

CREATE TABLE trip_event.trip_event (
  uid TEXT PRIMARY KEY,
  timestamp LIST<TIMESTAMP>,
  status LIST<TEXT>,
  driver_id LIST<TEXT>,
  rider_id LIST<TEXT>,
  from_location LIST<FROZEN<GEO_POINT>>,
  to_location LIST<FROZEN<GEO_POINT>>,
  amount LIST<DOUBLE>,
  distance LIST<DOUBLE>
);