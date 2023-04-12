#!/bin/bash

# Oracle veritabanı bağlantı bilgileri
db_username="paybito_db"
db_password="pAYb1t0\$20#tRAde"
db_host="ec2-13-52-131-42.us-west-1.compute.amazonaws.com"
db_port="1521"
db_name="paybito"

# SQL*Plus komutu ile Oracle veritabanına bağlanma
sqlplus ${db_username}/${db_password}@${db_host}:${db_port}/${db_name}
