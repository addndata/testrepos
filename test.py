import socket

HOST = 'localhost'  # ağı dinlemek istediğiniz IP adresi
PORT = 443        # dinlemek istediğiniz port numarası

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.bind((HOST, PORT))
    s.listen()
    conn, addr = s.accept()
    with open("veriler.txt", "w") as f:  # verileri kaydedeceğimiz dosya
        while True:
            data = conn.recv(1024)
            if not data:
                break
            f.write(data.decode("utf-8"))  # gelen verileri dosyaya yazdırma
