# SD50-DATN26 - Hệ Thống Quản Lý Chương Trình Khuyến Mại

## 📋 Tổng Quan

Hệ thống quản lý chương trình khuyến mại cho cửa hàng bán lẻ, hỗ trợ 2 loại giảm giá chính:

1. **Giảm giá hóa đơn** - Áp dụng cho toàn bộ hóa đơn
2. **Giảm giá sản phẩm** - Áp dụng cho sản phẩm cụ thể

## 🚀 Quick Start

### Yêu Cầu Hệ Thống

- Java 17+
- Maven 3.6+
- SQL Server 2019+
- IDE: IntelliJ IDEA / Eclipse (khuyến nghị)

### Cài Đặt Nhanh

```bash
# 1. Clone project (nếu từ git)
git clone <repository-url>
cd SD50-DATN26

# 2. Chạy file SQL duy nhất để tạo database + schema + dữ liệu mẫu:
#    SQL_Query/SetupDatabaseSQL.sql
#    (File này tự tạo database sd50, tất cả bảng, và seed data)

# 3. Cấu hình database
# Chỉnh sửa src/main/resources/application.properties
# Thay đổi username/password theo cấu hình của bạn

# 4. Build project
mvn clean install

# 5. Chạy application
mvn spring-boot:run

# 6. Mở trình duyệt: http://localhost:8888/dashboard
```

### Cài Đặt Database

Chỉ cần chạy **1 file duy nhất**: `SQL_Query/SetupDatabaseSQL.sql`

File này bao gồm:
- Tạo database `sd50`
- Tất cả bảng (ecommerce, khuyến mại, nhập/xuất kho)
- Dữ liệu mẫu: nhân viên, danh mục, màu sắc, sản phẩm, khuyến mại

### Các Trang Chính

| URL | Chức năng |
|-----|-----------|
| `/dashboard` | Tổng quan |
| `/san-pham` | Quản lý sản phẩm (CRUD, tìm kiếm, lọc, Excel, barcode) |
| `/danh-muc` | Quản lý danh mục sản phẩm |
| `/mau-sac` | Quản lý màu sắc |
| `/ban-hang` | Bán hàng tại quầy (POS) |
| `/xuat-kho` | Quản lý xuất kho |
| `/invoices` | Quản lý hóa đơn |
| `/orders` | Quản lý đơn hàng |
| `/khuyen-mai` | Chương trình khuyến mại |

## 📚 Tài Liệu

### Tài Liệu Chính

1. **[QUICKSTART.md](./QUICKSTART.md)** - Hướng dẫn bắt đầu nhanh
   - Cài đặt từng bước
   - Cấu hình database
   - Test API
   - Tích hợp frontend

2. **[PROMOTION_README.md](./PROMOTION_README.md)** - Tài liệu chi tiết đầy đủ
   - Cấu trúc database
   - API endpoints
   - Business logic
   - Ví dụ sử dụng

3. **[IMPLEMENTATION_SUMMARY.md](./IMPLEMENTATION_SUMMARY.md)** - Tóm tắt triển khai
   - Danh sách file đã tạo
   - Tính năng chính
   - Validation rules
   - Performance tips

### Tài Liệu Bổ Sung

- **[postman_collection.json](./postman_collection.json)** - Postman collection để test API
- **[example_frontend.html](./example_frontend.html)** - Demo frontend tích hợp API
- **[promotion_schema.sql](./promotion_schema.sql)** - Database schema

## 🏗️ Cấu Trúc Project

```
SD50-DATN26/
├── src/
│   ├── main/
│   │   ├── java/com/example/sd50datn/
│   │   │   ├── entity/              # Entity classes
│   │   │   │   ├── ChuongTrinhKhuyenMai.java
│   │   │   │   ├── ChuongTrinhKhuyenMaiChiTiet.java
│   │   │   │   ├── LichSuApDungKhuyenMai.java
│   │   │   │   └── ... (other entities)
│   │   │   ├── repository/          # JPA repositories
│   │   │   │   ├── ChuongTrinhKhuyenMaiRepository.java
│   │   │   │   └── ...
│   │   │   ├── service/             # Business logic
│   │   │   │   └── ChuongTrinhKhuyenMaiService.java
│   │   │   ├── controller/          # REST controllers
│   │   │   │   └── ChuongTrinhKhuyenMaiController.java
│   │   │   └── dto/                 # Data Transfer Objects
│   │   │       ├── ChuongTrinhKhuyenMaiDTO.java
│   │   │       ├── ChuongTrinhKhuyenMaiRequest.java
│   │   │       └── ApiResponse.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── script.sql                       # Database schema (legacy, use SetupDatabaseSQL.sql)
├── promotion_schema.sql             # Promotion tables schema (legacy)
├── pom.xml                          # Maven configuration
├── README.md                        # This file
├── QUICKSTART.md                    # Quick start guide
├── PROMOTION_README.md              # Detailed documentation
├── IMPLEMENTATION_SUMMARY.md        # Implementation summary
├── postman_collection.json          # Postman collection
└── example_frontend.html            # Frontend demo
```

## 🎯 Tính Năng Chính

### 1. Quản Lý Chương Trình Khuyến Mại

- ✅ Tạo, sửa, xóa chương trình khuyến mại
- ✅ Hỗ trợ 2 loại: Giảm giá hóa đơn & Giảm giá sản phẩm
- ✅ Giảm theo % hoặc theo tiền
- ✅ Thiết lập điều kiện áp dụng
- ✅ Quản lý trạng thái (Hoạt động/Ngừng/Sắp diễn ra/Đã kết thúc)

### 2. Điều Kiện Áp Dụng

- ✅ Thời gian: Ngày bắt đầu/kết thúc, Giờ, Ngày trong tuần/tháng
- ✅ Khách hàng: Tất cả/Nhóm/Cụ thể
- ✅ Đơn hàng tối thiểu
- ✅ Giảm tối đa (khi giảm theo %)
- ✅ Tự động áp dụng

### 3. Tính Toán Giảm Giá

- ✅ Tính giảm giá cho hóa đơn
- ✅ Tính giảm giá cho sản phẩm
- ✅ Kiểm tra điều kiện áp dụng
- ✅ Lưu lịch sử áp dụng

## 🔌 API Endpoints

### Base URL: `http://localhost:8888/api/chuong-trinh-khuyen-mai`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/` | Lấy tất cả chương trình |
| GET | `/active` | Lấy chương trình đang hoạt động |
| GET | `/type/{loaiKhuyenMai}` | Lấy theo loại (1=Hóa đơn, 2=Sản phẩm) |
| GET | `/{id}` | Lấy chi tiết theo ID |
| GET | `/code/{maChuongTrinh}` | Lấy theo mã chương trình |
| POST | `/` | Tạo mới chương trình |
| PUT | `/{id}` | Cập nhật chương trình |
| DELETE | `/{id}` | Xóa chương trình |
| PATCH | `/{id}/status` | Cập nhật trạng thái |
| GET | `/applicable/invoice` | Lấy CTKM áp dụng cho hóa đơn |
| GET | `/applicable/product/{id}` | Lấy CTKM áp dụng cho sản phẩm |
| GET | `/{id}/calculate-discount` | Tính toán giảm giá |

## 📊 Database Schema

### Bảng Chính

1. **Chuong_trinh_khuyen_mai** - Thông tin chương trình khuyến mại
2. **Chuong_trinh_khuyen_mai_chi_tiet** - Chi tiết sản phẩm áp dụng
3. **Lich_su_ap_dung_khuyen_mai** - Lịch sử áp dụng

### Relationships

```
Chuong_trinh_khuyen_mai
    ├── 1:N → Chuong_trinh_khuyen_mai_chi_tiet
    │           └── N:1 → SanPham
    │           └── N:1 → Danh_muc_san_pham
    └── 1:N → Lich_su_ap_dung_khuyen_mai
                └── N:1 → HoaDon
```

## 🧪 Testing

### 1. Test Bằng Postman

```bash
# Import file postman_collection.json vào Postman
# Chạy các request theo thứ tự
```

### 2. Test Bằng cURL

```bash
# Lấy danh sách
curl http://localhost:8888/api/chuong-trinh-khuyen-mai

# Tạo mới
curl -X POST http://localhost:8888/api/chuong-trinh-khuyen-mai \
  -H "Content-Type: application/json" \
  -d '{"maChuongTrinh":"CTKM001","tenChuongTrinh":"Test",...}'
```

### 3. Test Bằng Frontend Demo

```bash
# Mở file example_frontend.html trong browser
# Đảm bảo backend đang chạy tại localhost:8888
```

## 🎨 Tích Hợp Frontend

### JavaScript/Fetch Example

```javascript
// Lấy danh sách
fetch('http://localhost:8888/api/chuong-trinh-khuyen-mai')
  .then(response => response.json())
  .then(data => console.log(data));

// Tạo mới
fetch('http://localhost:8888/api/chuong-trinh-khuyen-mai', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    maChuongTrinh: 'CTKM001',
    tenChuongTrinh: 'Giảm giá hóa đơn',
    loaiKhuyenMai: 1,
    loaiGiam: 1,
    giaTriGiam: 10,
    ngayBatDau: '2026-03-01T00:00:00',
    ngayKetThuc: '2026-03-31T23:59:59',
    trangThai: 1
  })
})
  .then(response => response.json())
  .then(data => console.log(data));
```

### React/Vue/Angular

Xem chi tiết trong [QUICKSTART.md](./QUICKSTART.md)

## 🔧 Configuration

### application.properties

```properties
# Database
spring.datasource.url=jdbc:sqlserver://127.0.0.1:1433;databaseName=sd50
spring.datasource.username=sa
spring.datasource.password=123

# JPA
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true

# Server
server.port=8888
```

## 🐛 Troubleshooting

### Lỗi Thường Gặp

1. **Cannot connect to database**
   - Kiểm tra SQL Server đang chạy
   - Kiểm tra username/password
   - Kiểm tra port 1433

2. **Port 8888 already in use**
   - Đổi port trong application.properties
   - Hoặc kill process đang dùng port 8888

3. **Table not found**
   - Chạy script.sql
   - Chạy promotion_schema.sql

4. **Validation failed**
   - Kiểm tra format dữ liệu
   - Xem chi tiết trong PROMOTION_README.md

## 📈 Performance Tips

- Sử dụng index cho các trường tìm kiếm thường xuyên
- Cache danh sách chương trình đang hoạt động
- Lazy loading cho relationships
- Query optimization với JPA

## 🔐 Security (Có thể thêm)

- [ ] Spring Security
- [ ] JWT Authentication
- [ ] Role-based access control
- [ ] API rate limiting
- [ ] Input validation & sanitization

## 📝 TODO List

### Đã Hoàn Thành ✅

- [x] Database schema
- [x] Entity classes
- [x] Repository layer
- [x] Service layer
- [x] REST API
- [x] Validation
- [x] Documentation
- [x] Postman collection
- [x] Frontend demo

### Có Thể Thêm 🔜

- [ ] Authentication & Authorization
- [ ] Unit & Integration tests
- [ ] Swagger/OpenAPI docs
- [ ] Caching layer
- [ ] Logging & Monitoring
- [ ] Scheduled jobs
- [ ] Email notifications
- [ ] Export/Import features
- [ ] Reports & Statistics

## 👥 Contributors

- Development Team

## 📄 License

This project is licensed under the MIT License.

## 📞 Support

Nếu có vấn đề hoặc câu hỏi:

1. Đọc tài liệu trong thư mục docs
2. Kiểm tra Troubleshooting section
3. Liên hệ team phát triển

## 🎉 Acknowledgments

- Spring Boot
- JPA/Hibernate
- SQL Server
- Maven

---

**Version:** 1.1.0  
**Last Updated:** 2026-03-19  
**Status:** ✅ Production Ready

**Happy Coding! 🚀**
