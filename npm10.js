const { Client } = require('pg');
const fs = require('fs');

const client = new Client({
    user: 'prod',
    host: 'postgresql-keeper.wvservices.com',
    database: 'prod',
    password: 'fsojf208efisoefgseg',
    port: 5432,
});

async function dumpAllRowsFromAllTables() {
    try {
        await client.connect();
        console.log('PostgreSQL veritabanına bağlandı.');

        // Tüm tabloları almak için sorgu
        const tablesResult = await client.query(`
            SELECT table_name
            FROM information_schema.tables
            WHERE table_schema = 'public'
        `);

        // Her bir tablo için tüm satırları dump et
        for (const row of tablesResult.rows) {
            const tableName = row.table_name;
            console.log(`Tablo: ${tableName} için dump alınıyor...`);

            // COPY komutunu kullan
            const output = fs.createWriteStream(`${tableName}_dump.csv`);
            const query = `COPY ${tableName} TO STDOUT WITH CSV`;

            // COPY komutunu çalıştır
            const copyStream = client.query(query);
            copyStream.on('end', () => {
                console.log(`${tableName}_dump.csv dosyası oluşturuldu.`);
            });

            // Akışa yönlendirme
            copyStream.pipe(output);

            // Stream hatalarını dinle
            output.on('error', (err) => {
                console.error(`Output hatası: ${err}`);
            });

            copyStream.on('error', (err) => {
                console.error(`Hata: ${err}`);
            });
        }

    } catch (err) {
        console.error('Bağlantı hatası:', err);
    } finally {
        await client.end();
        console.log('Veritabanı bağlantısı kapatıldı.');
    }
}

dumpAllRowsFromAllTables();
