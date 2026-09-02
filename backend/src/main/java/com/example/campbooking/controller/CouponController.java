package com.example.campbooking.controller;

import com.example.campbooking.common.Result;
import com.example.campbooking.service.CouponService;
import com.example.campbooking.vo.CouponVO;
import com.example.campbooking.vo.UserCouponVO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping
    public Result<List<CouponVO>> list() {
        return Result.success(couponService.listAvailable(currentUserIdOrNull()));
    }

    @PostMapping("/{id}/receive")
    public Result<UserCouponVO> receive(@PathVariable Long id) {
        return Result.success(couponService.receive(currentUserId(), id));
    }

    @GetMapping("/my")
    public Result<List<UserCouponVO>> my(@RequestParam(required = false) String status) {
        return Result.success(couponService.myCoupons(currentUserId(), status));
    }

    @GetMapping("/available")
    public Result<List<UserCouponVO>> available(@RequestParam(required = false) BigDecimal totalPrice) {
        return Result.success(couponService.availableForOrder(currentUserId(), totalPrice));
    }

    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Long) auth.getPrincipal();
    }

    private Long currentUserIdOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long) {
            return (Long) auth.getPrincipal();
        }
        return null;
    }
}