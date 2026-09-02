package com.example.campbooking.service;

import com.example.campbooking.common.PageResult;
import com.example.campbooking.vo.ProductDetailVO;
import com.example.campbooking.vo.ProductVO;

public interface ProductService {
    PageResult<ProductVO> listProducts(String category, String keyword, Integer page, Integer pageSize);
    ProductDetailVO getDetail(Long productId, Long userId);
}
