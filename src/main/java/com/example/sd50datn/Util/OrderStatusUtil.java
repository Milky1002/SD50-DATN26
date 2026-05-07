package com.example.sd50datn.Util;

import java.util.List;
import java.util.Map;

public final class OrderStatusUtil {

    public static final int CHO_XAC_NHAN = 0;
    public static final int DA_XAC_NHAN  = 1;
    public static final int DANG_GIAO    = 2;
    public static final int HOAN_TAT     = 3;
    public static final int DA_HUY       = 4;

    private static final Map<Integer, String> LABELS = Map.of(
            CHO_XAC_NHAN, "Chờ xác nhận",
            DA_XAC_NHAN,  "Đã xác nhận",
            DANG_GIAO,    "Đang giao",
            HOAN_TAT,     "Hoàn tất",
            DA_HUY,       "Đã hủy"
    );

    private static final Map<Integer, List<Integer>> TRANSITIONS = Map.of(
            CHO_XAC_NHAN, List.of(DA_XAC_NHAN, DA_HUY),
            DA_XAC_NHAN,  List.of(DANG_GIAO, DA_HUY),
            DANG_GIAO,    List.of(HOAN_TAT, DA_HUY),
            HOAN_TAT,     List.of(),
            DA_HUY,       List.of()
    );

    private static final Map<Integer, String> TAB_KEYS = Map.of(
            CHO_XAC_NHAN, "waiting",
            DA_XAC_NHAN,  "confirmed",
            DANG_GIAO,    "shipping",
            HOAN_TAT,     "done",
            DA_HUY,       "cancelled"
    );

    private OrderStatusUtil() {
    }

    public static String getLabel(Integer status) {
        return LABELS.getOrDefault(status, "Không xác định");
    }

    public static List<Integer> getAllowedTransitions(Integer currentStatus) {
        return TRANSITIONS.getOrDefault(currentStatus, List.of());
    }

    public static boolean isValidTransition(Integer from, Integer to) {
        return getAllowedTransitions(from).contains(to);
    }

    public static String getTabKey(Integer status) {
        return TAB_KEYS.getOrDefault(status, "waiting");
    }

    public static Map<Integer, String> getAllStatuses() {
        return LABELS;
    }

    public static List<Integer> getShippingStatuses() {
        return List.of(DA_XAC_NHAN, DANG_GIAO, HOAN_TAT, DA_HUY);
    }
}
