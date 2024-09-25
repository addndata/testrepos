const http = require('http');
const httpProxy = require('http-proxy');
const readline = require('readline');

const proxy = httpProxy.createProxyServer({});

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

    server.listen(3128, () => {
        console.log(`Proxy server ${target} için çalışıyor ve 3128 portunu dinliyor.`);
    });
});
