package com.example.campbooking.g.mapper;

import com.example.campbooking.g.entity.Coupon;
import com.example.campbooking.g.entity.UserCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 兑换专用 Mapper。按 coupons.id 查券模板、写 user_coupons。
 * 不使用 BaseMapper，避免与团队已有的 CouponMapper / UserCouponMapper Bean 冲突。
 */
@Mapper
public interface RedeemMapper {

    /** 按 coupons.id 查券模板（兑换码表 code_value 存的就是优惠券 id） */
    Coupon selectCouponById(@Param("id") Long id);

    /** 写入用户券（user_coupons 无 code 列，status 由调用方写 "usable"） */
    int insertUserCoupon(UserCoupon userCoupon);
}
