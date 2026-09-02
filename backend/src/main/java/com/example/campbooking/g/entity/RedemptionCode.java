package com.example.campbooking.g.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 兑换码表 redemption_codes。
 * 真实列：code（录入码）, code_value（coupon/free_room 存 coupons.id；points 存积分值或标识）,
 *         type, is_used(0/1), used_by, used_at, created_at。无 value / used / expire_at。
 */
@TableName("redemption_codes")
public class RedemptionCode {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户输入的兑换码，如 WELCOME2024 / CAMP2024 / FREEROOM / SUMMER2024 */
    private String code;

    /** coupon/free_room → 对应 coupons.id；points → 积分值或标识 */
    @TableField("code_value")
    private String codeValue;

    /** coupon | free_room | points */
    private String type;

    /** 0 未使用 / 1 已使用 */
    @TableField("is_used")
    private Integer isUsed;

    @TableField("used_by")
    private Long usedBy;

    @TableField("used_at")
    private LocalDateTime usedAt;

    @TableField("created_at")
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getCodeValue() { return codeValue; }
    public void setCodeValue(String codeValue) { this.codeValue = codeValue; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Integer getIsUsed() { return isUsed; }
    public void setIsUsed(Integer isUsed) { this.isUsed = isUsed; }
    public Long getUsedBy() { return usedBy; }
    public void setUsedBy(Long usedBy) { this.usedBy = usedBy; }
    public LocalDateTime getUsedAt() { return usedAt; }
    public void setUsedAt(LocalDateTime usedAt) { this.usedAt = usedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
