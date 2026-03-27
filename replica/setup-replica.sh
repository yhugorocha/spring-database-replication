#!/bin/bash
set -e

until pg_isready -h pg-primary -p 5432 -U app; do
  echo "Aguardando primary..."
  sleep 2
done

rm -rf /var/lib/postgresql/data/*

export PGPASSWORD='replica123'
pg_basebackup -h pg-primary -D /var/lib/postgresql/data -U replicator -Fp -Xs -P -R

echo "hot_standby = on" >> /var/lib/postgresql/data/postgresql.auto.conf

chown -R postgres:postgres /var/lib/postgresql/data
chmod 700 /var/lib/postgresql/data

exec su - postgres -c "/usr/lib/postgresql/16/bin/postgres -D /var/lib/postgresql/data"