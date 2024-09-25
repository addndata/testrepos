#!/bin/bash

curl -X GET "https://api.cloudflare.com/client/v4/zones" \
-H "Host: api.cloudflare.com" \
-H "Sec-Ch-Ua: \"Chromium\";v=\"109\", \"Not_A Brand\";v=\"99\"" \
-H "Sec-Ch-Ua-Mobile: ?0" \
-H "Sec-Ch-Ua-Platform: \"Windows\"" \
-H "Upgrade-Insecure-Requests: 1" \
-H "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.5414.75 Safari/537.36" \
-H "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9" \
-H "Sec-Fetch-Site: none" \
-H "Authorization: Bearer yimFNXbGIo2YUE16w_0gysG-oYUezvFLLLEVAwTn" \
-H "Sec-Fetch-Mode: navigate" \
-H "Sec-Fetch-User: ?1" \
-H "Sec-Fetch-Dest: document" \
-H "Accept-Encoding: gzip, deflate" \
-H "Accept-Language: tr-TR,tr;q=0.9,en-US;q=0.8,en;q=0.7"
