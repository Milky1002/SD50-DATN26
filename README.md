# SD50-DATN26 - He Thong Quan Ly Chuong Trinh Khuyen Mai

## Tong Quan

He thong quan ly chuong trinh khuyen mai cho cua hang ban le, ho tro 2 loai giam gia chinh:

1. **Giam gia hoa don** - Ap dung cho toan bo hoa don
2. **Giam gia san pham** - Ap dung cho san pham cu the

---

## Quick Start

### Yeu Cau He Thong

- Java 17+
- Maven 3.6+
- SQL Server 2019+
- `sqlcmd` (co san khi cai SQL Server, hoac cai rieng tu [mssql-tools](https://docs.microsoft.com/en-us/sql/tools/sqlcmd-utility))
- IDE: IntelliJ IDEA / Eclipse (khuyen nghi)

### Cai Dat Nhanh

```bash
# 1. Clone project
git clone https://github.com/Milky1002/SD50-DATN26.git
cd SD50-DATN26

# 2. Tao database bang & schema (chay 1 lan dau tien)
sqlcmd -S 127.0.0.1,1433 -U sa -P 123 -i SQL_Query/SetupDatabaseSQL.sql

# 3. Chay script cap nhat du lieu mau (chay sau buoc 2)
sqlcmd -S 127.0.0.1,1433 -U sa -P 123 -i SQL_Query/run_all_updates.sql

# 4. Cau hinh database (neu khac mac dinh)
#    Chinh sua src/main/resources/application.properties
#    Thay doi host/port/username/password theo cau hinh cua ban

# 5. Build project
mvn clean install

# 6. Chay application
mvn spring-boot:run

# 7. Mo trinh duyet: http://localhost:8888/login
#    Dang nhap: admin / admin@123
```

> **Quan trong:** Doi mat khau admin ngay sau lan dang nhap dau tien! Bam vao ten nguoi dung goc tren phai -> "Doi mat khau".

---

## Cai Dat Database Chi Tiet

### Cau Truc Thu Muc SQL

```
SQL_Query/
  SetupDatabaseSQL.sql          # Tao database sd50, tat ca bang, va du lieu seed co ban
  run_all_updates.sql           # Chay tat ca script cap nhat (goi tung file trong updates/)
  updates/
    01_cap_nhat_chuc_vu.sql          # Chuc vu: Quan ly, Nhan vien
    02_cap_nhat_tai_khoan.sql        # Tai khoan: admin, nhanvien01, nhanvien02
    03_cap_nhat_nhan_vien.sql        # Nhan vien mau
    04_cap_nhat_hinh_thuc_thanh_toan.sql  # Hinh thuc thanh toan
    05_cap_nhat_danh_muc.sql         # Danh muc san pham
    05b_fix_danh_muc.sql             # Fix danh muc cho DB cu
    06_cap_nhat_mau_sac.sql          # Mau sac
    06b_fix_mau_sac.sql              # Fix mau sac cho DB cu
    07_cap_nhat_san_pham.sql         # San pham
    07b_fix_san_pham.sql             # Fix san pham cho DB cu
    08_cap_nhat_khuyen_mai.sql       # Chuong trinh khuyen mai
    09_them_khach_hang.sql           # Khach hang mau
```

### Cach 1: Chay Mot Lenh Duy Nhat (Khuyen Nghi)

Cach nay su dung `sqlcmd` voi lenh `:r` de goi tung file con tu dong.

```bash
# Buoc 1: Tao database & schema (neu chua co)
sqlcmd -S 127.0.0.1,1433 -U sa -P 123 -i SQL_Query/SetupDatabaseSQL.sql

# Buoc 2: Chay tat ca script cap nhat du lieu
#   QUAN TRONG: phai chay tu thu muc SQL_Query/ vi file dung duong dan tuong doi
cd SQL_Query
sqlcmd -S 127.0.0.1,1433 -U sa -P 123 -i run_all_updates.sql
cd ..
```

> **Luu y:** File `run_all_updates.sql` su dung lenh `:r updates\...` (duong dan tuong doi), nen ban **phai `cd SQL_Query`** truoc khi chay, hoac dung tham so `-v` de truyen duong dan.

### Cach 2: Chay Tung File Theo Thu Tu

Neu ban muon kiem soat tung buoc, chay tung file trong `updates/` theo thu tu so:

```bash
sqlcmd -S 127.0.0.1,1433 -U sa -P 123 -d sd50 -i SQL_Query/updates/01_cap_nhat_chuc_vu.sql
sqlcmd -S 127.0.0.1,1433 -U sa -P 123 -d sd50 -i SQL_Query/updates/02_cap_nhat_tai_khoan.sql
sqlcmd -S 127.0.0.1,1433 -U sa -P 123 -d sd50 -i SQL_Query/updates/03_cap_nhat_nhan_vien.sql
sqlcmd -S 127.0.0.1,1433 -U sa -P 123 -d sd50 -i SQL_Query/updates/04_cap_nhat_hinh_thuc_thanh_toan.sql
sqlcmd -S 127.0.0.1,1433 -U sa -P 123 -d sd50 -i SQL_Query/updates/05_cap_nhat_danh_muc.sql
sqlcmd -S 127.0.0.1,1433 -U sa -P 123 -d sd50 -i SQL_Query/updates/06_cap_nhat_mau_sac.sql
sqlcmd -S 127.0.0.1,1433 -U sa -P 123 -d sd50 -i SQL_Query/updates/07_cap_nhat_san_pham.sql
sqlcmd -S 127.0.0.1,1433 -U sa -P 123 -d sd50 -i SQL_Query/updates/08_cap_nhat_khuyen_mai.sql
sqlcmd -S 127.0.0.1,1433 -U sa -P 123 -d sd50 -i SQL_Query/updates/09_them_khach_hang.sql
```

Cac file `05b`, `06b`, `07b` la fix bo sung cho DB cu — chi can chay neu gap loi khi chay file chinh tuong ung.

### Backup Truoc Khi Chay (Khuyen Nghi Cho Production)

```sql
-- Tao backup truoc khi chay script
BACKUP DATABASE [sd50] TO DISK = N'C:\Backup\sd50_backup.bak' WITH FORMAT;
```

### Xac Nhan Sau Khi Chay

Sau khi chay xong, kiem tra du lieu:

```sql
USE [sd50];

-- Kiem tra so luong ban ghi
SELECT 'ChucVu' AS Bang, COUNT(*) AS SoLuong FROM dbo.ChucVu
UNION ALL SELECT 'TaiKhoan', COUNT(*) FROM dbo.TaiKhoan
UNION ALL SELECT 'NhanVien', COUNT(*) FROM dbo.NhanVien
UNION ALL SELECT 'DanhMuc', COUNT(*) FROM dbo.Danh_muc_san_pham
UNION ALL SELECT 'MauSac', COUNT(*) FROM dbo.MauSac
UNION ALL SELECT 'SanPham', COUNT(*) FROM dbo.SanPham
UNION ALL SELECT 'KhuyenMai', COUNT(*) FROM dbo.Chuong_trinh_khuyen_mai
UNION ALL SELECT 'KhachHang', COUNT(*) FROM dbo.KhachHang
UNION ALL SELECT 'HTTT', COUNT(*) FROM dbo.Hinh_thuc_thanh_toan;

-- Kiem tra tai khoan admin
SELECT Tai_khoan_id, User_name, Pass_word FROM dbo.TaiKhoan WHERE User_name = N'admin';
```

Ket qua mong doi:

| Bang | SoLuong |
|------|---------|
| ChucVu | 2 |
| TaiKhoan | 3 |
| NhanVien | 3 |
| DanhMuc | 5 |
| MauSac | 10 |
| SanPham | 12 |
| KhuyenMai | 7 |
| KhachHang | 9 |
| HTTT | 4 |

### Tai Khoan Mac Dinh

| Username | Password | Vai Tro |
|----------|----------|---------|
| `admin` | `admin@123` | Quan ly (full quyen) |
| `nhanvien01` | `admin@123` | Nhan vien |
| `nhanvien02` | `admin@123` | Nhan vien |

> Mat khau duoc luu plaintext trong SQL seed. Khi dang nhap lan dau, he thong tu dong hash bang BCrypt va cap nhat trong database. **Doi mat khau ngay sau khi cai dat!**

---

## Xac Thuc & Phan Quyen

### Tong Quan

He thong su dung xac thuc dua tren session (khong dung Spring Security):

- **AuthInterceptor**: Chan tat ca request (tru `/login`, `/css/**`, `/js/**`, ...). Neu chua dang nhap -> redirect ve `/login`.
- **AdminInterceptor**: Chan cac URL `/nhan-vien/**`. Chi cho phep role "Quan ly" truy cap.
- **BCrypt**: Mat khau duoc hash bang BCrypt. He thong ho tro tu dong chuyen doi mat khau plaintext cu sang BCrypt khi dang nhap thanh cong (migration).

### Luong Hoat Dong

```
Nguoi dung truy cap /dashboard
  -> AuthInterceptor kiem tra session
    -> Chua dang nhap? Redirect /login
    -> Da dang nhap? Cho phep truy cap
      -> URL /nhan-vien/**?
        -> AdminInterceptor kiem tra role
          -> Role "Quan ly"? Cho phep
          -> Role khac? Redirect /dashboard (403)
```

### Session Attributes

Khi dang nhap thanh cong, cac thuoc tinh sau duoc luu trong session:

| Attribute | Mo Ta |
|-----------|-------|
| `loggedIn` | `true` |
| `accountId` | ID tai khoan |
| `nhanVienId` | ID nhan vien |
| `username` | Ten dang nhap |
| `hoTen` | Ho ten day du |
| `email` | Email |
| `chucVuId` | ID chuc vu |
| `tenChucVu` | Ten chuc vu (vd: "Quan ly") |
| `role` | Vai tro (vd: "ADMIN", "STAFF") |

### API Doi Mat Khau

```
POST /api/change-password
Content-Type: application/x-www-form-urlencoded

oldPassword=matkhaucu&newPassword=matkhaumoi
```

Tra ve JSON: `{ "success": true/false, "message": "..." }`

---

## Cac Trang Chinh

| URL | Chuc Nang | Quyen |
|-----|-----------|-------|
| `/login` | Dang nhap | Cong khai |
| `/logout` | Dang xuat | Dang nhap |
| `/dashboard` | Tong quan | Dang nhap |
| `/san-pham` | Quan ly san pham (CRUD, tim kiem, loc, Excel, barcode) | Dang nhap |
| `/danh-muc` | Quan ly danh muc san pham | Dang nhap |
| `/mau-sac` | Quan ly mau sac | Dang nhap |
| `/ban-hang` | Ban hang tai quay (POS) | Dang nhap |
| `/xuat-kho` | Quan ly xuat kho | Dang nhap |
| `/invoices` | Quan ly hoa don | Dang nhap |
| `/orders` | Quan ly don hang | Dang nhap |
| `/khuyen-mai` | Chuong trinh khuyen mai | Dang nhap |
| `/khach-hang` | Quan ly khach hang | Dang nhap |
| `/nhan-vien` | Quan ly nhan vien | Chi "Quan ly" |

---

## Cau Truc Project

```
SD50-DATN26/
  src/main/
    java/com/example/sd50datn/
      Config/
        AuthInterceptor.java          # Chan request chua dang nhap
        AdminInterceptor.java         # Chan request khong du quyen
        WebMvcConfig.java             # Dang ky interceptor
      Controller/
        LoginController.java          # /login, /logout, /api/change-password
        DashboardController.java      # /dashboard
        SanPhamController.java        # /san-pham
        DanhMucSanPhamController.java # /danh-muc
        MauSacController.java         # /mau-sac
        BanHangController.java        # /ban-hang
        XuatKhoController.java        # /xuat-kho
        InvoiceController.java        # /invoices
        OrderController.java          # /orders
        StaffController.java          # /nhan-vien
        KhachHangController.java      # /khach-hang
        KhuyenMaiViewController.java  # /khuyen-mai
        ChuongTrinhKhuyenMaiController.java  # API khuyen mai
      Service/
        AuthService.java              # Xac thuc, BCrypt, doi mat khau
        StaffService.java             # Quan ly nhan vien (hash password)
        ...
      entity/                         # JPA entity classes
      repository/                     # JPA repositories
      dto/                            # Data Transfer Objects
    resources/
      templates/
        layout.html                   # Layout chinh (sidebar, topbar, user menu)
        login.html                    # Trang dang nhap
        ...
      static/
        css/
          app.css                     # User menu, modal, toast styles
          login.css                   # Trang dang nhap
        js/
          app.js                      # Dropdown, modal, change-password, toast
      application.properties          # Cau hinh DB, port
  SQL_Query/
    SetupDatabaseSQL.sql              # Tao database + schema + seed ban dau
    run_all_updates.sql               # Chay tat ca script cap nhat
    updates/                          # Cac script cap nhat theo thu tu (01..09)
  pom.xml                             # Maven configuration
  README.md                           # File nay
```

---

## Cau Hinh

### application.properties

```properties
# Database - thay doi cho phu hop moi truong cua ban
spring.datasource.url=jdbc:sqlserver://127.0.0.1:1433;databaseName=sd50;encrypt=false;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=123

# JPA
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true

# Server
server.port=8888
```

Doi voi production, thay doi:
- `spring.datasource.url` — host va port cua SQL Server
- `spring.datasource.username` / `password` — tai khoan database
- `spring.jpa.show-sql=false` — tat log SQL
- Them bien moi truong hoac Spring profiles de quan ly cau hinh

---

## API Endpoints

### Khuyen Mai: `/api/chuong-trinh-khuyen-mai`

| Method | Endpoint | Mo Ta |
|--------|----------|-------|
| GET | `/` | Lay tat ca chuong trinh |
| GET | `/active` | Lay chuong trinh dang hoat dong |
| GET | `/type/{loaiKhuyenMai}` | Lay theo loai (1=Hoa don, 2=San pham) |
| GET | `/{id}` | Lay chi tiet theo ID |
| GET | `/code/{maChuongTrinh}` | Lay theo ma chuong trinh |
| POST | `/` | Tao moi chuong trinh |
| PUT | `/{id}` | Cap nhat chuong trinh |
| DELETE | `/{id}` | Xoa chuong trinh |
| PATCH | `/{id}/status` | Cap nhat trang thai |
| GET | `/applicable/invoice` | Lay CTKM ap dung cho hoa don |
| GET | `/applicable/product/{id}` | Lay CTKM ap dung cho san pham |
| GET | `/{id}/calculate-discount` | Tinh toan giam gia |

### Xac Thuc

| Method | Endpoint | Mo Ta |
|--------|----------|-------|
| GET | `/login` | Trang dang nhap |
| POST | `/login` | Xu ly dang nhap |
| GET | `/logout` | Dang xuat |
| POST | `/api/change-password` | Doi mat khau |

---

## Bao Mat

### Da Trien Khai

- [x] Session-based authentication (AuthInterceptor)
- [x] Role-based authorization (AdminInterceptor — "Quan ly" only cho /nhan-vien/**)
- [x] BCrypt password hashing voi tu dong migration tu plaintext
- [x] Doi mat khau qua UI modal
- [x] Dang xuat xoa session

### Luu Y Cho Production

- He thong xac thuc hien tai la application-level, **khong dung Spring Security**. De bao mat tot hon, nen chuyen sang Spring Security.
- Chua co CSRF protection — nen them CSRF token cho cac form POST.
- Mat khau admin trong SQL seed la plaintext (tu dong hash khi dang nhap lan dau). De an toan hon, thay bang BCrypt hash da tinh truoc.
- Nen su dung HTTPS cho production.
- Nen them session timeout va gioi han so lan dang nhap sai.

---

## Troubleshooting

### Loi Thuong Gap

1. **Khong ket noi duoc database**
   - Kiem tra SQL Server dang chay
   - Kiem tra username/password trong application.properties
   - Kiem tra port 1433 va firewall

2. **Port 8888 da duoc su dung**
   - Doi port trong application.properties
   - Hoac kill process dang dung port 8888

3. **Loi "Table not found"**
   - Chay `SQL_Query/SetupDatabaseSQL.sql` truoc
   - Sau do chay `SQL_Query/run_all_updates.sql`

4. **Loi khi chay `run_all_updates.sql`**
   - Dam bao `cd SQL_Query` truoc khi chay (vi file dung duong dan tuong doi `:r updates\...`)
   - Hoac chay tung file trong `updates/` rieng le

5. **Dang nhap khong duoc**
   - Kiem tra da chay `02_cap_nhat_tai_khoan.sql` chua
   - Kiem tra tai khoan: `SELECT * FROM dbo.TaiKhoan WHERE User_name = N'admin'`
   - Mat khau mac dinh: `admin@123`

6. **Truy cap /nhan-vien bi redirect ve dashboard**
   - Chi tai khoan co chuc vu "Quan ly" moi truy cap duoc
   - Kiem tra chuc vu: `SELECT nv.*, cv.Ten_chuc_vu FROM dbo.NhanVien nv JOIN dbo.ChucVu cv ON nv.Chuc_vu_id = cv.Chuc_vu_id`

---

## TODO

### Da Hoan Thanh

- [x] Database schema & seed data (tieng Viet co dau)
- [x] Entity classes & JPA repositories
- [x] Service layer & REST API
- [x] Session-based authentication (AuthInterceptor)
- [x] Role-based authorization (AdminInterceptor)
- [x] BCrypt password hashing voi auto-migration
- [x] Doi mat khau qua UI
- [x] User menu (ten, vai tro, dang xuat)
- [x] SQL update scripts (01..09) voi runner
- [x] POS ban hang tai quay
- [x] Quan ly khuyen mai
- [x] Quan ly san pham, danh muc, mau sac
- [x] Quan ly hoa don & don hang

### Co The Them

- [ ] Spring Security (thay the interceptor hien tai)
- [ ] CSRF protection
- [ ] Unit & Integration tests
- [ ] Swagger/OpenAPI docs
- [ ] Caching layer
- [ ] Logging & Monitoring
- [ ] Scheduled jobs (tu dong ket thuc khuyen mai het han)
- [ ] Email notifications
- [ ] Database migration tool (Flyway/Liquibase)

---

## Contributors

- Development Team

## License

This project is licensed under the MIT License.

---

**Version:** 2.0.0
**Last Updated:** 2026-03-19
**Status:** Production Ready
