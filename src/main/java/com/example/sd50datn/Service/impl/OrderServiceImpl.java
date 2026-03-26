package com.example.sd50datn.Service.impl;


import com.example.sd50datn.Dto.OrderSummaryDTO;
import com.example.sd50datn.Entity.HoaDonChiTiet;
import com.example.sd50datn.Entity.SanPham;
import com.example.sd50datn.Repository.HoaDonChiTietRepository;
import com.example.sd50datn.Repository.OrderRepository;
import com.example.sd50datn.Repository.SanPhamRepository;
import com.example.sd50datn.Service.OrderService;
import com.example.sd50datn.Util.OrderStatusUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;


import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final SanPhamRepository sanPhamRepository;

    @Override
    public List<OrderSummaryDTO> getOrderSummaries() {
        log.info("Bắt đầu lấy danh sách đơn hàng"); // Log đơn giản, hiệu quả
        try {
            List<OrderSummaryDTO> result = orderRepository.fetchOrderSummaries();
            log.info("Lấy thành công, kích thước: {}", result != null ? result.size() : 0);
            return result;
        } catch (Exception ex) {
            log.error("Lỗi khi lấy danh sách đơn hàng: {}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void deleteOrder(Integer id) {
        if (id == null) {
            return;
        }
        orderRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateOrderStatus(Integer id, Integer status) {
        if (id == null || status == null) {
            return;
        }
        orderRepository.findById(id).ifPresent(order -> {
            Integer previousStatus = order.getTrangThai();
            if (!OrderStatusUtil.isValidTransition(previousStatus, status)) {
                throw new IllegalStateException(
                        "Không thể chuyển trạng thái từ '" + OrderStatusUtil.getLabel(previousStatus)
                                + "' sang '" + OrderStatusUtil.getLabel(status) + "'");
            }

            if (status == OrderStatusUtil.DA_HUY) {
                restoreStock(order.getId());
            }

            order.setTrangThai(status);
            orderRepository.save(order);
        });
    }

    private void restoreStock(Integer orderId) {
        List<HoaDonChiTiet> orderItems = hoaDonChiTietRepository.findByHoaDonId(orderId);
        for (HoaDonChiTiet item : orderItems) {
            SanPham sanPham = item.getSanPham();
            if (sanPham == null || item.getSoLuongSanPham() == null) {
                continue;
            }
            Integer currentStock = sanPham.getSoLuongTon() != null ? sanPham.getSoLuongTon() : 0;
            sanPham.setSoLuongTon(currentStock + item.getSoLuongSanPham());
            sanPhamRepository.save(sanPham);
        }
    }
}

