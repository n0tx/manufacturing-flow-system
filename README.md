# Manufacturing Flow System MVP

Sistem MVP untuk mendemonstrasikan **Alur Manufaktur Tekstil** — mulai dari pemesanan kain hingga pelunasan pembayaran.
Dibangun menggunakan **Java 17 + Spring Boot 3.2.4** (Backend) dengan dokumentasi API otomatis via **Swagger UI**.

> Proyek ini terinspirasi dari proses bisnis perusahaan tekstil **PT Sinar Sukses Mandiri** yang bergerak di bidang Knitting, Dyeing, dan Printing kain.

---

## Tech Stack

| Layer | Teknologi |
| :--- | :--- |
| Language | Java 17 |
| Framework | Spring Boot 3.2.4 |
| Database | PostgreSQL |
| ORM | Spring Data JPA (Hibernate) |
| Security | Spring Security + JWT Token |
| API Docs | Springdoc OpenAPI (Swagger UI) |
| Build Tool | Maven Wrapper |

---

## Arsitektur & Pola Desain

Aplikasi ini menerapkan tiga pola desain utama:

1. **State Machine Pattern** — Setiap pesanan (`Order`) memiliki kolom `status` yang bergerak maju secara berurutan. Sistem akan **menolak** jika urutan dilompati.
2. **Transaction / Event Log Pattern** — Setiap tahapan (Receiving, Production, Delivery, Payment) dicatat di **tabel terpisah** yang berelasi ke `Order`, sehingga menghasilkan jejak audit (*audit trail*) yang lengkap.
3. **Role-Based Access Control** — Setiap endpoint diamankan berdasarkan peran pengguna (Admin, Gudang, Produksi, Finance).

### Alur Status Pesanan (Order Flow)

```
CREATED → MATERIAL_PREPARED → IN_PRODUCTION → COMPLETED_PRODUCTION → DELIVERED → PAID
  │              │                   │                  │                 │          │
  │              │                   │                  │                 │          │
 Order        Receiving          Production          Production        Delivery   Payment
 dibuat       bahan baku         mesin jalan          selesai QC        dikirim    lunas
              diterima           (Knitting,
                                  Dyeing,
                                  Printing)
```

### Relasi Antar Tabel (ERD Sederhana)

```
customers ──┐
            ├──> orders ──> receivings
products ──┘       │
                   ├──────> productions (banyak log per order)
                   ├──────> deliveries
                   └──────> payments
```

---

## Cara Menjalankan

### Prasyarat
- Java 17 (JDK)
- PostgreSQL (running)
- Terminal / Command Prompt

### Langkah-langkah

**1. Menjalankan Backend (Spring Boot)**
1. **Buat database kosong** di PostgreSQL:
   ```sql
   CREATE DATABASE manufacturing_db;
   ```
2. **Jalankan aplikasi backend**:
   ```bash
   cd backend
   ./mvnw clean spring-boot:run
   ```
3. Backend akan berjalan di `http://localhost:8080` (termasuk Swagger UI).

**2. Menjalankan Frontend (Vue.js)**
1. Buka terminal baru dan masuk ke folder frontend:
   ```bash
   cd frontend
   ```
2. Instal dependensi dan jalankan server *development*:
   ```bash
   npm install
   npm run dev
   ```
3. Buka **Frontend Dashboard** di browser:
   ```
   http://localhost:5173
   ```

> **Tips untuk Demo Ulang:** Jika ingin mereset database dari awal (data bersih), ubah `spring.jpa.hibernate.ddl-auto=update` menjadi `create-drop` di `backend/src/main/resources/application.properties`, lalu restart backend. Setelah selesai, kembalikan ke `update`.

---

## Kredensial Demo

Aplikasi menyediakan fitur *Auto-Seed* untuk mengisi tabel user dengan akun dummy.

**Langkah:** Jalankan `POST /api/auth/seed` di Swagger (tanpa perlu login).

| Username | Password | Role | Hak Akses |
| :--- | :--- | :--- | :--- |
| `admin` | `admin123` | **ADMIN** | Semua fitur |
| `gudang` | `gudang123` | **GUDANG** | Receiving, Delivery |
| `produksi` | `produksi123` | **PRODUKSI** | Production |
| `finance` | `finance123` | **FINANCE** | Payment |

**Cara Login & Otorisasi:**
1. Jalankan `POST /api/auth/login` (username & password sudah terisi otomatis di Swagger).
2. *Copy* nilai `token` dari response.
3. Klik tombol **Authorize** (🔒) di kanan atas Swagger UI.
4. Ketik `Bearer ` (dengan spasi) lalu *paste* token. Contoh: `Bearer eyJhbGci...`
5. Klik **Authorize** → **Close**. Selesai!

---

## Panduan Demo End-to-End (Step by Step)

Ikuti langkah-langkah berikut secara berurutan untuk mendemonstrasikan seluruh alur manufaktur dari awal hingga akhir.

---

### STEP 1 — Seed User & Login

**Endpoint:** `POST /api/auth/seed`
> Klik Try it out → Execute. Sistem akan membuat 4 akun user.

**Endpoint:** `POST /api/auth/login`
> Klik Try it out → Execute (sudah terisi admin/admin123). Copy token dari response.

**Lalu:** Klik tombol 🔒 **Authorize** → paste `Bearer <token>` → Authorize → Close.

---

### STEP 2 — Buat Master Data

**Endpoint:** `POST /api/customers`
```json
{
  "name": "PT Maju Terus Tekstil",
  "email": "purchasing@majuterus.com",
  "phone": "08123456789",
  "address": "Jl. Industri No 5, Kawasan Industri Pulogadung, Jakarta Timur"
}
```
> Verifikasi: `GET /api/customers` → pastikan mendapatkan `id: 1`.

**Endpoint:** `POST /api/products`
```json
{
  "name": "Kain Katun Combed 30s",
  "sku": "FAB-COM30-001",
  "price": 55000,
  "description": "Kain katun combed kualitas ekspor, warna hitam, lebar 150cm"
}
```
> Verifikasi: `GET /api/products` → pastikan mendapatkan `id: 1`.

---

### STEP 3 — Buat Pesanan (Order)

**Endpoint:** `POST /api/orders`
```json
{
  "customerId": 1,
  "productId": 1,
  "quantity": 100
}
```
> **Hasil:** Order tersimpan dengan `totalPrice: 5500000` (100 × 55000) dan status **`CREATED`**.
>
> Verifikasi: `GET /api/orders/1`

---

### STEP 4 — Penerimaan Bahan Baku (Receiving)

*Login sebagai `gudang` / `gudang123` (atau tetap admin).*

**Endpoint:** `POST /api/receivings`
```json
{
  "orderId": 1,
  "rawMaterialNotes": "Diterima: Benang Katun Combed Putih 1000kg, Obat Pewarna Hitam 50kg"
}
```
> **Hasil:** Data receiving tersimpan, status order berubah menjadi **`MATERIAL_PREPARED`**.
>
> Verifikasi: `GET /api/orders/1` → cek status.

---

### STEP 5 — Proses Produksi (Production)

*Login sebagai `produksi` / `produksi123` (atau tetap admin).*

**Endpoint:** `POST /api/productions` — Tahap 1: Knitting (Rajut)
```json
{
  "orderId": 1,
  "productionType": "KNITTING",
  "machineId": "MESIN-RJT-01",
  "notes": "Merajut benang katun combed menjadi kain grey 150cm"
}
```
> **Hasil:** Log produksi tersimpan, status order berubah menjadi **`IN_PRODUCTION`**.

**Endpoint:** `POST /api/productions` — Tahap 2: Dyeing (Celup Warna)
```json
{
  "orderId": 1,
  "productionType": "DYEING",
  "machineId": "MESIN-DYE-03",
  "notes": "Pencelupan warna hitam pekat, suhu 90°C selama 4 jam"
}
```

**Endpoint:** `POST /api/productions` — Tahap 3: Printing (Cetak Motif)
```json
{
  "orderId": 1,
  "productionType": "PRINTING",
  "machineId": "MESIN-PRT-02",
  "notes": "Pencetakan motif logo pelanggan pada sisi kanan kain"
}
```

**Endpoint:** `POST /api/productions/1/finish` — Tandai Produksi Selesai
> Klik Try it out → isi orderId = `1` → Execute. Tidak perlu request body.
>
> **Hasil:** Status order berubah menjadi **`COMPLETED_PRODUCTION`**.
>
> Verifikasi: `GET /api/orders/1` → cek status.

---

### STEP 6 — Pengiriman (Delivery)

*Login sebagai `gudang` / `gudang123` (atau tetap admin).*

**Endpoint:** `POST /api/deliveries`
```json
{
  "orderId": 1,
  "trackingNumber": "SJ-2026-00123",
  "driverName": "Budi Santoso",
  "vehiclePlate": "B 1234 CD"
}
```
> **Hasil:** Data pengiriman tersimpan, status order berubah menjadi **`DELIVERED`**.
>
> Verifikasi: `GET /api/orders/1` → cek status.

---

### STEP 7 — Pembayaran (Payment)

*Login sebagai `finance` / `finance123` (atau tetap admin).*

**Endpoint:** `POST /api/payments`
```json
{
  "orderId": 1,
  "paymentMethod": "BANK_TRANSFER",
  "amountPaid": 5500000,
  "referenceNumber": "BCA-TRF-20260424-998877"
}
```
> **Hasil:** Pembayaran tercatat, status order berubah menjadi **`PAID`** ✅
>
> ⚠️ `amountPaid` harus ≥ `totalPrice` (5.500.000). Jika kurang, sistem akan menolak.
>
> Verifikasi: `GET /api/orders/1` → status = `PAID`. **Siklus pesanan selesai!**

---

## Ringkasan Endpoint API

| # | Method | Endpoint | Role | Fungsi |
|---|--------|----------|------|--------|
| 1 | POST | `/api/auth/seed` | Public | Seed 4 user demo |
| 2 | POST | `/api/auth/login` | Public | Login, dapat JWT token |
| 3 | POST | `/api/customers` | ADMIN | Buat customer baru |
| 4 | GET | `/api/customers` | Auth | Lihat semua customer |
| 5 | POST | `/api/products` | ADMIN | Buat produk baru |
| 6 | GET | `/api/products` | Auth | Lihat semua produk |
| 7 | POST | `/api/orders` | ADMIN | Buat pesanan baru |
| 8 | GET | `/api/orders` | Auth | Lihat semua pesanan |
| 9 | GET | `/api/orders/{id}` | Auth | Detail pesanan |
| 10 | POST | `/api/receivings` | ADMIN, GUDANG | Catat penerimaan bahan |
| 11 | GET | `/api/receivings` | Auth | Lihat semua penerimaan |
| 12 | POST | `/api/productions` | ADMIN, PRODUKSI | Catat aktivitas mesin |
| 13 | POST | `/api/productions/{id}/finish` | ADMIN, PRODUKSI | Tandai produksi selesai |
| 14 | GET | `/api/productions` | Auth | Lihat semua log produksi |
| 15 | POST | `/api/deliveries` | ADMIN, GUDANG | Catat pengiriman |
| 16 | GET | `/api/deliveries` | Auth | Lihat semua pengiriman |
| 17 | POST | `/api/payments` | ADMIN, FINANCE | Catat pembayaran |
| 18 | GET | `/api/payments` | Auth | Lihat semua pembayaran |
