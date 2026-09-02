package com.example.campbooking.g.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * 评价请求。
 * type 固定为 dynamic（我的动态）或 hotel（酒店评价），NOT camp。
 * targetId 对应 reviews.product_id；orderId 必须传真实订单。
 */
public class ReviewRequest {

    @NotBlank(message = "type 不能为空")
    @Pattern(regexp = "^(hotel|dynamic)$", message = "type 仅支持 hotel 或 dynamic")
    private String type;

    @NotNull(message = "productId 不能为空")
    private Long productId;

    /** 关联订单 ID（F 模块订单表），必须传真实订单，用于校验「已支付才能评价」 */
    @NotNull(message = "orderId 不能为空（需传真实订单）")
    private Long orderId;

    @NotNull(message = "rating 不能为空")
    @Min(1)
    @Max(5)
    private Integer rating;

    @NotBlank(message = "content 不能为空")
    private String content;

    /** 图片 URL，多个以英文逗号分隔，可空 */
    private String images;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
