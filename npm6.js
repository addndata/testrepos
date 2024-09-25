const { Client } = require('pg');
const { exec } = require('child_process');

const client = new Client({
    user: 'prod',
    host: 'postgresql-keeper.wvservices.com',
    database: 'data-service-prod', // Burayı ihtiyacınıza göre güncelleyin
    password: 'fsojf208efisoefgseg',
    port: 5432,
});

async function dumpDatabase() {
    try {
        await client.connect();
        console.log('PostgreSQL veritabanına bağlandı.');

        // Tüm veritabanlarını dump etmek için pg_dumpall
        const dumpCommand = `pg_dumpall -U prod -h postgresql-keeper.wvservices.com > dump.sql`;

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
