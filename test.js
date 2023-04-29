const fs = require('fs');
const https = require('https');
const mysql = require('mysql');

const token = 'JWT eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImFkZG5kYXRhQGdtYWlsLmNvbSIsImlhdCI6MTY4MjcyMDIwNywiZXhwIjoxNjgyODkzMDA3LCJqdGkiOiIxZjhkNjBmOS0xYTY2LTQ3N2MtYjBiMS1kNDA3MDEzM2YwNzQiLCJ1c2VyX2lkIjo1ODU3OCwib3JpZ19pYXQiOjE2ODI3MjAyMDd9.qbPrvwSK_mR5iWlNINiF-wpPPIxmak9EfjyUIiwbGvY';
const host = 'api.app.binaryedge.io';
const path = '/v2/query/web/search';
const query = 'JSONRPC%20server%20handles%20only%20POST%20requests';
const filename = 'results.txt';
const numPages = 100;

const options = {
  method: 'GET',
  headers: {
    Authorization: token,
    Accept: 'application/json',
  },
};

const connection = mysql.createConnection({
  host: 'localhost',
  user: 'root',
  password: '',
  database: 'ip_port'
});

// Veritabanındaki kayıtları al ve bir Set nesnesine yerleştir.
const existingRecords = new Set();
connection.query('SELECT ip_port FROM ip_port', (error, results) => {
  if (error) {
    console.error(`Error getting existing records from database: ${error}`);
  } else {
    for (const record of results) {
      existingRecords.add(record.ip_port);
    }
  }
});

// API'den veri al ve results.txt'ye kaydet.
for (let page = 1; page <= numPages; page++) {
  const queryParams = `?page=${page}&query=${query}`;
  const url = `https://${host}${path}${queryParams}`;

  setTimeout(() => {
    https.get(url, options, (response) => {
      let data = '';

      response.on('data', (chunk) => {
        data += chunk;
      });
      response.on('end', () => {
        const result = JSON.parse(data);
        const entries = result.data || [];
        const events = result.events || [];

        const newRecords = [];

        for (const event of events) {
          const ip = event.ip;
          const port = event.port;
          const ipPort = `${ip}:${port}`;
          if (!existingRecords.has(ipPort)) {
            newRecords.push(ipPort);
            existingRecords.add(ipPort);
          }
        }

        for (const entry of entries) {
          const { ip, port } = entry;
          const ip_port = `${ip}:${port}`;

          if (!existingRecords.has(ip_port)) {
            newRecords.push(ip_port);
            existingRecords.add(ip_port);
          }
        }

        // Yeni kayıtları veri tabanına ekle ve results.txt'ye yaz
        if (newRecords.length > 0) {
  const insertQuery = `INSERT INTO ip_port (ip_port) VALUES ?`;
  const values = newRecords.map((record) => [record]);

  connection.query(insertQuery, [values], (error, results) => {
    if (error) {
      console.error(`Error inserting new records into database: ${error}`);
    } else {
      console.log(`Inserted ${results.affectedRows} new records into database.`);
    }
  });

const fileContent = newRecords.join('\n') + '\n';
  fs.appendFile(filename, fileContent, (error) => {
    if (error) {
      console.error(`Error appending data to file: ${error}`);
    } else {
      console.log(`Appended ${newRecords.length} records to file.`);
    }
  });
}

  });
}).on('error', (error) => {
  
 
console.error(`Error getting data from API: ${error}`);
});
