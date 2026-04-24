# Manufacturing Flow System MVP

Sistem Backend API untuk mengelola alur manufaktur tekstil secara end-to-end.
Terinspirasi dari proses bisnis nyata perusahaan tekstil Indonesia.

## Tech Stack

| Komponen | Teknologi |
| :--- | :--- |
| Language | Java 17 |
| Framework | Spring Boot 3.2.4 |
| Database | PostgreSQL |
| Security | JWT (JSON Web Token) + Role-Based Access |
| API Docs | Swagger (springdoc-openapi 2.6.0) |
| Build Tool | Maven |

## Arsitektur & Pola Desain

Aplikasi ini menggunakan **Clean Architecture** (Controller → Service → Repository) dengan 3 pola desain utama:

1. **State Machine Pattern** — Setiap order memiliki status yang bergerak maju secara berurutan dan tidak bisa dilompati.
2. **Transaction/Event Log Pattern** — Setiap tahapan bisnis dicatat di tabel terpisah (child table) yang berelasi ke tabel `orders`.
3. **Role-Based Constraint** — Setiap endpoint dikunci berdasarkan role user yang berhak mengaksesnya.

## Alur Status Order (State Machine)

```
CREATED → MATERIAL_PREPARED → IN_PRODUCTION → COMPLETED_PRODUCTION → DELIVERED → PAID
```

| Status | Artinya | Siapa yang Mengubah |
| :--- | :--- | :--- |
| `CREATED` | Pesanan baru dibuat | Admin / Sales |
| `MATERIAL_PREPARED` | Bahan baku sudah diterima gudang | Gudang |
| `IN_PRODUCTION` | Sedang dalam proses produksi | Produksi |
| `COMPLETED_PRODUCTION` | Produksi selesai, lolos QC | Produksi |
| `DELIVERED` | Barang sudah dikirim ke pelanggan | Gudang / Logistik |
| `PAID` | Pembayaran lunas diterima | Finance |

## Cara Menjalankan

### Prasyarat
- Java 17
- PostgreSQL (running)
- Database kosong bernama `manufacturing_db`

### Menjalankan Aplikasi
```bash
cd backend/
./mvnw clean spring-boot:run
```

### Membuka API Docs (Swagger UI)
```
http://localhost:8080/swagger-ui/index.html
```

### Reset Database (Fresh Start untuk Demo)
Ubah konfigurasi di `backend/src/main/resources/application.properties`:
```properties
# Ganti 'update' menjadi 'create-drop' untuk reset total
spring.jpa.hibernate.ddl-auto=create-drop
```
Jalankan ulang aplikasi, lalu kembalikan ke `update` setelah selesai.

---

## Panduan Demo (Step-by-Step)

Ikuti langkah-langkah di bawah ini secara berurutan dari atas ke bawah.
Semua payload JSON sudah siap di-copy-paste.

---

### STEP 0: Seed User & Login

**0.1 — Seed akun demo**

Endpoint: `POST /api/auth/seed` → klik Try it out → Execute.

Akun yang tersedia setelah seed:

| Username | Password | Role |
| :--- | :--- | :--- |
| `admin` | `admin123` | ADMIN |
| `gudang` | `gudang123` | GUDANG |
| `produksi` | `produksi123` | PRODUKSI |
| `finance` | `finance123` | FINANCE |

**0.2 — Login sebagai Admin**

Endpoint: `POST /api/auth/login`
```json
{
  "username": "admin",
  "password": "admin123"
}
```
Copy token dari response, klik tombol **Authorize** (🔒), paste dengan format: `Bearer <token>`.

---

### STEP 1: Buat Master Data

**1.1 — Buat Customer**

Endpoint: `POST /api/customers`
```json
{
  "name": "PT Maju Terus Tekstil",
  "email": "purchasing@majuterus.com",
  "phone": "08123456789",
  "address": "Jl. Industri No 5, Tangerang"
}
```

**1.2 — Buat Product**

Endpoint: `POST /api/products`
```json
{
  "name": "Kain Katun Combed 30s",
  "sku": "FAB-COM30-001",
  "price": 55000,
  "description": "Kain katun premium kualitas ekspor, warna hitam"
}
```

**Verifikasi:** `GET /api/customers` dan `GET /api/products` — pastikan masing-masing mendapat `id: 1`.

---

### STEP 2: Buat Order

Endpoint: `POST /api/orders`
```json
{
  "customerId": 1,
  "productId": 1,
  "quantity": 100
}
```

**Verifikasi:** `GET /api/orders/1`
- `totalPrice` otomatis terhitung: 55.000 × 100 = **5.500.000**
- `status`: **CREATED**

---

### STEP 3: Receiving (Gudang Terima Bahan Baku)

*Login sebagai `gudang` / `gudang123`, atau tetap pakai admin.*

Endpoint: `POST /api/receivings`
```json
{
  "orderId": 1,
  "rawMaterialNotes": "Benang Katun Combed Putih 500kg + Obat Pewarna Hitam 50L"
}
```

**Verifikasi:** `GET /api/orders/1` → status berubah menjadi **MATERIAL_PREPARED**

---

### STEP 4: Production (Proses Produksi di Pabrik)

*Login sebagai `produksi` / `produksi123`, atau tetap pakai admin.*

**4.1 — Log Aktivitas Knitting (Merajut)**

Endpoint: `POST /api/productions`
```json
{
  "orderId": 1,
  "productionType": "KNITTING",
  "machineId": "MESIN-RJT-01",
  "notes": "Merajut benang katun combed menjadi kain mentah"
}
```
→ Status order berubah menjadi **IN_PRODUCTION**

**4.2 — Log Aktivitas Dyeing (Pencelupan Warna)**

Endpoint: `POST /api/productions`
```json
{
  "orderId": 1,
  "productionType": "DYEING",
  "machineId": "MESIN-DYE-03",
  "notes": "Pencelupan warna hitam pekat, suhu 90 derajat"
}
```

**4.3 — Log Aktivitas Printing (Pencetakan Motif)**

Endpoint: `POST /api/productions`
```json
{
  "orderId": 1,
  "productionType": "PRINTING",
  "machineId": "MESIN-PRT-02",
  "notes": "Cetak motif logo pelanggan PT Maju Terus"
}
```

**4.4 — Tandai Produksi Selesai**

Endpoint: `POST /api/productions/1/finish` → tidak perlu body, langsung Execute.

**Verifikasi:** `GET /api/orders/1` → status berubah menjadi **COMPLETED_PRODUCTION**

---

### STEP 5: Delivery (Pengiriman ke Pelanggan)

*Login sebagai `gudang` / `gudang123`, atau tetap pakai admin.*

Endpoint: `POST /api/deliveries`
```json
{
  "orderId": 1,
  "trackingNumber": "SJ-2026-00123",
  "driverName": "Budi Santoso",
  "vehiclePlate": "B 1234 CD"
}
```

**Verifikasi:** `GET /api/orders/1` → status berubah menjadi **DELIVERED**

---

### STEP 6: Payment (Pelunasan Pembayaran)

*Login sebagai `finance` / `finance123`, atau tetap pakai admin.*

Endpoint: `POST /api/payments`
```json
{
  "orderId": 1,
  "paymentMethod": "BANK_TRANSFER",
  "amountPaid": 5500000,
  "referenceNumber": "BCA-TRF-20260424-001"
}
```

> ⚠️ `amountPaid` harus ≥ `totalPrice` (5.500.000). Jika kurang, sistem akan menolak.

**Verifikasi:** `GET /api/orders/1` → status berubah menjadi **PAID** ✅

---

## 🎉 Demo Selesai!

Siklus pesanan telah berjalan dari awal hingga akhir:

```
CREATED → MATERIAL_PREPARED → IN_PRODUCTION → COMPLETED_PRODUCTION → DELIVERED → PAID
```

Setiap tahapan tercatat lengkap di tabel masing-masing (`receivings`, `productions`, `deliveries`, `payments`) sebagai audit trail / bukti transaksi.
