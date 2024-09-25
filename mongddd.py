from pymongo import MongoClient
import json
from bson import ObjectId

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

# Specify output file path
output_file_path = 'sharedfiles_data.txt'

# Function to convert MongoDB documents to serializable format
def serialize_document(document):
    document['_id'] = str(document['_id'])  # Convert ObjectId to string
    return document

# Write data to a text file
with open(output_file_path, 'w') as output_file:
    for document in data:
        serialized_document = serialize_document(document)
        output_file.write(json.dumps(serialized_document) + '\n')  # Write each document as a JSON string

print(f"Data from 'sharedfiles' collection has been saved to {output_file_path}.")

# Close the connection
client.close()
