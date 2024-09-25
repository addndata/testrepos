from pymongo import MongoClient

# MongoDB connection string
connection_string = "mongodb://ide-backend-prod:0uPvPTaE0KDG4ZS@mongo-htz-hel1-1.wvservices.com:27017/ide-backend-prod"

# Connect to MongoDB
client = MongoClient(connection_string)

# Select the database
db = client['ide-backend-prod']

# List all collection names
collection_names = db.list_collection_names()
print("Collections in the database:")
for name in collection_names:
    print(name)

# Close the connection
client.close()
