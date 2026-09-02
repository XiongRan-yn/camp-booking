package com.example.campbooking.g.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 评价表 reviews。
 * type 固定为 dynamic（我的动态）或 hotel（酒店评价），NOT camp。
 * product_id 为真实列（NOT NULL），原 target_id 已改名。
 * order_id 关联 F 模块订单表，必须传真实订单（校验「已支付才能评价」留 TODO）。
 */
@TableName("reviews")
public class Review {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    /** dynamic | hotel */
    private String type;

    /** 真实列 product_id（NOT NULL），原 target_id */
    @TableField("product_id")
    private Long productId;

    @TableField("order_id")
    private Long orderId;

    private Integer rating;

    private String content;

    /** 图片 URL，多个以英文逗号分隔，可空 */
    private String images;

    @TableField("created_at")
    private LocalDateTime createdAt;

    /** 非表字段：评价人昵称（联 users 表填充） */
    @TableField(exist = false)
    private String nickname;

    /** 非表字段：商品标题（联 products 表填充） */
    @TableField(exist = false)
    private String productTitle;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }
}
