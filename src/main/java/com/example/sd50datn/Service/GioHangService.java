package com.example.sd50datn.Service;

import com.example.sd50datn.Entity.GioHang;
import com.example.sd50datn.Entity.GioHangChiTiet;
import com.example.sd50datn.Entity.SanPham;
import com.example.sd50datn.Repository.GioHangChiTietRepository;
import com.example.sd50datn.Repository.GioHangRepository;
import com.example.sd50datn.Repository.SanPhamRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GioHangService {

    private final GioHangRepository gioHangRepo;
    private final GioHangChiTietRepository chiTietRepo;
    private final SanPhamRepository sanPhamRepo;

    /**
     * Get or create cart for the current session/customer.
     * If shopCustomerId is in session, use customer-based cart.
     * Otherwise, use session-based cart (guest).
     */
    public GioHang getOrCreateCart(HttpSession session) {
        Integer customerId = (Integer) session.getAttribute("shopCustomerId");
        GioHang cart;

        if (customerId != null) {
            cart = gioHangRepo.findByKhachHangId(customerId).orElse(null);
            if (cart == null) {
                cart = GioHang.builder()
                        .khachHangId(customerId)
                        .ngayTao(LocalDateTime.now())
                        .chiTietList(new ArrayList<>())
                        .build();
                cart = gioHangRepo.save(cart);
            }
        } else {
            String sessionId = session.getId();
            cart = gioHangRepo.findBySessionId(sessionId).orElse(null);
            if (cart == null) {
                cart = GioHang.builder()
                        .sessionId(sessionId)
                        .ngayTao(LocalDateTime.now())
                        .chiTietList(new ArrayList<>())
                        .build();
                cart = gioHangRepo.save(cart);
            }
        }
        return cart;
    }

    public List<GioHangChiTiet> getCartItems(GioHang cart) {
        if (cart == null) {
            return new ArrayList<>();
        }
        return chiTietRepo.findByGioHangId(cart.getId());
    }

    public int getCartItemCount(HttpSession session) {
        Integer customerId = (Integer) session.getAttribute("shopCustomerId");
        GioHang cart = null;
        if (customerId != null) {
            cart = gioHangRepo.findByKhachHangId(customerId).orElse(null);
        } else {
            cart = gioHangRepo.findBySessionId(session.getId()).orElse(null);
        }
        if (cart == null) {
            return 0;
        }
        List<GioHangChiTiet> items = chiTietRepo.findByGioHangId(cart.getId());
        return items.stream().mapToInt(i -> i.getSoLuong() != null ? i.getSoLuong() : 0).sum();
    }

    @Transactional
    public String addToCart(HttpSession session, Integer sanPhamId, Integer soLuong) {
        if (sanPhamId == null || soLuong == null || soLuong < 1) {
            return "Số lượng không hợp lệ";
        }

        Optional<SanPham> optSp = sanPhamRepo.findById(sanPhamId);
        if (optSp.isEmpty()) {
            return "Sản phẩm không tồn tại";
        }

        SanPham sp = optSp.get();
        if (sp.getTrangThai() != 1) {
            return "Sản phẩm ngừng kinh doanh";
        }
        if (sp.getSoLuongTon() == null || sp.getSoLuongTon() < soLuong) {
            return "Số lượng tồn kho không đủ";
        }

        GioHang cart = getOrCreateCart(session);
        Optional<GioHangChiTiet> existing = chiTietRepo.findByGioHangIdAndSanPhamId(cart.getId(), sanPhamId);

        if (existing.isPresent()) {
            GioHangChiTiet item = existing.get();
            int newQty = item.getSoLuong() + soLuong;
            if (newQty > sp.getSoLuongTon()) {
                return "Số lượng vượt quá tồn kho (còn " + sp.getSoLuongTon() + ")";
            }
            item.setSoLuong(newQty);
            item.setGiaTaiThoiDiem(sp.getGiaBan());
            chiTietRepo.save(item);
        } else {
            GioHangChiTiet item = GioHangChiTiet.builder()
                    .gioHang(cart)
                    .sanPham(sp)
                    .soLuong(soLuong)
                    .giaTaiThoiDiem(sp.getGiaBan())
                    .build();
            chiTietRepo.save(item);
        }

        return null;
    }

    @Transactional
    public void updateQuantity(Integer chiTietId, Integer soLuong) {
        Optional<GioHangChiTiet> opt = chiTietRepo.findById(chiTietId);
        if (opt.isPresent()) {
            GioHangChiTiet item = opt.get();
            if (soLuong <= 0) {
                chiTietRepo.delete(item);
            } else {
                item.setSoLuong(soLuong);
                chiTietRepo.save(item);
            }
        }
    }

    @Transactional
    public void removeItem(Integer chiTietId) {
        chiTietRepo.deleteById(chiTietId);
    }

    @Transactional
    public void clearCart(GioHang cart) {
        if (cart != null) {
            chiTietRepo.deleteByGioHangId(cart.getId());
        }
    }

    public BigDecimal calculateTotal(List<GioHangChiTiet> items) {
        return items.stream()
                .map(item -> {
                    BigDecimal price = item.getGiaTaiThoiDiem() != null
                            ? item.getGiaTaiThoiDiem()
                            : (item.getSanPham() != null ? item.getSanPham().getGiaBan() : BigDecimal.ZERO);
                    return price.multiply(BigDecimal.valueOf(item.getSoLuong()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Merge guest cart into customer cart after login.
     */
    @Transactional
    public void mergeGuestCart(HttpSession session, Integer customerId) {
        String sessionId = session.getId();
        Optional<GioHang> guestCartOpt = gioHangRepo.findBySessionId(sessionId);
        if (guestCartOpt.isEmpty()) {
            return;
        }

        GioHang guestCart = guestCartOpt.get();
        List<GioHangChiTiet> guestItems = chiTietRepo.findByGioHangId(guestCart.getId());
        if (guestItems.isEmpty()) {
            gioHangRepo.delete(guestCart);
            return;
        }

        GioHang customerCart = gioHangRepo.findByKhachHangId(customerId).orElse(null);
        if (customerCart == null) {
            guestCart.setKhachHangId(customerId);
            guestCart.setSessionId(null);
            gioHangRepo.save(guestCart);
        } else {
            for (GioHangChiTiet guestItem : guestItems) {
                Optional<GioHangChiTiet> existingOpt = chiTietRepo.findByGioHangIdAndSanPhamId(
                        customerCart.getId(), guestItem.getSanPham().getId());
                if (existingOpt.isPresent()) {
                    GioHangChiTiet existing = existingOpt.get();
                    existing.setSoLuong(existing.getSoLuong() + guestItem.getSoLuong());
                    chiTietRepo.save(existing);
                } else {
                    guestItem.setGioHang(customerCart);
                    chiTietRepo.save(guestItem);
                }
            }
            gioHangRepo.delete(guestCart);
        }
    }
}
