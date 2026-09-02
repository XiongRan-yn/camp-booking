package com.example.campbooking.g.service;

import com.example.campbooking.g.dto.ReviewRequest;
import com.example.campbooking.g.entity.Review;

import java.util.List;

public interface ReviewService {

    /**
     * 提交评价。
     * TODO(G模块联调)：依赖 F 模块 orders 表做「已支付才可评价」校验，
     * 联调前可手动插一条 status='paid' 的订单自测。
     */
    void add(ReviewRequest request);

    /** 按对象查评价列表：targetType = hotel|dynamic，targetId 为对象 id */
    List<Review> listByTarget(String targetType, Long targetId);

    /**
     * 评价列表：传 targetType+targetId 按对象查；
     * 不传则查当前用户的评价（可选按 type 过滤）
     */
    List<Review> list(String targetType, Long targetId, String type);
}
