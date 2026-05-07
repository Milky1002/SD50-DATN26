package com.example.sd50datn.Service;

import com.example.sd50datn.Entity.SanPham;
import com.example.sd50datn.Model.DashboardViewModel;
import com.example.sd50datn.Repository.DashboardRepository;

import java.time.LocalDate;
import java.util.List;

public interface DashboardService {

    DashboardViewModel getDashboardView();

    List<DashboardRepository.TopSellingProjection> getTopSellingProducts(LocalDate from, LocalDate to);

    List<SanPham> getLowStockProducts(int threshold);
}
