package com.example.campbooking.service;

import com.example.campbooking.common.PageResult;
import com.example.campbooking.dto.OrderCalculateRequest;
import com.example.campbooking.dto.OrderCreateRequest;
import com.example.campbooking.vo.OrderVO;

import java.util.Map;

public interface OrderService {
    Map<String, Object> calculate(Long userId, OrderCalculateRequest request);
    Map<String, Object> create(Long userId, OrderCreateRequest request);
    Map<String, Object> pay(Long userId, Long orderId);
    void cancel(Long userId, Long orderId);
    PageResult<OrderVO> list(Long userId, String status, String keyword, Integer page, Integer pageSize);
    OrderVO detail(Long userId, Long orderId);
}
