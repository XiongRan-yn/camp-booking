package com.example.campbooking.g.vo;

import java.time.LocalDateTime;

/**
 * 兑换结果视图对象，返回给前端。
 */
public class RedeemResultVO {

    /** 兑换码 */
    private String code;

    /** 是否成功 */
    private boolean success;

    /** 提示信息 */
    private String message;

    /** 发放到的券名称（coupon / free_room 时有值） */
    private String couponName;

    /** 券过期时间 */
    private LocalDateTime expireAt;

    public static RedeemResultVO success(String code, String couponName, LocalDateTime expireAt) {
        RedeemResultVO vo = new RedeemResultVO();
        vo.code = code;
        vo.success = true;
        vo.message = "兑换成功";
        vo.couponName = couponName;
        vo.expireAt = expireAt;
        return vo;
    }

    public static RedeemResultVO fail(String code, String message) {
        RedeemResultVO vo = new RedeemResultVO();
        vo.code = code;
        vo.success = false;
        vo.message = message;
        return vo;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public LocalDateTime getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(LocalDateTime expireAt) {
        this.expireAt = expireAt;
    }
}
