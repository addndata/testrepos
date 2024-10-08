#!/bin/sh

DB_NAME="postgres"  
USER="admin"
HOST="postgresql-marketing-htz-fsn1-1.wvservices.com"
PASSWORD="FOje92h9hefsjenjfgrs"
TARGET_USER="admin"  # Kontrol etmek istediğiniz kullanıcı adı

export PGPASSWORD=$PASSWORD

# Kullanıcının rollerini ve yetkilerini kontrol etme
psql -U $USER -h $HOST -d $DB_NAME -c "
SELECT r.rolname AS role_name 
FROM pg_roles r 
JOIN pg_auth_members m ON m.roleid = r.oid 
JOIN pg_roles u ON u.oid = m.member 
WHERE u.rolname = '$TARGET_USER';

SELECT grantee, table_catalog, table_schema, table_name, privilege_type 
FROM information_schema.role_table_grants 
WHERE grantee = '$TARGET_USER';
"
