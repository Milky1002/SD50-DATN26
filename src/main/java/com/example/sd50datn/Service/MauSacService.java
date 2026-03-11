package com.example.sd50datn.Service;

import com.example.sd50datn.Entity.MauSac;
import com.example.sd50datn.Repository.MauSacRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MauSacService {

    private final MauSacRepository repository;

    public MauSacService(MauSacRepository repository) {
        this.repository = repository;
    }

    public List<MauSac> getAll() {
        return repository.findAll();
    }

    public List<MauSac> search(String q) {
        if (q == null || q.trim().isEmpty()) {
            return repository.findAll();
        }
        return repository.findByTenMauContainingIgnoreCase(q.trim());
    }

    public MauSac getById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public MauSac save(MauSac mauSac) {
        if (mauSac.getMauSacId() == null) {
            mauSac.setNgayTao(LocalDateTime.now());
        }
        mauSac.setNgayCapNhat(LocalDateTime.now());
        return repository.save(mauSac);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public boolean isTenMauExistsForSave(MauSac mauSac) {
        if (mauSac.getMauSacId() == null) {
            return repository.existsByTenMau(mauSac.getTenMau());
        }
        return repository.existsByTenMauAndMauSacIdNot(mauSac.getTenMau(), mauSac.getMauSacId());
    }
}