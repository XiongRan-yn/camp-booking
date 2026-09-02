package com.example.campbooking.g.service;

import com.example.campbooking.g.dto.RedeemRequest;
import com.example.campbooking.g.vo.RedeemResultVO;

public interface RedeemService {

    /**
     * 兑换码兑换。
     * coupon / free_room 走同一发放逻辑（查/建券模板 → 写入 user_coupons）；
     * points 累加积分；未知类型抛 400 且不消耗兑换码。
     */
    RedeemResultVO redeem(RedeemRequest request);
}
