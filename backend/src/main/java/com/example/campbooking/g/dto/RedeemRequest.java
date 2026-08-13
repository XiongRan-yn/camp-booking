package com.example.campbooking.g.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 兑换码兑换请求。
 * code 即 redemption_codes 表中的兑换码（如 FREEROOM / 优惠券码 / 积分码）。
 */
public class RedeemRequest {

    @NotBlank(message = "兑换码不能为空")
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
