package com.example.campbooking.g.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 收藏表 favorites。
 * targetType 取值 hotel（住宿）或 camp（研学营），与 products.category 枚举一致。
 */
@TableName("favorites")
public class Favorite {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("target_type")
    private String targetType;

    @TableField("target_id")
    private Long targetId;

    @TableField("created_at")
    private LocalDateTime createdAt;

    /** 非表字段：收藏对象标题（联表查询填充） */
    @TableField(exist = false)
    private String targetTitle;

    /** 非表字段：收藏对象封面图（联表查询填充） */
    @TableField(exist = false)
    private String targetImage;

    /** 非表字段：收藏对象起价（联表查询填充） */
    @TableField(exist = false)
    private java.math.BigDecimal targetPrice;

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

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getTargetTitle() {
        return targetTitle;
    }

    public void setTargetTitle(String targetTitle) {
        this.targetTitle = targetTitle;
    }

    public String getTargetImage() {
        return targetImage;
    }

    public void setTargetImage(String targetImage) {
        this.targetImage = targetImage;
    }

    public java.math.BigDecimal getTargetPrice() {
        return targetPrice;
    }

    public void setTargetPrice(java.math.BigDecimal targetPrice) {
        this.targetPrice = targetPrice;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
