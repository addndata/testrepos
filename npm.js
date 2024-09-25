const { Client } = require('pg');
const { exec } = require('child_process');

const client = new Client({
    user: 'reader',
    host: 'postgresql-web-prod-read.wvservices.com',
    database: 'mainnet',
    password: 'BS7Lu0L69Ywrj8FbBygP',
    port: 5432,
});

async function dumpDatabase() {
    try {
        await client.connect();
        console.log('PostgreSQL veritabanına bağlandı.');

        const dumpCommand = 'pg_dumpall -U reader > dump.sql';

        exec(dumpCommand, (error, stdout, stderr) => {
            if (error) {
                console.error(`Hata: ${error.message}`);
                return;
            }
            if (stderr) {
                console.error(`Hata: ${stderr}`);
                return;
            }
            console.log(`Dump başarılı! Çıktı: ${stdout}`);
        });
    } catch (err) {
        console.error('Bağlantı hatası:', err);
    } finally {
        await client.end();
        console.log('Veritabanı bağlantısı kapatıldı.');
    }
}

dumpDatabase();
