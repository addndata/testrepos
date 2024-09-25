from pymongo import MongoClient

# MongoDB connection string
connection_string = "mongodb://ide-backend-stage:Kd3F278zNXaq7ZV@mongo-htz-hel1-1.wvservices.com:27017/ide-backend-stage"

# Connect to MongoDB
client = MongoClient(connection_string)

# Select the database
db = client['ide-backend-stage']

# List all collection names
collection_names = db.list_collection_names()
print("Collections in the database:")
for name in collection_names:
    print(name)

# Close the connection
client.close()
