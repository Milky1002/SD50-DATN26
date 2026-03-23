package com.example.sd50datn.Service;

import com.example.sd50datn.Entity.DanhMucSanPham;
import com.example.sd50datn.Entity.SanPham;
import com.example.sd50datn.Entity.TrangChuDanhMucNoiBat;
import com.example.sd50datn.Entity.TrangChuSanPhamHot;
import com.example.sd50datn.Repository.TrangChuDanhMucNoiBatRepository;
import com.example.sd50datn.Repository.TrangChuSanPhamHotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomepageConfigService {

    private final TrangChuSanPhamHotRepository hotRepository;
    private final TrangChuDanhMucNoiBatRepository categoryRepository;
    private final SanPhamService sanPhamService;
    private final DanhMucSanPhamService danhMucSanPhamService;

    public List<TrangChuSanPhamHot> getHotConfigs() {
        return hotRepository.findActiveWithSanPham(1);
    }

    public List<TrangChuDanhMucNoiBat> getFeaturedCategoryConfigs() {
        return categoryRepository.findActiveWithDanhMuc(1);
    }

    public List<SanPham> getHotProducts() {
        return getHotConfigs().stream()
                .map(TrangChuSanPhamHot::getSanPham)
                .filter(sp -> sp != null && Integer.valueOf(1).equals(sp.getTrangThai()))
                .collect(Collectors.toList());
    }

    public List<DanhMucSanPham> getHomepageCategories() {
        return getFeaturedCategoryConfigs().stream()
                .map(TrangChuDanhMucNoiBat::getDanhMucSanPham)
                .filter(dm -> dm != null && Integer.valueOf(1).equals(dm.getTrangThai()))
                .collect(Collectors.toList());
    }

    public Map<Integer, List<SanPham>> getLatestProductsByConfiguredCategory() {
        Map<Integer, List<SanPham>> result = new LinkedHashMap<>();
        for (TrangChuDanhMucNoiBat config : getFeaturedCategoryConfigs()) {
            DanhMucSanPham danhMuc = config.getDanhMucSanPham();
            if (danhMuc == null || !Integer.valueOf(1).equals(danhMuc.getTrangThai())) {
                continue;
            }
            List<SanPham> products = sanPhamService.getLatestProductsByCategory(danhMuc.getDanhMucSanPhamId(), config.getSoLuongHienThi());
            result.put(danhMuc.getDanhMucSanPhamId(), products);
        }
        return result;
    }

    @Transactional
    public void saveHotProducts(List<Integer> sanPhamIds) {
        hotRepository.deleteAll();
        if (sanPhamIds == null) {
            return;
        }
        Set<Integer> uniqueIds = new LinkedHashSet<>(sanPhamIds);
        List<TrangChuSanPhamHot> configs = new ArrayList<>();
        int order = 1;
        for (Integer sanPhamId : uniqueIds) {
            SanPham sp = sanPhamService.getById(sanPhamId);
            if (sp == null) {
                continue;
            }
            configs.add(TrangChuSanPhamHot.builder()
                    .sanPham(sp)
                    .thuTu(order++)
                    .trangThai(1)
                    .build());
        }
        hotRepository.saveAll(configs);
    }

    @Transactional
    public void saveFeaturedCategories(List<Integer> danhMucIds) {
        categoryRepository.deleteAll();
        if (danhMucIds == null) {
            return;
        }
        Set<Integer> uniqueIds = new LinkedHashSet<>(danhMucIds);
        List<TrangChuDanhMucNoiBat> configs = new ArrayList<>();
        int order = 1;
        for (Integer danhMucId : uniqueIds) {
            DanhMucSanPham danhMuc = danhMucSanPhamService.getById(danhMucId);
            if (danhMuc == null) {
                continue;
            }
            configs.add(TrangChuDanhMucNoiBat.builder()
                    .danhMucSanPham(danhMuc)
                    .thuTu(order++)
                    .soLuongHienThi(20)
                    .trangThai(1)
                    .build());
        }
        categoryRepository.saveAll(configs);
    }
}
