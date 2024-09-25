from pymongo import MongoClient

# MongoDB connection string
connection_string = "mongodb://ide-backend-prod:0uPvPTaE0KDG4ZS@mongo-htz-hel1-1.wvservices.com:27017/ide-backend-prod"

# Connect to MongoDB
client = MongoClient(connection_string)

# Select the database
db = client['ide-backend-prod']

# Select the collection
collection = db['sharedfiles']

# Fetch data
data = collection.find()  # Fetches all documents

# Print fetched data
for document in data:
    print(document)

# Close the connection
client.close()
