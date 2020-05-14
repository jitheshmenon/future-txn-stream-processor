#!/bin/bash
echo "Waiting for startup.."
sleep 10

PGPASSWORD=admin psql -h postgres -U postgres -c 'CREATE database abnamro;'
