const { MongoClient } = require('mongodb');

const url = 'mongodb://ridequiz-prod:dwuVKX05uEk1JSJcbeeh@mongo-htz-hel1-2.wvservices.com:27017/';
const dbName = 'ridequiz-prod';

async function main() {
    const client = new MongoClient(url);

    try {
        await client.connect();
        console.log('Bağlantı sağlandı.');

        const db = client.db(dbName);

        // Koleksiyonları listele
        const collections = await db.listCollections().toArray();
        console.log('Koleksiyonlar:', collections.map(col => col.name));
    } catch (err) {
        console.error('Hata:', err);
    } finally {
        await client.close();
    }
}

main().catch(console.error);
