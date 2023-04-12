#!/bin/bash

# Oracle veritabanı bağlantı bilgileri
db_username="paybito_db"
db_password="pAYb1t0\$20#tRAde"
db_host="ec2-13-52-131-42.us-west-1.compute.amazonaws.com"
db_port="1521"
db_name="paybito"

# SQL*Plus komutu ile Oracle veritabanına bağlanma
output=$(sqlplus ${db_username}/${db_password}@${db_host}:${db_port}/${db_name} 2>&1)

# Bağlantı başarılı mesajı gösterme ve log dosyasına yazdırma
if [ $? -eq 0 ]
then
    echo "Oracle veritabanına başarıyla bağlandınız."
    echo "Oracle veritabanına başarıyla bağlandınız." >> log.txt
else
    echo "Oracle veritabanına bağlanırken bir hata oluştu: $output"
    echo "Oracle veritabanına bağlanırken bir hata oluştu: $output" >> log.txt
fi
