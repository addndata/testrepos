from pyzabbix import ZabbixAPI

# Zabbix sunucu bilgilerini girin
ZABBIX_USER = 'ansible'
ZABBIX_PASS = 'kh2YtF5BFoQua9'
ZABBIX_SERVER = 'https://zabbix.wavesnodes.com/api_jsonrpc.php'

# Zabbix API'sine bağlanın
zapi = ZabbixAPI(ZABBIX_SERVER)
zapi.login(ZABBIX_USER, ZABBIX_PASS)
print(f"Connected to Zabbix API Version {zapi.api_version()}")

# Belirli bir host'tan veri çekmek için host_id'yi alın
host = zapi.host.get(filter={"host": ["your_host_name"]})
if host:
    host_id = host[0]['hostid']
    print(f"Host ID for {host[0]['name']} is {host_id}")

    # Hosttaki item'ları listeleme
    items = zapi.item.get(hostids=host_id, output=["itemid", "name", "lastvalue"], limit=5)
    for item in items:
        print(f"Item ID: {item['itemid']}, Name: {item['name']}, Last Value: {item['lastvalue']}")
else:
    print("Host bulunamadı")

# Zabbix API oturumunu kapatma
zapi.user.logout()
