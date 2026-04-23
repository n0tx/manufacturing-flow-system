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
5. Klik tombol **Authorize** (gembok) di kanan atas Swagger UI, lalu *paste* token Anda. Anda kini bisa mengakses semua endpoint yang diamankan!
