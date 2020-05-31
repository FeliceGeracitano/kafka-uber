#!/bin/bash
set -e

# Helpers
function wait() {
  until [[ $(curl "$1/_cat/health?h=status" --silent) == 'green' ]]; do
    echo "Waiting for "$2"..."
    sleep 5
  done
}

# check elassandra is up
wait "$ELASTIC_URL" "elastic"

# Cassandra schema
cqlsh --cqlversion=3.4.4 $CASSANDRA_HOST -f /tmp/cassandra-schema.cql

# Elastics Indexes
curl -XPUT "$ELASTIC_URL/user_event" -H 'Content-Type: application/json' -d'
{
  "mappings": {
    "user_event": {
      "discover": "^((?!location).*)",
      "properties": {
        "location": {
          "type": "geo_point"
        }
      }
    }
  }
}'
curl -XPUT "$ELASTIC_URL/trip_event" -H 'Content-Type: application/json' -d'
{
  "mappings": {
    "trip_event": {
      "discover": "^((?!location).)*$",
      "properties": {
        "from_location": {
          "type": "geo_point"
        },
        "to_location": {
          "type": "geo_point"
        }
      }
    }
  }
}'

# NB: this does not work if indexes are empty

# Kibana Index Mapping
# curl -XPOST "$ELASTIC_URL/.kibana/doc/index-pattern:trip_event" -H 'Content-Type: application/json' -d'
# {
# "type" : "index-pattern",
#  "index-pattern" : {
#  "title": "trip_event*",
#  "timeFieldName": "timestamp"
#  }
# }'
# curl -XPOST "$ELASTIC_URL/.kibana/doc/index-pattern:trip_event" -H 'Content-Type: application/json' -d'
# {
# "type" : "index-pattern",
#  "index-pattern" : {
#  "title": "trip_event*",
#  "timeFieldName": "timestamp"
#  }
# }'

echo "Done."
