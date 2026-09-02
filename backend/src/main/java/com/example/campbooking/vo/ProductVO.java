package com.example.campbooking.vo;

import java.math.BigDecimal;
import java.util.List;

public class ProductVO {
    private Long id;
    private String title;
    private String category;
    private String subCategory;
    private String coverImage;
    private List<String> tags;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean isFull;

    public ProductVO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSubCategory() { return subCategory; }
    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }
    public BigDecimal getMaxPrice() { return maxPrice; }
    public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }
    public Boolean getIsFull() { return isFull; }
    public void setIsFull(Boolean isFull) { this.isFull = isFull; }
}
