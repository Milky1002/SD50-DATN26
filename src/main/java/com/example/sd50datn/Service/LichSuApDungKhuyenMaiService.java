package com.example.sd50datn.Service;

import com.example.sd50datn.Dto.LichSuApDungKhuyenMaiDTO;
import com.example.sd50datn.Entity.LichSuApDungKhuyenMai;
import com.example.sd50datn.Repository.LichSuApDungKhuyenMaiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LichSuApDungKhuyenMaiService {

    private final LichSuApDungKhuyenMaiRepository repository;

    public List<LichSuApDungKhuyenMaiDTO> getAll() {
        return repository.findAllWithDetails().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<LichSuApDungKhuyenMaiDTO> getByPromotionId(Integer promotionId) {
        return repository.findByChuongTrinhKhuyenMaiId(promotionId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<LichSuApDungKhuyenMaiDTO> getWithFilters(Integer promotionId, Integer hoaDonId,
                                                          LocalDateTime fromDate, LocalDateTime toDate) {
        return repository.findWithFilters(promotionId, hoaDonId, fromDate, toDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public LichSuApDungKhuyenMaiDTO getById(Integer id) {
        LichSuApDungKhuyenMai entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch sử áp dụng với ID: " + id));
        return convertToDTO(entity);
    }

    private LichSuApDungKhuyenMaiDTO convertToDTO(LichSuApDungKhuyenMai entity) {
        LichSuApDungKhuyenMaiDTO dto = new LichSuApDungKhuyenMaiDTO();
        dto.setId(entity.getId());
        dto.setGiaTriGiam(entity.getGiaTriGiam());
        dto.setNgayApDung(entity.getNgayApDung());

        if (entity.getChuongTrinhKhuyenMai() != null) {
            dto.setChuongTrinhKhuyenMaiId(entity.getChuongTrinhKhuyenMai().getId());
            dto.setMaChuongTrinh(entity.getChuongTrinhKhuyenMai().getMaChuongTrinh());
            dto.setTenChuongTrinh(entity.getChuongTrinhKhuyenMai().getTenChuongTrinh());
            dto.setLoaiKhuyenMai(entity.getChuongTrinhKhuyenMai().getLoaiKhuyenMai());
        } else {
            dto.setChuongTrinhKhuyenMaiId(entity.getChuongTrinhKhuyenMaiId());
        }

        if (entity.getHoaDon() != null) {
            dto.setHoaDonId(entity.getHoaDon().getId());
            dto.setTenKhachHang(entity.getHoaDon().getTenKhachHang());
            dto.setTongTienHoaDon(entity.getHoaDon().getTongTienSauKhiGiam());
        } else {
            dto.setHoaDonId(entity.getHoaDonId());
        }

        return dto;
    }
}
