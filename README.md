# Manufacturing Flow System MVP

Sistem MVP untuk mendemonstrasikan Alur Manufaktur (Order -> Receiving -> Production -> Delivery -> Payment).
Dibuat menggunakan Java Spring Boot (Backend) dan Vue.js (Frontend).

## Cara Menjalankan Backend

1. Pastikan Anda memiliki PostgreSQL yang berjalan.
2. Buat database kosong bernama `manufacturing_db` (dengan user `springboot` dan password `springboot`).
3. Masuk ke folder `backend/` dan jalankan:
   ```bash
   ./mvnw clean spring-boot:run
   ```
4. Buka Swagger UI di browser: `http://localhost:8080/swagger-ui/index.html`

## Testing & Kredensial Demo

Agar memudahkan proses pengetesan API di Swagger atau Postman, aplikasi ini menyediakan fitur *Auto-Seed* untuk mengisi tabel *User* dengan akun-akun dummy.

1. Buka Swagger UI.
2. Cari dan jalankan endpoint `POST /api/auth/seed`.
3. Setelah itu, Anda bisa menggunakan salah satu kredensial berikut untuk login di `POST /api/auth/login`:

| Username | Password | Role |
| :--- | :--- | :--- |
| `admin` | `admin123` | **ADMIN** |
| `gudang` | `gudang123` | **GUDANG** |
| `produksi` | `produksi123` | **PRODUKSI** |
| `finance` | `finance123` | **FINANCE** |

4. *Copy* JWT token yang dikembalikan dari hasil login.
5. Klik tombol **Authorize** (gembok) di kanan atas Swagger UI, lalu *paste* token Anda (jangan lupa awali dengan kata `Bearer `, contoh: `Bearer eyJ...`). Anda kini bisa mengakses semua endpoint yang diamankan!

## Panduan Pengetesan Alur (Flow)

Setelah Anda melakukan otentikasi di Swagger sebagai `admin`, ikuti urutan pengetesan berikut:

### 1. Master Data (Customer & Product)
*Pastikan Anda sudah login sebagai Admin.*
- **POST /api/customers**: Masukkan data JSON customer (contoh: `{"name": "PT Maju Terus", "email": "info@maju.com", "phone": "08111", "address": "Jakarta"}`).
- **GET /api/customers**: Pastikan data customer berhasil ditarik dan mendapatkan `id: 1`.
- **POST /api/products**: Masukkan data JSON produk (contoh: `{"name": "Kain Katun", "sku": "FAB-01", "price": 50000, "description": "Katun Premium"}`).
- **GET /api/products**: Pastikan data produk berhasil ditarik dan mendapatkan `id: 1`.

### 2. Order Flow (Sales)
*Pastikan Anda sudah login sebagai Admin.*
- **POST /api/orders**: Buat order baru dengan merujuk pada `id` customer dan produk yang baru saja dibuat. Contoh JSON:
  ```json
  {
    "customerId": 1,
    "productId": 1,
    "quantity": 100
  }
  ```
- **GET /api/orders**: Pastikan order berhasil tersimpan. Sistem akan mengalikan harga dengan quantity secara otomatis menjadi `totalPrice` dan menetapkan status pesanan sebagai `CREATED`.

### 3. Receiving Flow (Gudang)
*Pastikan Anda sudah login sebagai Admin atau Gudang (`gudang` / `gudang123`).*
- **POST /api/receivings**: Proses penerimaan bahan baku untuk pesanan yang ada. Contoh JSON:
  ```json
  {
    "orderId": 1,
    "rawMaterialNotes": "Benang Katun Combed Putih 1000kg"
  }
  ```
- *Perhatikan*: Sistem akan memvalidasi apakah status order masih `CREATED`. Jika ya, data akan disimpan dan status order otomatis berubah menjadi `MATERIAL_PREPARED`. Cek kembali dengan `GET /api/orders`.

### 4. Production Flow (Pabrik)
*Pastikan Anda sudah login sebagai Admin atau Produksi (`produksi` / `produksi123`).*
- **POST /api/productions**: Catat aktivitas mesin. Contoh JSON:
  ```json
  {
    "orderId": 1,
    "productionType": "KNITTING",
    "machineId": "MESIN-RJT-01",
    "notes": "Merajut benang katun combed"
  }
  ```
- *Perhatikan*: Status order akan berubah dari `MATERIAL_PREPARED` menjadi `IN_PRODUCTION`.
- **POST /api/productions/1/finish**: Jika seluruh tahapan produksi (Knitting, Dyeing, Printing) sudah selesai, panggil endpoint ini untuk menandai produksi rampung. Status order akan berubah menjadi `COMPLETED_PRODUCTION`.

### 5. Delivery Flow (Logistik Gudang)
*Pastikan Anda login sebagai Admin atau Gudang (`gudang` / `gudang123`).*
- **POST /api/deliveries**: Proses pengiriman barang yang sudah jadi. Contoh JSON:
  ```json
  {
    "orderId": 1,
    "trackingNumber": "RESI-00123",
    "driverName": "Budi Santoso",
    "vehiclePlate": "B 1234 CD"
  }
  ```
- *Perhatikan*: Status order otomatis berubah menjadi `DELIVERED`.

### 6. Payment Flow (Keuangan)
*Pastikan Anda login sebagai Admin atau Finance (`finance` / `finance123`).*
- **POST /api/payments**: Catat pembayaran dari pelanggan. Contoh JSON:
  ```json
  {
    "orderId": 1,
    "paymentMethod": "BANK_TRANSFER",
    "amountPaid": 5000000,
    "referenceNumber": "BCA-998877"
  }
  ```
- *Perhatikan*: Sistem akan memvalidasi apakah tagihan dibayar penuh (`amountPaid` >= `totalPrice`). Jika sukses, status order berubah menjadi `PAID` dan siklus pesanan selesai!
