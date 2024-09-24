#!/bin/bash

output_file="secrets.txt"

> "$output_file"

secrets=$(kubectl get secrets -o jsonpath='{.items[*].metadata.name}')

for secret in $secrets; do
    echo "Secret: $secret" >> "$output_file"
    
    keys=$(kubectl get secret "$secret" -o jsonpath='{.data}')

    for key in $(echo "$keys" | jq -r 'keys[]'); do
        value=$(kubectl get secret "$secret" -o jsonpath="{.data.$key}" | base64 --decode)
        echo "$key: $value" >> "$output_file"
    done
    
    echo "-----------------------------" >> "$output_file"
done

echo "++ $output_file ++."
