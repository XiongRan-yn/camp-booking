package com.example.campbooking.g.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * 评价请求。
 * type 枚举固定为 dynamic（我的动态）或 hotel（酒店评价），NOT camp。
 */
public class ReviewRequest {

    @NotBlank(message = "type 不能为空")
    @Pattern(regexp = "^(hotel|dynamic)$", message = "type 仅支持 hotel 或 dynamic")
    private String type;

    @NotNull(message = "targetId 不能为空")
    private Long targetId;

    /** 关联订单 ID（F 模块订单表），用于校验「已支付才能评价」。联调前可空。 */
    private Long orderId;

    @NotNull(message = "rating 不能为空")
    @Min(1)
    @Max(5)
    private Integer rating;

    @NotBlank(message = "content 不能为空")
    private String content;

    /** 图片 URL，多个以英文逗号分隔，可空。 */
    private String images;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
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
}
