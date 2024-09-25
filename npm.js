const { Client } = require('pg');
const { exec } = require('child_process');

const client = new Client({
    user: 'admin',
    host: 'postgresql-swopfi-htz-hel1-1.wvservices.com',
    database: 'postgres', // Burayı ihtiyacınıza göre güncelleyin
    password: 'FojfojFEie20f3g',
    port: 5432,
});

async function dumpDatabase() {
    try {
        await client.connect();
        console.log('PostgreSQL veritabanına bağlandı.');

        // Tüm veritabanlarını dump etmek için pg_dumpall
        const dumpCommand = `pg_dumpall -U admin -h postgresql-swopfi-htz-hel1-1.wvservices.com > dump.sql`;

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
