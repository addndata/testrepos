const { MongoClient } = require('mongodb');

const url = 'mongodb://ridequiz-prod:dwuVKX05uEk1JSJcbeeh@mongo-htz-hel1-2.wvservices.com:27017/';
const dbName = 'ridequiz-prod';

async function main() {
    const client = new MongoClient(url);

    try {
        // Bağlantı aç
        await client.connect();
        console.log('Bağlantı sağlandı.');

        const db = client.db(dbName);
        const collection = db.collection('your-collection-name'); // Buraya veri çekmek istediğiniz koleksiyon adını yazın

        // Verileri çek
        const data = await collection.find({}).toArray();
        console.log('Veriler:', data);
    } catch (err) {
        console.error('Hata:', err);
    } finally {
        await client.close();
    }
}

main().catch(console.error);
