package com.example.campbooking.g.mapper;

import com.example.campbooking.g.entity.Coupon;
import com.example.campbooking.g.entity.UserCoupon;
import org.apache.ibatis.annotations.Param;

/**
 * 兑换专用 Mapper：负责把券模板与用户券写入 coupons / user_coupons 表。
 * 自定义方法，SQL 见 resources/mapper/RedeemMapper.xml。
 * （不使用 BaseMapper，避免与团队已有的 CouponMapper / UserCouponMapper Bean 名称冲突。）
 */
public interface RedeemMapper {

    int insertCoupon(Coupon coupon);

    int insertUserCoupon(UserCoupon userCoupon);

    /** 按模板编码查券（coupon / free_room 复用同一模板） */
    Coupon selectCouponByCode(@Param("code") String code);
}
