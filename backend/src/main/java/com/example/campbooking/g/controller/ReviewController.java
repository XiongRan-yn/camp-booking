package com.example.campbooking.g.controller;

import com.example.campbooking.common.Result;
import com.example.campbooking.g.dto.ReviewRequest;
import com.example.campbooking.g.entity.Review;
import com.example.campbooking.g.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * 提交评价。
     * TODO(G模块联调)：依赖 F 模块订单「已支付」校验，联调前可手动插 paid 订单自测。
     */
    @PostMapping
    public Result<Void> add(@Valid @RequestBody ReviewRequest request) {
        reviewService.add(request);
        return Result.success();
    }

    /** 评价列表：传 targetType+targetId 按对象查；不传则查当前用户的评价 */
    @GetMapping
    public Result<List<Review>> list(@RequestParam(required = false) String targetType,
                                     @RequestParam(required = false) Long targetId,
                                     @RequestParam(required = false) String type) {
        return Result.success(reviewService.list(targetType, targetId, type));
    }
}
