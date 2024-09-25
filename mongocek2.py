from pymongo import MongoClient

# MongoDB connection string
connection_string = "mongodb://ridequiz-prod:dwuVKX05uEk1JSJcbeeh@mongo-htz-hel1-1.wvservices.com:27017/ridequiz-prod"

# Connect to MongoDB
client = MongoClient(connection_string)

# Select the database
db = client['ridequiz-prod']

# List all collection names
collection_names = db.list_collection_names()
print("Collections in the database:")
for name in collection_names:
    print(name)

# Close the connection
client.close()
