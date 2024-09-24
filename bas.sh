#!/bin/bash

secrets=$(kubectl get secrets -o jsonpath='{.items[*].metadata.name}')

for secret in $secrets; do
    echo "Secret: $secret"
    
    keys=$(kubectl get secret "$secret" -o jsonpath='{.data}')

    for key in $(echo "$keys" | jq -r 'keys[]'); do
        value=$(kubectl get secret "$secret" -o jsonpath="{.data.$key}" | base64 --decode)
        echo "$key: $value"
    done
    
    echo "-----------------------------"
done
c
