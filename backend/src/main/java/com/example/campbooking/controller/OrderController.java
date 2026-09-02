package com.example.campbooking.controller;

import com.example.campbooking.common.PageResult;
import com.example.campbooking.common.Result;
import com.example.campbooking.dto.OrderCalculateRequest;
import com.example.campbooking.dto.OrderCreateRequest;
import com.example.campbooking.service.OrderService;
import com.example.campbooking.vo.OrderVO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/calculate")
    public Result<Map<String, Object>> calculate(@RequestBody OrderCalculateRequest request) {
        return Result.success(orderService.calculate(currentUserId(), request));
    }

    @PostMapping
    public Result<Map<String, Object>> create(@RequestBody OrderCreateRequest request) {
        return Result.success(orderService.create(currentUserId(), request));
    }

    @PutMapping("/{id}/pay")
    public Result<Map<String, Object>> pay(@PathVariable Long id) {
        return Result.success(orderService.pay(currentUserId(), id));
    }

    @PutMapping("/{id}/complete")
    public Result<Map<String, Object>> complete(@PathVariable Long id) {
        return Result.success(orderService.complete(currentUserId(), id));
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        orderService.cancel(currentUserId(), id);
        return Result.success();
    }

    @GetMapping
    public Result<PageResult<OrderVO>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(orderService.list(currentUserId(), status, keyword, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<OrderVO> detail(@PathVariable Long id) {
        return Result.success(orderService.detail(currentUserId(), id));
    }

    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Long) auth.getPrincipal();
    }
}