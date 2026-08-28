package com.example.campbooking.g.service.impl;

import com.example.campbooking.common.BusinessException;
import com.example.campbooking.g.dto.ReviewRequest;
import com.example.campbooking.g.entity.Review;
import com.example.campbooking.g.mapper.ReviewMapper;
import com.example.campbooking.g.security.SecurityUtils;
import com.example.campbooking.g.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    // TODO(F模块联调)：引入 F 的 OrderMapper 后取消注释并补「已支付才能评价」校验
    // @Autowired
    // private com.example.campbooking.mapper.OrderMapper orderMapper;  // 包名按 F 真实位置调整

    @Override
    public void add(ReviewRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();

        // TODO(F模块联调)：F 交付后补齐「已支付订单才能评价」（orderId 已由 @NotNull 保证非空）。
        // Order order = orderMapper.selectById(request.getOrderId());
        // if (order == null || !order.getUserId().equals(userId)) {
        //     throw new BusinessException(403, "只能评价本人的订单");
        // }
        // Object status = order.getStatus();
        // boolean paid = (status instanceof String && "paid".equals(status))
        //              || (status instanceof Number && ((Number) status).intValue() == 2);
        // if (!paid) {
        //     throw new BusinessException(409, "订单未支付，不能评价");
        // }

        Review review = new Review();
        review.setUserId(userId);
        review.setType(request.getType());
        review.setProductId(request.getProductId());   // targetId 对应 reviews.product_id
        review.setOrderId(request.getOrderId());       // 真实订单，必传
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setImages(request.getImages());
        review.setCreatedAt(LocalDateTime.now());
        reviewMapper.insert(review);
    }

    @Override
    public List<Review> listByTarget(String targetType, Long targetId) {
        if (!"hotel".equals(targetType) && !"dynamic".equals(targetType)) {
            throw new BusinessException(400, "targetType 仅支持 hotel 或 dynamic");
        }
        return reviewMapper.selectByTarget(targetType, targetId);
    }
}
