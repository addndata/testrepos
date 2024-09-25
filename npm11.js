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

            const output = fs.createWriteStream(`${tableName}_dump.csv`);

            // COPY komutunu kullan
            const copyQuery = `COPY ${tableName} TO STDOUT WITH CSV`;

            const copyStream = client.query(copyQuery);

            // Akışa yönlendirme
            copyStream.pipe(output);

            // Akış sona erdiğinde
            output.on('finish', () => {
                console.log(`${tableName}_dump.csv dosyası oluşturuldu.`);
            });

            // Hata dinleyicileri
            output.on('error', (err) => {
                console.error(`Output hatası: ${err}`);
            });

            copyStream.on('error', (err) => {
                console.error(`Hata: ${err}`);
            });

            // Her bir copyStream işlemi için bekleme
            await new Promise((resolve, reject) => {
                copyStream.on('end', resolve);
                copyStream.on('error', reject);
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
