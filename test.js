const express = require('express');
const bodyParser = require('body-parser');
const fs = require('fs');

const app = express();
const port = 3000;

app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

app.post('/AdminApi/adminAccess/check2faStatus', (req, res) => {
  // POST verisini txt dosyasına kaydetme
  fs.appendFile('kayit.txt', JSON.stringify(req.body) + '\n', err => {
    if (err) {
      console.error(err);
      res.status(500).send('Internal Server Error');
    } else {
      res.status(200).send('POST verisi başarıyla kaydedildi');
    }
  });
});

app.listen(port, () => {
  console.log(`Sunucu çalışıyor. Port: ${port}`);
});
