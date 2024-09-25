const { Client } = require('pg');
const fs = require('fs');

const client = new Client({
    user: 'prod',
    host: 'postgresql-keeper.wvservices.com',
    database: 'prod', // Burayı ihtiyacınıza göre güncelleyin
    password: 'fsojf208efisoefgseg',
    port: 5432,
});

async function dumpFirst1000RowsFromAllTables() {
    try {
        await client.connect();
        console.log('PostgreSQL veritabanına bağlandı.');

        // Tüm tabloları almak için sorgu
        const tablesResult = await client.query(`
            SELECT table_name
            FROM information_schema.tables
            WHERE table_schema = 'public'
        `);

        // Her bir tablo için ilk 1000 satırı dump et
        for (const row of tablesResult.rows) {
            const tableName = row.table_name;
            console.log(`Tablo: ${tableName} için dump alınıyor...`);

            const query = `COPY (SELECT * FROM ${tableName} LIMIT 1000) TO STDOUT WITH CSV`;
            const output = fs.createWriteStream(`${tableName}_dump.csv`);

            // COPY komutunu çalıştır
            const copyStream = client.query(query);
            copyStream.pipe(output);

            output.on('finish', () => {
                console.log(`${tableName}_dump.csv dosyası oluşturuldu.`);
            });

            // Stream hatalarını dinle
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

dumpFirst1000RowsFromAllTables();
