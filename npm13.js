const { Client } = require('pg');
const fs = require('fs');

const client = new Client({
    user: 'prod',
    host: 'postgresql-keeper.wvservices.com',
    database: 'prod',
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
            console.log(`Tablo: ${tableName} için ilk 1000 satır alınıyor...`);

            const dataResult = await client.query(`SELECT * FROM ${tableName} LIMIT 1000`);
            const data = dataResult.rows;

            // Verileri .txt dosyasına yaz
            const output = fs.createWriteStream(`${tableName}_first_1000_rows.txt`);
            data.forEach((row) => {
                output.write(JSON.stringify(row) + '\n'); // JSON formatında yaz
            });
            output.end();
            console.log(`${tableName}_first_1000_rows.txt dosyası oluşturuldu.`);
        }

    } catch (err) {
        console.error('Bağlantı hatası:', err);
    } finally {
        await client.end();
        console.log('Veritabanı bağlantısı kapatıldı.');
    }
}

dumpFirst1000RowsFromAllTables();
