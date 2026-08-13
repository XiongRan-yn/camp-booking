package com.example.campbooking.g.controller;

import com.example.campbooking.common.Result;
import com.example.campbooking.g.dto.FeedbackRequest;
import com.example.campbooking.g.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    /** 提交意见反馈 */
    @PostMapping
    public Result<Void> submit(@Valid @RequestBody FeedbackRequest request) {
        feedbackService.submit(request);
        return Result.success();
    }
}
