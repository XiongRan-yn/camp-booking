package com.example.campbooking.dto;

public class OrderCalculateRequest {
    private Long productId;
    private Long specId;
    private Integer quantity;
    private Long couponId;

    public OrderCalculateRequest() {}

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Long getSpecId() { return specId; }
    public void setSpecId(Long specId) { this.specId = specId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Long getCouponId() { return couponId; }
    public void setCouponId(Long couponId) { this.couponId = couponId; }
}
