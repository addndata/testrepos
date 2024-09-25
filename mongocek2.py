from pymongo import MongoClient

# MongoDB connection string
connection_string = "mongodb://waves-enterprise-v2-prod:sza5VzRMCl3TMSWqavtA@mongo-htz-hel1-1.wvservices.com:27017/waves-enterprise-v2-prod"

# Connect to MongoDB
client = MongoClient(connection_string)

# Select the database
db = client['waves-enterprise-v2-prod']

# List all collection names
collection_names = db.list_collection_names()
print("Collections in the database:")
for name in collection_names:
    print(name)

# Close the connection
client.close()
