const http = require('http');
const httpProxy = require('http-proxy');
const readline = require('readline');

const proxy = httpProxy.createProxyServer({});
const port = 4000; // Proxy'nin dinleyeceği port

const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

rl.question('Hedef URL\'yi girin (örneğin: http://example.com): ', (target) => {
    const server = http.createServer((req, res) => {
        proxy.web(req, res, { target: target }, (error) => {
            res.writeHead(502);
            res.end('Bad Gateway');
        });
    });

    server.listen(port, () => {
        console.log(`Proxy server ${target} için çalışıyor ve ${port} portunu dinliyor.`);
    });
});
