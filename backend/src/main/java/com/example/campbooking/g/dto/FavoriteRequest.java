package com.example.campbooking.g.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * 收藏请求。
 * targetType 仅支持 hotel（住宿）或 camp（研学营），与 products.category 枚举保持一致。
 */
public class FavoriteRequest {

    @NotNull(message = "targetType 不能为空")
    @Pattern(regexp = "^(hotel|camp)$", message = "targetType 仅支持 hotel 或 camp")
    private String targetType;

    @NotNull(message = "targetId 不能为空")
    private Long targetId;

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
}
