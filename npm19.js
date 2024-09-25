const { Client } = require('pg');
const fs = require('fs');
const path = require('path');

const client = new Client({
    user: 'admin',
    host: 'postgresql-swopfi-htz-fsn1-1.wvservices.com',
    database: 'swopfi-prod',
    password: 'Fke209ejfisoe',
    port: 5432,
});

async function dumpFirst1000RowsFromAllTables() {
    try {
        await client.connect();
        console.log('PostgreSQL veritabanına bağlandı.');

        // db_1 klasörünü oluştur
        const outputDir = path.join(__dirname, '1');

        if (!fs.existsSync(outputDir)) {
            fs.mkdirSync(outputDir);
            console.log(`${outputDir} klasörü oluşturuldu.`);
        }

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

            try {
                const dataResult = await client.query(`SELECT * FROM ${tableName} LIMIT 1000`);
                const data = dataResult.rows;

                // Verileri db_1 klasöründe .txt dosyasına yaz
                const outputFilePath = path.join(outputDir, `${tableName}_first_1000_rows.txt`);
                const output = fs.createWriteStream(outputFilePath);
                data.forEach((row) => {
                    output.write(JSON.stringify(row) + '\n'); // JSON formatında yaz
                });
                output.end();
                console.log(`${outputFilePath} dosyası oluşturuldu.`);
            } catch (err) {
                console.error(`Tablo: ${tableName} için veri alınırken hata oluştu:`, err.message);
            }
        }

    } catch (err) {
        console.error('Bağlantı hatası:', err);
    } finally {
        await client.end();
        console.log('Veritabanı bağlantısı kapatıldı.');
    }
}

dumpFirst1000RowsFromAllTables();
