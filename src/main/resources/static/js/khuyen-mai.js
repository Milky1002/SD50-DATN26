// API Base URL
const API_BASE_URL = '/api/chuong-trinh-khuyen-mai';

// Global variables
let currentPage = 1;
let pageSize = 10;
let totalRecords = 0;
let allPromotionsFull = [];
let filteredPromotions = [];
let selectedPromoType = null;

// History tab variables
let historyCurrentPage = 1;
let historyPageSize = 10;
let historyTotalRecords = 0;
let allHistory = [];
let historyLoaded = false;

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

    // Load history data on first switch to history tab
    if (tabId === 'lich-su' && !historyLoaded) {
        populateHistoryPromotionFilter();
        loadHistory();
        historyLoaded = true;
    }
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
    document.getElementById('step1').classList.remove('active');
    document.getElementById('step2').classList.add('active');
    document.getElementById('btnBack').style.display = 'none';
    document.getElementById('btnSave').style.display = 'inline-block';
    resetForm();
    selectedPromoType = 1;
    document.getElementById('loaiKhuyenMai').value = 1;
}

// Close modal
function closeModal(modalId) {
    document.getElementById(modalId).classList.remove('active');
    resetForm();
}

// Select promo type
function selectPromoType(type) {
    if (type !== 1) {
        return;
    }
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
    document.getElementById('step1').classList.remove('active');
    document.getElementById('step2').classList.add('active');
    document.getElementById('btnBack').style.display = 'none';
    document.getElementById('btnSave').style.display = 'inline-block';
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
        loaiKhuyenMai: 1,
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
    selectedPromoType = 1;
    document.getElementById('loaiKhuyenMai').value = 1;
    document.querySelectorAll('.promo-type-card').forEach(card => {
        card.classList.remove('selected');
    });
    const invoiceCard = document.querySelector('.promo-type-card');
    if (invoiceCard) {
        invoiceCard.classList.add('selected');
    }
    toggleGiamToiDa();
}

// Helper functions
function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('vi-VN');
}

function getLoaiKhuyenMaiText(loai) {
    const types = {
        1: 'Giảm giá hóa đơn'
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

// ======================== HISTORY TAB FUNCTIONS ========================

// Populate promotion filter dropdown for history tab
function populateHistoryPromotionFilter() {
    const select = document.getElementById('historyFilterPromotion');
    if (!select) return;
    select.innerHTML = '<option value="">Tất cả CTKM</option>';
    allPromotionsFull.forEach(promo => {
        const option = document.createElement('option');
        option.value = promo.id;
        option.textContent = promo.maChuongTrinh + ' - ' + promo.tenChuongTrinh;
        select.appendChild(option);
    });
}

// Load history from API
async function loadHistory() {
    const tbody = document.getElementById('historyTableBody');
    if (!tbody) return;

    tbody.innerHTML = '<tr><td colspan="9" class="text-center"><div class="loading">Đang tải dữ liệu...</div></td></tr>';

    try {
        const promotionId = document.getElementById('historyFilterPromotion')?.value || '';
        const fromDate = document.getElementById('historyFromDate')?.value || '';
        const toDate = document.getElementById('historyToDate')?.value || '';

        let url = `${API_BASE_URL}/history?`;
        const params = [];
        if (promotionId) params.push(`promotionId=${promotionId}`);
        if (fromDate) params.push(`fromDate=${fromDate}`);
        if (toDate) params.push(`toDate=${toDate}`);
        url += params.join('&');

        const response = await fetch(url);
        const result = await response.json();

        if (result.success) {
            allHistory = result.data || [];
            historyTotalRecords = allHistory.length;
            historyCurrentPage = 1;
            renderHistoryTable();
            updateHistoryPagination();
        } else {
            tbody.innerHTML = '<tr><td colspan="9" class="text-center">Lỗi: ' + result.message + '</td></tr>';
        }
    } catch (error) {
        tbody.innerHTML = '<tr><td colspan="9" class="text-center">Lỗi kết nối: ' + error.message + '</td></tr>';
    }
}

// Render history table
function renderHistoryTable() {
    const tbody = document.getElementById('historyTableBody');
    if (!tbody) return;

    if (!allHistory || allHistory.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" class="text-center"><div class="loading">Chưa có lịch sử áp dụng</div></td></tr>';
        return;
    }

    const start = (historyCurrentPage - 1) * historyPageSize;
    const end = start + historyPageSize;
    const pageData = allHistory.slice(start, end);

    tbody.innerHTML = pageData.map((item, index) => `
        <tr>
            <td>${start + index + 1}</td>
            <td>${item.maChuongTrinh || '-'}</td>
            <td>${item.tenChuongTrinh || '-'}</td>
            <td>${getLoaiKhuyenMaiText(item.loaiKhuyenMai)}</td>
            <td><a href="javascript:void(0)" onclick="viewHistoryDetail(${item.id})" class="link-primary">HD-${item.hoaDonId}</a></td>
            <td>${item.tenKhachHang || '-'}</td>
            <td class="text-right text-danger">${formatCurrency(item.giaTriGiam)}</td>
            <td>${formatDateTime(item.ngayApDung)}</td>
            <td>
                <div class="action-btns">
                    <button class="btn-sm btn-view" onclick="viewHistoryDetail(${item.id})" title="Xem chi tiết">
                        <i class="bi bi-eye"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

// Update history pagination
function updateHistoryPagination() {
    const start = (historyCurrentPage - 1) * historyPageSize + 1;
    const end = Math.min(historyCurrentPage * historyPageSize, historyTotalRecords);

    const fromEl = document.getElementById('historyShowingFrom');
    const toEl = document.getElementById('historyShowingTo');
    const totalEl = document.getElementById('historyTotalRecords');
    const pageInfoEl = document.getElementById('historyPageInfo');
    const prevBtn = document.getElementById('btnHistoryPrevPage');
    const nextBtn = document.getElementById('btnHistoryNextPage');

    if (fromEl) fromEl.textContent = historyTotalRecords > 0 ? start : 0;
    if (toEl) toEl.textContent = end;
    if (totalEl) totalEl.textContent = historyTotalRecords;
    if (pageInfoEl) pageInfoEl.textContent = `Trang ${historyCurrentPage}`;
    if (prevBtn) prevBtn.disabled = historyCurrentPage === 1;
    if (nextBtn) nextBtn.disabled = end >= historyTotalRecords;
}

// History pagination buttons
function initHistoryPagination() {
    const prevBtn = document.getElementById('btnHistoryPrevPage');
    const nextBtn = document.getElementById('btnHistoryNextPage');
    if (prevBtn) prevBtn.addEventListener('click', () => changeHistoryPage(-1));
    if (nextBtn) nextBtn.addEventListener('click', () => changeHistoryPage(1));
}

function changeHistoryPage(direction) {
    const totalPages = Math.ceil(historyTotalRecords / historyPageSize);
    const newPage = historyCurrentPage + direction;
    if (newPage >= 1 && newPage <= totalPages) {
        historyCurrentPage = newPage;
        renderHistoryTable();
        updateHistoryPagination();
    }
}

// Reset history filters
function resetHistoryFilters() {
    const promoFilter = document.getElementById('historyFilterPromotion');
    const fromDate = document.getElementById('historyFromDate');
    const toDate = document.getElementById('historyToDate');
    if (promoFilter) promoFilter.value = '';
    if (fromDate) fromDate.value = '';
    if (toDate) toDate.value = '';
    loadHistory();
}

// View history detail
async function viewHistoryDetail(id) {
    const modal = document.getElementById('modalHistoryDetail');
    const content = document.getElementById('historyDetailContent');
    if (!modal || !content) return;

    modal.classList.add('active');
    content.innerHTML = '<p>Đang tải chi tiết...</p>';

    try {
        const response = await fetch(`${API_BASE_URL}/history/${id}`);
        const result = await response.json();

        if (result.success) {
            const item = result.data;
            content.innerHTML = `
                <div class="detail-grid">
                    <div class="detail-section">
                        <h4>Thông tin khuyến mại</h4>
                        <div class="detail-row">
                            <span class="detail-label">Mã CTKM:</span>
                            <span class="detail-value">${item.maChuongTrinh || '-'}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Tên CTKM:</span>
                            <span class="detail-value">${item.tenChuongTrinh || '-'}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Loại:</span>
                            <span class="detail-value">${getLoaiKhuyenMaiText(item.loaiKhuyenMai)}</span>
                        </div>
                    </div>
                    <div class="detail-section">
                        <h4>Thông tin hóa đơn</h4>
                        <div class="detail-row">
                            <span class="detail-label">Mã hóa đơn:</span>
                            <span class="detail-value">HD-${item.hoaDonId}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Khách hàng:</span>
                            <span class="detail-value">${item.tenKhachHang || '-'}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Tổng tiền hóa đơn:</span>
                            <span class="detail-value">${formatCurrency(item.tongTienHoaDon)}</span>
                        </div>
                    </div>
                    <div class="detail-section detail-full">
                        <h4>Chi tiết áp dụng</h4>
                        <div class="detail-row">
                            <span class="detail-label">Giá trị giảm:</span>
                            <span class="detail-value text-danger" style="font-weight:700;font-size:1.1em">-${formatCurrency(item.giaTriGiam)}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Ngày áp dụng:</span>
                            <span class="detail-value">${formatDateTime(item.ngayApDung)}</span>
                        </div>
                    </div>
                </div>
            `;
        } else {
            content.innerHTML = '<p class="text-danger">Lỗi: ' + result.message + '</p>';
        }
    } catch (error) {
        content.innerHTML = '<p class="text-danger">Lỗi kết nối: ' + error.message + '</p>';
    }
}

// Format currency
function formatCurrency(value) {
    if (value == null) return '0 ₫';
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
}

// Format datetime
function formatDateTime(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' });
}

// Initialize history pagination on page load
document.addEventListener('DOMContentLoaded', function() {
    initHistoryPagination();
});
