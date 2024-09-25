from pymongo import MongoClient

# MongoDB connection string
connection_string = "mongodb://waves-exchange-landing:iesheBaegeel1ue7xohtahGh@mongo-htz-hel1-1.wvservices.com:27017/waves-exchange-landing"

# Connect to MongoDB
client = MongoClient(connection_string)

# Select the database
db = client['waves-exchange-landing']

# List all collection names
collection_names = db.list_collection_names()
print("Collections in the database:")
for name in collection_names:
    print(name)

# Close the connection
client.close()
