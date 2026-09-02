package com.example.campbooking.vo;

import java.time.LocalDateTime;

public class PointsRecordVO {
    private Long id;
    private Integer amount;
    private String type;
    private String source;
    private String remark;
    private LocalDateTime createdAt;

    public PointsRecordVO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
