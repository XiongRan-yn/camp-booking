package com.example.campbooking.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CouponVO {
    private Long id;
    private String title;
    private String type;
    private BigDecimal discountValue;
    private BigDecimal minAmount;
    private Integer totalCount;
    private Integer receivedCount;
    private String rules;
    private Integer validDays;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean hasReceived;

    public CouponVO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }
    public BigDecimal getMinAmount() { return minAmount; }
    public void setMinAmount(BigDecimal minAmount) { this.minAmount = minAmount; }
    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
    public Integer getReceivedCount() { return receivedCount; }
    public void setReceivedCount(Integer receivedCount) { this.receivedCount = receivedCount; }
    public String getRules() { return rules; }
    public void setRules(String rules) { this.rules = rules; }
    public Integer getValidDays() { return validDays; }
    public void setValidDays(Integer validDays) { this.validDays = validDays; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public Boolean getHasReceived() { return hasReceived; }
    public void setHasReceived(Boolean hasReceived) { this.hasReceived = hasReceived; }
}
