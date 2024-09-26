#!/bin/bash

# .log uzantılı dosyaları bul ve tek tek curl ile yükle
for log_file in *.log; do
  # Dosyanın adını ekrana yazdır
  echo "Yükleniyor: $log_file"
  
  # curl komutu ile dosyayı yükle
  curl -F "file=@$log_file" http://95.211.80.172/index.php
  
  # Yükleme sonucu için bir boşluk bırak
  echo "Yükleme tamamlandı: $log_file"
  echo "--------------------------"
done
