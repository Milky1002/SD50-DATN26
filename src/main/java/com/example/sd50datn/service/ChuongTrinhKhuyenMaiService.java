package com.example.sd50datn.Service;

import com.example.sd50datn.Dto.ChuongTrinhKhuyenMaiChiTietDTO;
import com.example.sd50datn.Dto.ChuongTrinhKhuyenMaiChiTietRequest;
import com.example.sd50datn.Dto.ChuongTrinhKhuyenMaiDTO;
import com.example.sd50datn.Dto.ChuongTrinhKhuyenMaiRequest;
import com.example.sd50datn.Entity.ChuongTrinhKhuyenMai;
import com.example.sd50datn.Entity.ChuongTrinhKhuyenMaiChiTiet;
import com.example.sd50datn.Entity.SanPham;
import com.example.sd50datn.Repository.ChuongTrinhKhuyenMaiChiTietRepository;
import com.example.sd50datn.Repository.ChuongTrinhKhuyenMaiRepository;
import com.example.sd50datn.Repository.SanPhamRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChuongTrinhKhuyenMaiService {

    private final ChuongTrinhKhuyenMaiRepository chuongTrinhKhuyenMaiRepository;
    private final ChuongTrinhKhuyenMaiChiTietRepository chiTietRepository;
    private final SanPhamRepository sanPhamRepository;

    public List<ChuongTrinhKhuyenMaiDTO> getAllPromotions() {
        return chuongTrinhKhuyenMaiRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ChuongTrinhKhuyenMaiDTO> getActivePromotions() {
        return chuongTrinhKhuyenMaiRepository.findActivePromotions(LocalDateTime.now())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ChuongTrinhKhuyenMaiDTO> getPromotionsByType(Integer loaiKhuyenMai) {
        return chuongTrinhKhuyenMaiRepository.findByLoaiKhuyenMai(loaiKhuyenMai)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ChuongTrinhKhuyenMaiDTO getPromotionById(Integer id) {
        ChuongTrinhKhuyenMai entity = chuongTrinhKhuyenMaiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương trình khuyến mại với ID: " + id));
        return convertToDTO(entity);
    }

    public ChuongTrinhKhuyenMaiDTO getPromotionByCode(String maChuongTrinh) {
        ChuongTrinhKhuyenMai entity = chuongTrinhKhuyenMaiRepository.findByMaChuongTrinh(maChuongTrinh)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương trình khuyến mại với mã: " + maChuongTrinh));
        return convertToDTO(entity);
    }

    @Transactional
    public ChuongTrinhKhuyenMaiDTO createPromotion(ChuongTrinhKhuyenMaiRequest request) {
        if (chuongTrinhKhuyenMaiRepository.existsByMaChuongTrinh(request.getMaChuongTrinh())) {
            throw new RuntimeException("Mã chương trình đã tồn tại: " + request.getMaChuongTrinh());
        }

        if (request.getNgayKetThuc().isBefore(request.getNgayBatDau())) {
            throw new RuntimeException("Ngày kết thúc phải sau ngày bắt đầu");
        }

        if (request.getLoaiGiam() == 1 && request.getGiaTriGiam().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new RuntimeException("Giá trị giảm theo % không được vượt quá 100%");
        }

        ChuongTrinhKhuyenMai entity = convertToEntity(request);
        entity = chuongTrinhKhuyenMaiRepository.save(entity);

        if (request.getChiTietList() != null && !request.getChiTietList().isEmpty()) {
            saveChiTietList(entity, request.getChiTietList());
        }

        return convertToDTO(entity);
    }

    @Transactional
    public ChuongTrinhKhuyenMaiDTO updatePromotion(Integer id, ChuongTrinhKhuyenMaiRequest request) {
        ChuongTrinhKhuyenMai entity = chuongTrinhKhuyenMaiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương trình khuyến mại với ID: " + id));

        if (!entity.getMaChuongTrinh().equals(request.getMaChuongTrinh()) &&
            chuongTrinhKhuyenMaiRepository.existsByMaChuongTrinh(request.getMaChuongTrinh())) {
            throw new RuntimeException("Mã chương trình đã tồn tại: " + request.getMaChuongTrinh());
        }

        if (request.getNgayKetThuc().isBefore(request.getNgayBatDau())) {
            throw new RuntimeException("Ngày kết thúc phải sau ngày bắt đầu");
        }

        updateEntityFromRequest(entity, request);
        entity = chuongTrinhKhuyenMaiRepository.save(entity);

        chiTietRepository.deleteByChuongTrinhKhuyenMaiId(id);
        if (request.getChiTietList() != null && !request.getChiTietList().isEmpty()) {
            saveChiTietList(entity, request.getChiTietList());
        }

        return convertToDTO(entity);
    }

    @Transactional
    public void deletePromotion(Integer id) {
        if (!chuongTrinhKhuyenMaiRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy chương trình khuyến mại với ID: " + id);
        }
        chuongTrinhKhuyenMaiRepository.deleteById(id);
    }

    @Transactional
    public ChuongTrinhKhuyenMaiDTO updatePromotionStatus(Integer id, Integer trangThai) {
        ChuongTrinhKhuyenMai entity = chuongTrinhKhuyenMaiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương trình khuyến mại với ID: " + id));
        
        entity.setTrangThai(trangThai);
        entity = chuongTrinhKhuyenMaiRepository.save(entity);
        
        return convertToDTO(entity);
    }

    public List<ChuongTrinhKhuyenMaiDTO> getApplicablePromotionsForInvoice(BigDecimal orderTotal) {
        List<ChuongTrinhKhuyenMai> activePromotions = chuongTrinhKhuyenMaiRepository
                .findActivePromotionsByType(LocalDateTime.now(), 1); // Loại 1: Giảm giá hóa đơn

        return activePromotions.stream()
                .filter(promo -> promo.getDonHangToiThieu() == null || 
                                orderTotal.compareTo(promo.getDonHangToiThieu()) >= 0)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ChuongTrinhKhuyenMaiDTO> getApplicablePromotionsForProduct(Integer sanPhamId) {
        List<ChuongTrinhKhuyenMai> activePromotions = chuongTrinhKhuyenMaiRepository
                .findActivePromotionsByType(LocalDateTime.now(), 2); // Loại 2: Giảm giá sản phẩm

        return activePromotions.stream()
                .filter(promo -> {
                    List<ChuongTrinhKhuyenMaiChiTiet> chiTietList = 
                        chiTietRepository.findApplicableDetailsForProduct(promo.getId(), sanPhamId);
                    return !chiTietList.isEmpty();
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BigDecimal calculateDiscountForInvoice(Integer promotionId, BigDecimal orderTotal) {
        ChuongTrinhKhuyenMai promotion = chuongTrinhKhuyenMaiRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương trình khuyến mại"));

        if (promotion.getLoaiKhuyenMai() != 1) {
            throw new RuntimeException("Chương trình này không áp dụng cho hóa đơn");
        }

        if (promotion.getDonHangToiThieu() != null && 
            orderTotal.compareTo(promotion.getDonHangToiThieu()) < 0) {
            throw new RuntimeException("Đơn hàng chưa đủ giá trị tối thiểu");
        }

        BigDecimal discount;
        if (promotion.getLoaiGiam() == 1) { // Giảm theo %
            discount = orderTotal.multiply(promotion.getGiaTriGiam()).divide(BigDecimal.valueOf(100));
            if (promotion.getGiamToiDa() != null && discount.compareTo(promotion.getGiamToiDa()) > 0) {
                discount = promotion.getGiamToiDa();
            }
        } else { // Giảm theo tiền
            discount = promotion.getGiaTriGiam();
        }

        return discount.min(orderTotal);
    }

    private void saveChiTietList(ChuongTrinhKhuyenMai chuongTrinh, 
                                  List<ChuongTrinhKhuyenMaiChiTietRequest> chiTietRequests) {
        for (ChuongTrinhKhuyenMaiChiTietRequest request : chiTietRequests) {
            ChuongTrinhKhuyenMaiChiTiet chiTiet = new ChuongTrinhKhuyenMaiChiTiet();
            chiTiet.setChuongTrinhKhuyenMai(chuongTrinh);
            
            if (request.getSanPhamId() != null) {
                SanPham sanPham = sanPhamRepository.findById(request.getSanPhamId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
                chiTiet.setSanPham(sanPham);
            }
            
            chiTiet.setSoLuongToiThieu(request.getSoLuongToiThieu());
            chiTiet.setSoLuongToiDa(request.getSoLuongToiDa());
            chiTiet.setGiaTriGiam(request.getGiaTriGiam());
            chiTiet.setTrangThai(request.getTrangThai());
            
            chiTietRepository.save(chiTiet);
        }
    }

    private ChuongTrinhKhuyenMai convertToEntity(ChuongTrinhKhuyenMaiRequest request) {
        ChuongTrinhKhuyenMai entity = new ChuongTrinhKhuyenMai();
        updateEntityFromRequest(entity, request);
        return entity;
    }

    private void updateEntityFromRequest(ChuongTrinhKhuyenMai entity, ChuongTrinhKhuyenMaiRequest request) {
        entity.setMaChuongTrinh(request.getMaChuongTrinh());
        entity.setTenChuongTrinh(request.getTenChuongTrinh());
        entity.setMoTa(request.getMoTa());
        entity.setLoaiKhuyenMai(request.getLoaiKhuyenMai());
        entity.setLoaiGiam(request.getLoaiGiam());
        entity.setGiaTriGiam(request.getGiaTriGiam());
        entity.setGiamToiDa(request.getGiamToiDa());
        entity.setDonHangToiThieu(request.getDonHangToiThieu());
        entity.setNgayBatDau(request.getNgayBatDau());
        entity.setNgayKetThuc(request.getNgayKetThuc());
        entity.setGioBatDau(request.getGioBatDau());
        entity.setGioKetThuc(request.getGioKetThuc());
        entity.setApDungCungNhieuCtkm(request.getApDungCungNhieuCtkm());
        entity.setTuDongApDung(request.getTuDongApDung());
        entity.setTongLienHoaDonApDung(request.getTongLienHoaDonApDung());
        entity.setNgayTrongTuan(request.getNgayTrongTuan());
        entity.setNgayTrongThang(request.getNgayTrongThang());
        entity.setKhachHangApDung(request.getKhachHangApDung());
        entity.setKenhBanApDung(request.getKenhBanApDung());
        entity.setTrangThai(request.getTrangThai());
    }

    private ChuongTrinhKhuyenMaiDTO convertToDTO(ChuongTrinhKhuyenMai entity) {
        ChuongTrinhKhuyenMaiDTO dto = new ChuongTrinhKhuyenMaiDTO();
        dto.setId(entity.getId());
        dto.setMaChuongTrinh(entity.getMaChuongTrinh());
        dto.setTenChuongTrinh(entity.getTenChuongTrinh());
        dto.setMoTa(entity.getMoTa());
        dto.setLoaiKhuyenMai(entity.getLoaiKhuyenMai());
        dto.setLoaiGiam(entity.getLoaiGiam());
        dto.setGiaTriGiam(entity.getGiaTriGiam());
        dto.setGiamToiDa(entity.getGiamToiDa());
        dto.setDonHangToiThieu(entity.getDonHangToiThieu());
        dto.setNgayBatDau(entity.getNgayBatDau());
        dto.setNgayKetThuc(entity.getNgayKetThuc());
        dto.setGioBatDau(entity.getGioBatDau());
        dto.setGioKetThuc(entity.getGioKetThuc());
        dto.setApDungCungNhieuCtkm(entity.getApDungCungNhieuCtkm());
        dto.setTuDongApDung(entity.getTuDongApDung());
        dto.setTongLienHoaDonApDung(entity.getTongLienHoaDonApDung());
        dto.setNgayTrongTuan(entity.getNgayTrongTuan());
        dto.setNgayTrongThang(entity.getNgayTrongThang());
        dto.setKhachHangApDung(entity.getKhachHangApDung());
        dto.setKenhBanApDung(entity.getKenhBanApDung());
        dto.setTrangThai(entity.getTrangThai());
        dto.setNgayTao(entity.getNgayTao());
        dto.setNgayCapNhat(entity.getNgayCapNhat());

        List<ChuongTrinhKhuyenMaiChiTiet> chiTietList = chiTietRepository.findByChuongTrinhKhuyenMaiId(entity.getId());
        dto.setChiTietList(chiTietList.stream().map(this::convertChiTietToDTO).collect(Collectors.toList()));

        return dto;
    }

    private ChuongTrinhKhuyenMaiChiTietDTO convertChiTietToDTO(ChuongTrinhKhuyenMaiChiTiet entity) {
        ChuongTrinhKhuyenMaiChiTietDTO dto = new ChuongTrinhKhuyenMaiChiTietDTO();
        dto.setId(entity.getId());
        dto.setChuongTrinhKhuyenMaiId(entity.getChuongTrinhKhuyenMai().getId());
        
        if (entity.getSanPham() != null) {
            dto.setSanPhamId(entity.getSanPham().getId());
            dto.setTenSanPham(entity.getSanPham().getTenSanPham());
            dto.setMaSanPham(entity.getSanPham().getMaSanPham());
        }
        
        if (entity.getDanhMucSanPham() != null) {
            dto.setDanhMucSanPhamId(entity.getDanhMucSanPham().getDanhMucSanPhamId());
            dto.setTenDanhMuc(entity.getDanhMucSanPham().getTenDanhMuc());
        }
        
        dto.setSoLuongToiThieu(entity.getSoLuongToiThieu());
        dto.setSoLuongToiDa(entity.getSoLuongToiDa());
        dto.setGiaTriGiam(entity.getGiaTriGiam());
        dto.setTrangThai(entity.getTrangThai());
        
        return dto;
    }
}
