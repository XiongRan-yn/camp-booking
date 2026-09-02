package com.example.campbooking.g.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 用户优惠券表 user_coupons。
 * 真实列：id, user_id, coupon_id, coupon_title(NOT NULL), status,
 *         received_at, used_at, expire_at(NOT NULL)（无 code / created_at）。
 * status 固定写 "usable"（F 的卡券/订单模块才识别）。
 */
@TableName("user_coupons")
public class UserCoupon {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("coupon_id")
    private Long couponId;

    /** NOT NULL，取 coupons.title */
    @TableField("coupon_title")
    private String couponTitle;

    /** usable（非 unused） */
    private String status;

    @TableField("received_at")
    private LocalDateTime receivedAt;

    @TableField("used_at")
    private LocalDateTime usedAt;

    @TableField("expire_at")
    private LocalDateTime expireAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public String getCouponTitle() {
        return couponTitle;
    }

    public void setCouponTitle(String couponTitle) {
        this.couponTitle = couponTitle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }

    public LocalDateTime getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(LocalDateTime expireAt) {
        this.expireAt = expireAt;
    }
}
