package com.example.sd50datn.Service;

import com.example.sd50datn.Entity.DanhMucSanPham;
import com.example.sd50datn.Repository.DanhMucSanPhamRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DanhMucSanPhamService {

    private final DanhMucSanPhamRepository repo;

    public DanhMucSanPhamService(DanhMucSanPhamRepository repo) {
        this.repo = repo;
    }

    public List<DanhMucSanPham> getAll() {
        return repo.findAll();
    }

    public DanhMucSanPham getById(Integer id) {
        return repo.findById(id).orElse(null);
    }
    public List<DanhMucSanPham> search(String q) {
        if (q == null || q.trim().isEmpty()) {
            return repo.findAll();
        }
        return repo.findByTenDanhMucContainingIgnoreCase(q.trim());
    }

    public DanhMucSanPham save(DanhMucSanPham dm) {
        if (dm.getDanhMucSanPhamId() == null) {
            dm.setNgayTao(LocalDate.now());
        }
        dm.setNgayCapNhat(LocalDate.now());
        return repo.save(dm);
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }
    public boolean isTenDanhMucExistsForSave(DanhMucSanPham dm) {
        if (dm.getDanhMucSanPhamId() == null) {
            return repo.existsByTenDanhMuc(dm.getTenDanhMuc());
        }
        return repo.existsByTenDanhMucAndDanhMucSanPhamIdNot(
                dm.getTenDanhMuc(),
                dm.getDanhMucSanPhamId()
        );
    }
}