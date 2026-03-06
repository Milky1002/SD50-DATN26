// API Base URL
const API_BASE_URL = 'http://localhost:8888/api/chuong-trinh-khuyen-mai';

// Global variables
let currentPage = 1;
let pageSize = 10;
let totalRecords = 0;
let allPromotionsFull = [];
let filteredPromotions = [];
let selectedPromoType = null;

// Load data when page loads
document.addEventListener('DOMContentLoaded', function() {
    loadPromotions();
    initEventListeners();
});

// Initialize event listeners
function initEventListeners() {
    // Tab switching
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const tabId = this.getAttribute('data-tab');
            switchTab(tabId);
        });
    });

    // Search
    document.getElementById('searchInput').addEventListener('input', debounce(filterTable, 300));
    
    // Filters
    document.getElementById('filterLoai').addEventListener('change', filterTable);

    // Pagination
    document.getElementById('btnPrevPage').addEventListener('click', () => changePage(-1));
    document.getElementById('btnNextPage').addEventListener('click', () => changePage(1));

    // Select all checkbox
    document.getElementById('selectAll').addEventListener('change', function() {
        document.querySelectorAll('.row-checkbox').forEach(cb => {
            cb.checked = this.checked;
        });
    });
}

// Switch tabs
function switchTab(tabId) {
    // Update tab buttons
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    document.querySelector(`[data-tab="${tabId}"]`).classList.add('active');

    // Update tab content
    document.querySelectorAll('.tab-content').forEach(content => {
        content.classList.remove('active');
    });
    document.getElementById(`tab-${tabId}`).classList.add('active');
}

// Load promotions from API
async function loadPromotions() {
    try {
        const response = await fetch(API_BASE_URL);
        const result = await response.json();

        if (result.success) {
            // Lưu toàn bộ CTKM (bao gồm cả hoạt động và ngừng) để vẫn xem được lịch sử
            allPromotionsFull = result.data || [];
            applyFilters();
        } else {
            showError('Không thể tải dữ liệu: ' + result.message);
        }
    } catch (error) {
        showError('Lỗi kết nối: ' + error.message);
    }
}

// Render table
function renderTable() {
    const tbody = document.getElementById('tableBody');
    
    if (!filteredPromotions || filteredPromotions.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="10" class="text-center">
                    <div class="loading">Không có dữ liệu</div>
                </td>
            </tr>
        `;
        return;
    }

    const start = (currentPage - 1) * pageSize;
    const end = start + pageSize;
    const pageData = filteredPromotions.slice(start, end);

    tbody.innerHTML = pageData.map(promo => `
        <tr>
            <td><input type="checkbox" class="row-checkbox" value="${promo.id}" /></td>
            <td>${promo.maChuongTrinh}</td>
            <td>${promo.tenChuongTrinh}</td>
            <td>${getKhachHangText(promo.khachHangApDung)}</td>
            <td>${formatDate(promo.ngayBatDau)}</td>
            <td>${formatDate(promo.ngayKetThuc)}</td>
            <td>${getLoaiKhuyenMaiText(promo.loaiKhuyenMai)}</td>
            <td>${promo.moTa || '-'}</td>
            <td>${getTrangThaiHTML(promo.trangThai)}</td>
            <td>
                <div class="action-btns">
                    <button class="btn-sm btn-edit" onclick="editPromotion(${promo.id})" title="Sửa">✏️</button>
                    ${
                        promo.trangThai === 1
                            ? `<button class="btn-sm btn-stop" onclick="updateStatus(${promo.id}, 0)" title="Ngừng áp dụng">⏸</button>`
                            : `<button class="btn-sm btn-activate" onclick="updateStatus(${promo.id}, 1)" title="Kích hoạt lại">▶️</button>`
                    }
                    <button class="btn-sm btn-delete" onclick="deletePromotion(${promo.id})" title="Xóa">🗑️</button>
                </div>
            </td>
        </tr>
    `).join('');
}

// Áp dụng filter dựa trên dữ liệu gốc
function applyFilters() {
    const searchText = document.getElementById('searchInput').value.toLowerCase();
    const filterLoai = document.getElementById('filterLoai').value;

    let filtered = [...allPromotionsFull];

    // Search filter
    if (searchText) {
        filtered = filtered.filter(promo =>
            promo.maChuongTrinh.toLowerCase().includes(searchText) ||
            promo.tenChuongTrinh.toLowerCase().includes(searchText)
        );
    }

    // Loại filter
    if (filterLoai) {
        filtered = filtered.filter(promo => promo.loaiKhuyenMai == filterLoai);
    }

    filteredPromotions = filtered;
    totalRecords = filteredPromotions.length;
    currentPage = 1;
    renderTable();
    updatePagination();
}

// Filter table (wrapper cho event)
function filterTable() {
    applyFilters();
}

// Reset toàn bộ search + filter
function resetFilters() {
    const searchInput = document.getElementById('searchInput');
    const filterLoai = document.getElementById('filterLoai');

    if (searchInput) searchInput.value = '';
    if (filterLoai) filterLoai.value = '';

    applyFilters();
}

// Update pagination
function updatePagination() {
    const start = (currentPage - 1) * pageSize + 1;
    const end = Math.min(currentPage * pageSize, totalRecords);
    
    document.getElementById('showingFrom').textContent = totalRecords > 0 ? start : 0;
    document.getElementById('showingTo').textContent = end;
    document.getElementById('totalRecords').textContent = totalRecords;
    document.getElementById('pageInfo').textContent = `Trang ${currentPage}`;

    // Disable/enable buttons
    document.getElementById('btnPrevPage').disabled = currentPage === 1;
    document.getElementById('btnNextPage').disabled = end >= totalRecords;
}

// Change page
function changePage(direction) {
    const totalPages = Math.ceil(totalRecords / pageSize);
    const newPage = currentPage + direction;

    if (newPage >= 1 && newPage <= totalPages) {
        currentPage = newPage;
        renderTable();
        updatePagination();
    }
}

// Open add modal
function openAddModal() {
    document.getElementById('modalAddCTKM').classList.add('active');
    document.getElementById('step1').classList.add('active');
    document.getElementById('step2').classList.remove('active');
    document.getElementById('btnBack').style.display = 'none';
    document.getElementById('btnSave').style.display = 'none';
    resetForm();
}

// Close modal
function closeModal(modalId) {
    document.getElementById(modalId).classList.remove('active');
    resetForm();
}

// Select promo type
function selectPromoType(type) {
    selectedPromoType = type;
    document.getElementById('loaiKhuyenMai').value = type;
    
    // Visual feedback
    document.querySelectorAll('.promo-type-card').forEach(card => {
        card.classList.remove('selected');
    });
    event.target.closest('.promo-type-card').classList.add('selected');
    
    // Move to step 2
    setTimeout(() => {
        document.getElementById('step1').classList.remove('active');
        document.getElementById('step2').classList.add('active');
        document.getElementById('btnBack').style.display = 'inline-block';
        document.getElementById('btnSave').style.display = 'inline-block';
    }, 200);
}

// Back to step 1
function backToStep1() {
    document.getElementById('step2').classList.remove('active');
    document.getElementById('step1').classList.add('active');
    document.getElementById('btnBack').style.display = 'none';
    document.getElementById('btnSave').style.display = 'none';
}

// Toggle giảm tối đa field
function toggleGiamToiDa() {
    const loaiGiam = document.getElementById('loaiGiam').value;
    const groupGiamToiDa = document.getElementById('groupGiamToiDa');
    
    if (loaiGiam == '1') { // Theo %
        groupGiamToiDa.style.display = 'block';
    } else {
        groupGiamToiDa.style.display = 'none';
        document.getElementById('giamToiDa').value = '';
    }
}

// Save CTKM
async function saveCTKM() {
    const id = document.getElementById('ctkm_id').value;
    const isEdit = id !== '';

    // Get ngày trong tuần
    const ngayTuanCheckboxes = document.querySelectorAll('input[name="ngayTrongTuan"]:checked');
    const ngayTuan = Array.from(ngayTuanCheckboxes).map(cb => cb.value);

    const data = {
        maChuongTrinh: document.getElementById('maChuongTrinh').value || 'CTKM' + Date.now(),
        tenChuongTrinh: document.getElementById('tenChuongTrinh').value,
        moTa: document.getElementById('moTa').value,
        loaiKhuyenMai: parseInt(document.getElementById('loaiKhuyenMai').value),
        loaiGiam: parseInt(document.getElementById('loaiGiam').value),
        giaTriGiam: parseFloat(document.getElementById('giaTriGiam').value),
        giamToiDa: parseFloat(document.getElementById('giamToiDa').value) || null,
        donHangToiThieu: parseFloat(document.getElementById('donHangToiThieu').value) || null,
        ngayBatDau: document.getElementById('ngayBatDau').value + 'T' + (document.getElementById('gioBatDau').value || '00:00') + ':00',
        ngayKetThuc: document.getElementById('ngayKetThuc').value + 'T' + (document.getElementById('gioKetThuc').value || '23:59') + ':00',
        khachHangApDung: parseInt(document.getElementById('khachHangApDung').value),
        ngayTrongTuan: ngayTuan.length > 0 ? JSON.stringify(ngayTuan) : null,
        ngayTrongThang: document.getElementById('ngayTrongThang').value || null,
        apDungCungNhieuCtkm: document.getElementById('apDungCungNhieuCtkm').checked,
        tuDongApDung: document.getElementById('tuDongApDung').checked,
        tongLienHoaDonApDung: document.getElementById('tongLienHoaDon').checked ? 'true' : null,
        trangThai: 1
    };

    try {
        const url = isEdit ? `${API_BASE_URL}/${id}` : API_BASE_URL;
        const method = isEdit ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data)
        });

        const result = await response.json();

        if (result.success) {
            showSuccess(result.message);
            closeModal('modalAddCTKM');
            loadPromotions();
        } else {
            showError(result.message);
        }
    } catch (error) {
        showError('Lỗi kết nối: ' + error.message);
    }
}

// Edit promotion
async function editPromotion(id) {
    try {
        const response = await fetch(`${API_BASE_URL}/${id}`);
        const result = await response.json();

        if (result.success) {
            const promo = result.data;
            
            // Fill form
            document.getElementById('ctkm_id').value = promo.id;
            document.getElementById('maChuongTrinh').value = promo.maChuongTrinh;
            document.getElementById('tenChuongTrinh').value = promo.tenChuongTrinh;
            document.getElementById('moTa').value = promo.moTa || '';
            document.getElementById('loaiKhuyenMai').value = promo.loaiKhuyenMai;
            document.getElementById('loaiGiam').value = promo.loaiGiam;
            document.getElementById('giaTriGiam').value = promo.giaTriGiam;
            document.getElementById('giamToiDa').value = promo.giamToiDa || '';
            document.getElementById('donHangToiThieu').value = promo.donHangToiThieu || '';
            document.getElementById('ngayBatDau').value = promo.ngayBatDau.split('T')[0];
            document.getElementById('ngayKetThuc').value = promo.ngayKetThuc.split('T')[0];
            document.getElementById('gioBatDau').value = promo.gioBatDau || '';
            document.getElementById('gioKetThuc').value = promo.gioKetThuc || '';
            document.getElementById('khachHangApDung').value = promo.khachHangApDung;
            document.getElementById('apDungCungNhieuCtkm').checked = promo.apDungCungNhieuCtkm;
            document.getElementById('tuDongApDung').checked = promo.tuDongApDung;

            // Open modal at step 2
            document.getElementById('modalAddCTKM').classList.add('active');
            document.getElementById('step1').classList.remove('active');
            document.getElementById('step2').classList.add('active');
            document.getElementById('btnBack').style.display = 'none';
            document.getElementById('btnSave').style.display = 'inline-block';
            
            toggleGiamToiDa();
        }
    } catch (error) {
        showError('Lỗi khi tải dữ liệu: ' + error.message);
    }
}

// Delete promotion
async function deletePromotion(id) {
    if (!confirm('Bạn có chắc muốn xóa chương trình này?')) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/${id}`, {
            method: 'DELETE'
        });

        const result = await response.json();

        if (result.success) {
            showSuccess(result.message);
            loadPromotions();
        } else {
            showError(result.message);
        }
    } catch (error) {
        showError('Lỗi khi xóa: ' + error.message);
    }
}

// Cập nhật trạng thái (ngừng / kích hoạt lại)
async function updateStatus(id, newStatus) {
    const actionText = newStatus === 1 ? 'kích hoạt lại' : 'ngừng áp dụng';
    if (!confirm(`Bạn có chắc muốn ${actionText} chương trình này?`)) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/${id}/status?trangThai=${newStatus}`, {
            method: 'PATCH'
        });

        const result = await response.json();

        if (result.success) {
            showSuccess(result.message);
            loadPromotions();
        } else {
            showError(result.message);
        }
    } catch (error) {
        showError('Lỗi khi cập nhật trạng thái: ' + error.message);
    }
}

// Reset form
function resetForm() {
    document.getElementById('formCTKM').reset();
    document.getElementById('ctkm_id').value = '';
    selectedPromoType = null;
    document.querySelectorAll('.promo-type-card').forEach(card => {
        card.classList.remove('selected');
    });
}

// Helper functions
function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('vi-VN');
}

function getLoaiKhuyenMaiText(loai) {
    const types = {
        1: 'Giảm giá hóa đơn',
        2: 'Giảm giá sản phẩm',
        3: 'Tặng hàng',
        4: 'Đồng giá'
    };
    return types[loai] || '-';
}

function getKhachHangText(loai) {
    const types = {
        1: 'Tất cả',
        2: 'Bán tại cửa hàng',
        3: 'Người bán hàng'
    };
    return types[loai] || '-';
}

function getTrangThaiHTML(trangThai) {
    if (trangThai === 1) {
        return '<span class="badge badge-success">Hoạt động</span>';
    } else {
        return '<span class="badge badge-danger">Ngừng</span>';
    }
}

function showSuccess(message) {
    alert('✅ ' + message);
}

function showError(message) {
    alert('❌ ' + message);
}

function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}
