from http.server import BaseHTTPRequestHandler, HTTPServer

class RequestHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        # HTTP yanıt kodu ve başlıkları ayarla
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()
        
        # Yanıt olarak "Hello, World!" gönder
        self.wfile.write(b"Hello, World!")

def run(server_class=HTTPServer, handler_class=RequestHandler, port=8081):
    server_address = ('', port)
    httpd = server_class(server_address, handler_class)
    print(f'Sunucu başlatılıyor, http://localhost:{port} adresinden erişebilirsiniz...')
    httpd.serve_forever()

if __name__ == "__main__":
    run()
