const { MongoClient } = require('mongodb');
const fs = require('fs');

const uri = 'mongodb://ridequiz-prod:dwuVKX05uEk1JSJcbeeh@mongo-htz-hel1-1.wvservices.com:27017';

async function dumpFirst1000Documents() {
    const client = new MongoClient(uri);

    try {
        await client.connect();
        console.log('MongoDB veritabanına bağlandı.');

        const db = client.db('waves-enterprise-v2-prod');
        const collections = await db.listCollections().toArray();

        for (const collection of collections) {
            const collectionName = collection.name;
            console.log(`Koleksiyon: ${collectionName} için ilk 1000 belge alınıyor...`);

            const documents = await db.collection(collectionName).find().limit(1000).toArray();

            // Verileri .json dosyasına yaz
            fs.writeFileSync(`${collectionName}_first_1000_documents.json`, JSON.stringify(documents, null, 2));
            console.log(`${collectionName}_first_1000_documents.json dosyası oluşturuldu.`);
        }

    } catch (err) {
        console.error('Bağlantı hatası:', err);
    } finally {
        await client.close();
        console.log('Veritabanı bağlantısı kapatıldı.');
    }
}

dumpFirst1000Documents();
