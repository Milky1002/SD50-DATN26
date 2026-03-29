# SD50-DATN26 — Hệ Thống Quản Lý Cửa Hàng Cầu Lông

Ứng dụng Spring Boot quản lý cửa hàng cầu lông gồm 2 khu vực chính:

- **Admin / vận hành nội bộ**: dashboard, quản lý sản phẩm, POS bán tại quầy, khách hàng, nhân viên, tài khoản, hóa đơn, đơn hàng, khuyến mãi, xuất kho, cấu hình trang chủ.
- **Storefront / khách hàng**: xem sản phẩm, tìm kiếm, giỏ hàng, đặt hàng nhanh, thanh toán, đăng ký/đăng nhập khách hàng, quản lý hồ sơ và đơn hàng.

---

## 1. Công nghệ sử dụng

- Java 17
- Spring Boot 4.0.3
- Spring MVC + Thymeleaf
- Spring Data JPA
- SQL Server
- Maven
- Lombok
- Apache POI (xuất Excel)
- BCrypt (`spring-security-crypto`) để mã hóa mật khẩu

---

## 2. Yêu cầu môi trường

- JDK 17+
- Maven 3.9+
- SQL Server 2019+
- `sqlcmd` hoặc SQL Server Management Studio

---

## 3. Cấu hình runtime hiện tại

Theo `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:sqlserver://127.0.0.1:1433;databaseName=sd50;encrypt=false;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=123
spring.jpa.hibernate.ddl-auto=none
server.port=8888
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=60MB
server.tomcat.max-http-form-post-size=60MB
server.tomcat.max-swallow-size=60MB
server.tomcat.max-part-count=-1
app.upload.dir=uploads
```

> `ddl-auto=none` nghĩa là **database phải được tạo bằng SQL script**, ứng dụng không tự sinh schema.

---

## 4. Cài đặt dự án

### Bước 1: Clone source

```bash
git clone https://github.com/Milky1002/SD50-DATN26.git
cd SD50-DATN26
```

### Bước 2: Tạo database

Toàn bộ schema + dữ liệu mẫu được tổ chức thành các file nhỏ trong `SQL_Query/`.  
**Không cần Python.** Script tự xử lý encoding tiếng Việt hoàn toàn trong PowerShell.

#### Cách 1 — PowerShell script (khuyến nghị)

```powershell
cd D:\ProjectWeb\WebDoAn\SD50-DATN26\SQL_Query
.\install.ps1
```

Nếu PowerShell báo lỗi execution policy, chạy một lần rồi thử lại:

```powershell
Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned
```

Tham số tuỳ chọn:

```powershell
# Server khác (ví dụ SQL Express)
.\install.ps1 -Server ".\SQLEXPRESS" -User "sa" -Password "matkhau"

# Chỉ chạy patch (thêm cột thiếu vào DB đã tồn tại, an toàn khi chạy lại)
.\install.ps1 -PatchOnly
```

> **Cách hoạt động:** Script đọc từng file SQL bằng UTF-8 trong PowerShell, ghi lại thành file tạm UTF-16 LE (có BOM), rồi truyền cho `sqlcmd -i`. sqlcmd nhận diện BOM tự động và xử lý `GO` nội bộ — **không cần flag `-f 65001`**, không cần Python.

#### Cách 2 — SSMS (nếu không dùng được PowerShell)

Mở từng file trong thư mục `SQL_Query/` theo thứ tự trong SSMS rồi nhấn **Execute**:

```
01_create_database.sql
02_schema_core.sql → 10_schema_misc.sql
18_schema_ca_lam_viec.sql
11_seed_core.sql → 17_seed_misc.sql
99_patch_missing_columns.sql
```

Script sẽ tự động:

1. Tạo database `sd50` (bỏ qua nếu đã tồn tại)
2. Tạo toàn bộ bảng theo đúng thứ tự dependency
3. Chèn dữ liệu mẫu (mỗi bảng ≥ 20 bản ghi), tiếng Việt đầy đủ

Script **idempotent** — chạy lại nhiều lần không bị lỗi (mỗi bước đều kiểm tra `IF NOT EXISTS`).

### Bước 3: Build và chạy ứng dụng

```bash
mvn clean install
mvn spring-boot:run
```

### Bước 4: Truy cập hệ thống

- Admin login: `http://localhost:8888/login`
- Trang chủ storefront: `http://localhost:8888/`

---

## 5. Tài khoản mặc định

| Username | Password | Vai trò |
|---|---|---|
| `admin` | `admin@123` | ADMIN / Quản lý |
| `nhanvien01` | `admin@123` | STAFF / Nhân viên |
| `nhanvien02` | `admin@123` | STAFF / Nhân viên |

> Mật khẩu mặc định có thể được hệ thống tự chuyển sang BCrypt sau khi đăng nhập thành công.

---

## 6. Cấu trúc SQL hiện tại

### File chính

- `SQL_Query/install_full_database.sql` — file cài đặt toàn bộ database bằng một lệnh
- `SQL_Query/SetupDatabaseSQL.sql` — file schema + seed gốc
- `SQL_Query/run_all_updates.sql` — file gọi tuần tự các script cập nhật

### Nhóm script cập nhật

- `updates/01_cap_nhat_chuc_vu.sql`
- `updates/02_cap_nhat_tai_khoan.sql`
- `updates/03_cap_nhat_nhan_vien.sql`
- `updates/04_cap_nhat_hinh_thuc_thanh_toan.sql`
- `updates/05_cap_nhat_danh_muc.sql`
- `updates/05b_fix_danh_muc.sql`
- `updates/06_cap_nhat_mau_sac.sql`
- `updates/06b_fix_mau_sac.sql`
- `updates/07_cap_nhat_san_pham.sql`
- `updates/07b_fix_san_pham.sql`
- `updates/08_cap_nhat_khuyen_mai.sql`
- `updates/09_them_khach_hang.sql`
- `updates/10_shop_tables.sql`
- `updates/11_sync_storefront_and_homepage.sql`
- `updates/12_unify_tai_khoan.sql`
- `updates/13_normalize_customer_account_links.sql`
- `updates/14_lich_su_hoat_dong_nhan_vien.sql`

### Ghi chú schema

Schema hiện tại có lịch sử phát triển theo nhiều đợt:

- `SetupDatabaseSQL.sql` là nền schema chính
- các file update bổ sung storefront, liên kết tài khoản khách hàng, lịch sử hoạt động nhân viên và đồng bộ thêm dữ liệu/cột mới

Vì vậy, **file nên dùng để cài mới đầy đủ là `install_full_database.sql`**.

---

## 7. Tính năng chính

### 7.1 Khu vực admin / nội bộ

- Đăng nhập / đăng xuất / đổi mật khẩu
- Dashboard tổng quan
- Quản lý nhân viên
- Quản lý tài khoản hệ thống
- Lịch sử hoạt động nhân viên
- Quản lý khách hàng
- Quản lý sản phẩm
  - tạo / sửa / xóa
  - upload ảnh hoặc nhập link ảnh
  - sinh mã sản phẩm, barcode, SKU
  - kiểm tra trùng mã / barcode
  - export Excel
- Quản lý danh mục sản phẩm
- Quản lý màu sắc
- Bán hàng tại quầy (POS)
  - tìm kiếm sản phẩm
  - giỏ hàng nhiều tab đơn hàng
  - áp dụng khuyến mãi hóa đơn
  - tìm khách hàng theo tên hoặc số điện thoại
  - thêm khách hàng mới tại quầy
  - checkout và in phiếu tính tiền PDF
- Quản lý hóa đơn
- Quản lý đơn hàng
- Quản lý xuất kho
- Quản lý / API chương trình khuyến mãi
- Cấu hình trang chủ storefront

### 7.2 Khu vực storefront / khách hàng

- Trang chủ cửa hàng
- Danh sách sản phẩm `/cua-hang`
- Chi tiết sản phẩm `/cua-hang/{id}`
- Tìm kiếm sản phẩm `/tim-kiem`
- Giỏ hàng `/gio-hang`
- Thanh toán `/thanh-toan`
- Đặt hàng nhanh `/dat-hang-nhanh`
- Đăng ký / đăng nhập / đăng xuất khách hàng
- Hồ sơ khách hàng `/tai-khoan/ho-so`
- Theo dõi đơn hàng khách hàng `/tai-khoan/don-hang`

---

## 8. Inventory route và API chính

## 8.1 Auth / hệ thống

| Method | Route | Mô tả |
|---|---|---|
| GET | `/login` | Trang đăng nhập admin/staff |
| POST | `/login` | Xử lý đăng nhập admin/staff |
| GET | `/logout` | Đăng xuất |
| POST | `/api/change-password` | Đổi mật khẩu tài khoản hệ thống |

## 8.2 Dashboard / admin views

| Method | Route | Mô tả |
|---|---|---|
| GET | `/dashboard` | Trang tổng quan |
| GET | `/quan-ly-tai-khoan/them-moi` | Form tạo tài khoản |
| POST | `/quan-ly-tai-khoan/them-moi` | Tạo tài khoản |
| GET | `/quan-ly-tai-khoan/chinh-sua/{id}` | Form sửa tài khoản |
| POST | `/quan-ly-tai-khoan/chinh-sua/{id}` | Cập nhật tài khoản |
| POST | `/quan-ly-tai-khoan/doi-trang-thai/{id}` | Đổi trạng thái tài khoản |
| GET | `/lich-su-nhan-vien` | Xem lịch sử nhân viên |

## 8.3 Sản phẩm

| Method | Route | Mô tả |
|---|---|---|
| GET | `/san-pham` | Danh sách sản phẩm |
| POST | `/san-pham/save` | Tạo / cập nhật sản phẩm |
| GET | `/san-pham/delete/{id}` | Xóa sản phẩm |
| GET | `/san-pham/export` | Xuất Excel sản phẩm |
| GET | `/san-pham/api/search` | Tìm kiếm sản phẩm |
| GET | `/san-pham/api/generate-code` | Sinh mã sản phẩm |
| GET | `/san-pham/api/generate-barcode` | Sinh barcode |
| GET | `/san-pham/api/generate-sku` | Sinh SKU |
| GET | `/san-pham/api/check-ma` | Kiểm tra trùng mã sản phẩm |
| GET | `/san-pham/api/check-barcode` | Kiểm tra trùng barcode |

## 8.4 POS / bán hàng tại quầy

| Method | Route | Mô tả |
|---|---|---|
| GET | `/ban-hang` | Màn hình POS |
| GET | `/ban-hang/api/tim-san-pham` | Tìm sản phẩm cho POS |
| GET | `/ban-hang/api/tim-khach-hang` | Tìm khách hàng theo tên / SĐT |
| POST | `/ban-hang/api/khach-hang` | Tạo khách hàng mới tại quầy |
| POST | `/ban-hang/checkout` | Thanh toán hóa đơn POS |
| GET | `/ban-hang/{id}/phieu-tinh-tien` | Xuất phiếu tính tiền PDF |

## 8.5 Khách hàng admin

| Method | Route | Mô tả |
|---|---|---|
| GET | `/khachhang/hienthi` | Danh sách khách hàng |
| POST | `/khachhang/save` | Tạo / cập nhật khách hàng |
| GET | `/khachhang/edit/{id}` | Lấy thông tin khách hàng |
| GET | `/khachhang/delete/{id}` | Xóa khách hàng |
| GET | `/khachhang/search` | Tìm theo tên |
| GET | `/khachhang/status` | Lọc theo trạng thái |

## 8.6 Storefront

| Method | Route | Mô tả |
|---|---|---|
| GET | `/` | Trang chủ storefront |
| GET | `/cua-hang` | Danh sách sản phẩm storefront |
| GET | `/cua-hang/{id}` | Chi tiết sản phẩm |
| GET | `/tim-kiem` | Tìm kiếm storefront |
| GET | `/khuyen-mai` | Danh sách khuyến mãi storefront |
| GET | `/gio-hang` | Xem giỏ hàng |
| POST | `/gio-hang/them` | Thêm sản phẩm vào giỏ |
| POST | `/gio-hang/cap-nhat` | Cập nhật số lượng giỏ hàng |
| POST | `/gio-hang/xoa` | Xóa một item khỏi giỏ |
| POST | `/gio-hang/xoa-tat-ca` | Xóa toàn bộ giỏ |
| GET | `/thanh-toan` | Trang thanh toán |
| GET | `/thanh-toan/xac-nhan` | Trang xác nhận thanh toán |
| POST | `/dat-hang-nhanh` | Đặt hàng nhanh không cần đăng nhập |
| GET | `/dat-hang-nhanh/xac-nhan` | Xác nhận đơn đặt nhanh |
| GET | `/dang-nhap` | Đăng nhập khách hàng |
| POST | `/dang-nhap` | Xử lý đăng nhập khách hàng |
| GET | `/dang-ky` | Trang đăng ký khách hàng |
| POST | `/dang-ky` | Xử lý đăng ký khách hàng |
| GET | `/dang-xuat` | Đăng xuất khách hàng |
| GET | `/tai-khoan/ho-so` | Hồ sơ khách hàng |
| POST | `/tai-khoan/ho-so` | Cập nhật hồ sơ khách hàng |
| GET | `/tai-khoan/don-hang` | Danh sách đơn hàng của khách |
| GET | `/tai-khoan/don-hang/{id}` | Chi tiết đơn hàng của khách |

## 8.7 Khuyến mãi API

Base path: `/api/chuong-trinh-khuyen-mai`

Các endpoint chính:

- `GET /api/chuong-trinh-khuyen-mai`
- `GET /api/chuong-trinh-khuyen-mai/active`
- `GET /api/chuong-trinh-khuyen-mai/type/{loaiKhuyenMai}`
- `GET /api/chuong-trinh-khuyen-mai/{id}`
- `GET /api/chuong-trinh-khuyen-mai/code/{maChuongTrinh}`
- `POST /api/chuong-trinh-khuyen-mai`
- `PUT /api/chuong-trinh-khuyen-mai/{id}`
- `DELETE /api/chuong-trinh-khuyen-mai/{id}`
- `PATCH /api/chuong-trinh-khuyen-mai/{id}/status`
- `GET /api/chuong-trinh-khuyen-mai/applicable/invoice?orderTotal=...`
- `GET /api/chuong-trinh-khuyen-mai/applicable/product/{sanPhamId}`
- `GET /api/chuong-trinh-khuyen-mai/{promotionId}/calculate-discount?orderTotal=...`
- `GET /api/chuong-trinh-khuyen-mai/history`
- `GET /api/chuong-trinh-khuyen-mai/history/{id}`
- `GET /api/chuong-trinh-khuyen-mai/{promotionId}/history`

---

## 9. Cấu trúc thư mục chính

```text
src/main/java/com/example/sd50datn/
├── Config/                 # Interceptor, MVC config, multipart config
├── Controller/             # MVC + REST controllers
├── Dto/                    # DTO cho API khuyến mãi và dữ liệu trao đổi
├── Entity/                 # JPA entities
├── Model/                  # Account / Staff model
├── Repository/             # Spring Data JPA repository
└── Service/                # Business logic

src/main/resources/
├── application.properties
├── templates/              # Thymeleaf templates (admin + shop)
└── static/                 # CSS / JS / assets

SQL_Query/
├── install_full_database.sql
├── SetupDatabaseSQL.sql
├── run_all_updates.sql
└── updates/
```

---

## 10. Xác thực và phân quyền

Hệ thống đang dùng **session-based authentication** kết hợp interceptor:

- `AuthInterceptor` / `WebMvcConfig`: chặn route nội bộ nếu chưa đăng nhập
- `AdminInterceptor`: giới hạn một số route cho admin/quản lý
- `StaffPosOnlyInterceptor`: ràng buộc một số flow POS theo vai trò
- `ShopAuthService` / `ShopAuthController`: auth riêng cho khách hàng storefront

Ứng dụng hiện **không dùng Spring Security full-stack cho web flow**, mà chủ yếu dùng interceptor + session thủ công.

---

## 11. Lưu ý quan trọng

- `spring.jpa.hibernate.ddl-auto=none` → luôn phải cài DB bằng SQL script
- nên dùng **`SQL_Query/install_full_database.sql`** cho môi trường cài mới
- `run_all_updates.sql` vẫn hữu ích khi nâng cấp DB cũ đã tồn tại
- upload ảnh đang dùng thư mục `uploads/`
- multipart hiện đã cấu hình cho request lớn và nhiều part (`server.tomcat.max-part-count=-1`)

---

## 12. Kiểm tra nhanh sau khi cài đặt

Sau khi cài DB và chạy app:

1. đăng nhập `/login` bằng `admin / admin@123`
2. vào `/san-pham` kiểm tra CRUD sản phẩm
3. vào `/ban-hang` kiểm tra:
   - tìm sản phẩm
   - tìm khách hàng
   - thêm khách hàng mới
   - checkout
4. vào `/lich-su-nhan-vien` kiểm tra log hoạt động
5. vào storefront `/`, `/cua-hang`, `/gio-hang`, `/thanh-toan`

---

## 13. Xử lý lỗi thường gặp

| Lỗi | Cách xử lý |
|---|---|
| Không kết nối được SQL Server | Kiểm tra SQL Server chạy đúng host/port/user/pass trong `application.properties` |
| Lỗi thiếu bảng | Chạy lại `SQL_Query/install_full_database.sql` |
| Lỗi khi chạy script có `:r` | Chạy từ thư mục `SQL_Query` bằng `sqlcmd` hoặc mở bằng SSMS SQLCMD mode |
| Port 8888 bị chiếm | Đổi `server.port` trong `application.properties` |
| Upload ảnh lỗi 413 | Kiểm tra lại multipart config và Tomcat limits trong `application.properties` |

---

## 14. Hướng phát triển tiếp

- thay SQL script rời rạc bằng Flyway/Liquibase
- chuẩn hóa REST API inventory thành OpenAPI/Swagger
- bổ sung test tự động cho POS, checkout, promotions, customer flow
- chuẩn hóa README ảnh chụp màn hình / kiến trúc / sơ đồ DB
