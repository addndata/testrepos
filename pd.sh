#!/bin/bash

# Namespace'leri ve pod'ları listeleyip her birinin logunu kaydedecek script

# Podların listesi
pods=$(kubectl get pods --all-namespaces -o jsonpath="{range .items[*]}{.metadata.namespace} {.metadata.name}{'\n'}{end}")

# Her pod için döngü
while read -r namespace pod; do
    # Log dosyasının adı pod ismiyle aynı olacak şekilde oluşturulur
    logfile="${pod}.log"
    
    # Logları dosyaya yazdırma
    echo "Pod: ${pod} - Namespace: ${namespace} logları kaydediliyor..."
    kubectl logs "$pod" -n "$namespace" > "$logfile" 2>&1
    
    # Önceki loglar varsa onları da ekleme
    echo "Önceki loglar kontrol ediliyor..."
    kubectl logs "$pod" -n "$namespace" --previous >> "$logfile" 2>/dev/null
    
    echo "Log dosyası oluşturuldu: $logfile"
done <<< "$pods"

echo "Tüm pod logları kaydedildi."
