package com.example.campbooking.service;

import com.example.campbooking.vo.CouponVO;
import com.example.campbooking.vo.UserCouponVO;

import java.math.BigDecimal;
import java.util.List;

public interface CouponService {
    List<CouponVO> listAvailable(Long userId);
    UserCouponVO receive(Long userId, Long couponId);
    List<UserCouponVO> myCoupons(Long userId, String status);
    List<UserCouponVO> availableForOrder(Long userId, BigDecimal totalPrice);
}