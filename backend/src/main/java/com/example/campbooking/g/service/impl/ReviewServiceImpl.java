package com.example.campbooking.g.service.impl;

import com.example.campbooking.common.BusinessException;
import com.example.campbooking.g.dto.ReviewRequest;
import com.example.campbooking.g.entity.Review;
import com.example.campbooking.g.mapper.ReviewMapper;
import com.example.campbooking.g.security.SecurityUtils;
import com.example.campbooking.g.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    /**
     * 订单校验用 JdbcTemplate 直查 orders 表（列名 id/user_id/status 已从 schema.sql 核实），
     * 不依赖 F 的 Order 实体/Mapper 类名；联调稳定后可替换为注入 F 的 OrderMapper。
     */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void add(ReviewRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        validateOrder(request.getOrderId(), userId);

        Review review = new Review();
        review.setUserId(userId);
        review.setType(request.getType());
        review.setProductId(request.getProductId());   // productId 对应 reviews.product_id
        review.setOrderId(request.getOrderId());
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setImages(request.getImages());
        review.setCreatedAt(LocalDateTime.now());
        reviewMapper.insert(review);
    }

    /**
     * 校验订单：存在（404）→ 本人（403）→ 已支付（409）。
     * reviews.order_id 有外键指向 orders(id)，不先校验会让脏数据以 500 外键错误暴露。
     */
    private void validateOrder(Long orderId, Long userId) {
        Map<String, Object> order;
        try {
            order = jdbcTemplate.queryForMap(
                    "SELECT user_id, status FROM orders WHERE id = ?", orderId);
        } catch (EmptyResultDataAccessException e) {
            throw new BusinessException(404, "订单不存在");
        }

        Long orderUserId = ((Number) order.get("user_id")).longValue();
        if (!orderUserId.equals(userId)) {
            throw new BusinessException(403, "只能评价本人的订单");
        }

        Object status = order.get("status");
        // orders.status 默认 'pending'，已支付为 'paid'；'completed' 视为已支付过的终态
        if (!"paid".equals(status) && !"completed".equals(status)) {
            throw new BusinessException(409, "订单未支付，不能评价");
        }
    }

    @Override
    public List<Review> listByTarget(String targetType, Long targetId) {
        if (!"hotel".equals(targetType) && !"dynamic".equals(targetType)) {
            throw new BusinessException(400, "targetType 仅支持 hotel 或 dynamic");
        }
        return reviewMapper.selectByTarget(targetType, targetId);
    }
}
