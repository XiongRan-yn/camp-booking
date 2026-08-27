package com.example.campbooking.g.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 兑换码表 redemption_codes。
 * type: coupon（优惠券）| free_room（免费房）| points（积分）。
 * used: 0 未使用 / 1 已使用。
 */
@TableName("redemption_codes")
public class RedemptionCode {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String code;

    /** coupon | free_room | points */
    private String type;

    /** coupon: 面额；points: 积分值；free_room: 一般无用 */
    private Integer value;

    /** 0 未使用 / 1 已使用 */
    private Integer used;

    @TableField("used_by")
    private Long usedBy;

    @TableField("used_at")
    private LocalDateTime usedAt;

    @TableField("expire_at")
    private LocalDateTime expireAt;

    @TableField("created_at")
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getUsed() {
        return used;
    }

    public void setUsed(Integer used) {
        this.used = used;
    }

    public Long getUsedBy() {
        return usedBy;
    }

    public void setUsedBy(Long usedBy) {
        this.usedBy = usedBy;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
