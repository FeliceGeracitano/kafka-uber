FROM debian:stretch
RUN apt-get update && apt-get install -y curl && apt install -y python-pip && pip install cqlsh
COPY docker-entrypoint.sh /usr/local/bin
COPY cassandra-schema.cql /tmp
ENTRYPOINT ["docker-entrypoint.sh"]
