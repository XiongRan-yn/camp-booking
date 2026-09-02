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

    /**
     * 写积分流水 points_records（type 固定 'earn'，created_at 走数据库默认值）。
     * 这是 F 的 PointsRecordMapper 未并入前的 G 侧兜底：直写同一张表、列完全一致，
     * F 的 /api/points/records 上线后即可读到；F 代码并入后改由 F 的 Mapper 写入。
     */
    int insertPointsRecord(@Param("userId") Long userId,
                           @Param("amount") int amount,
                           @Param("source") String source,
                           @Param("remark") String remark);
}
