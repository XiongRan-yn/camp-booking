package com.example.campbooking.g.service;

import com.example.campbooking.g.dto.FeedbackRequest;

public interface FeedbackService {

    /** 提交意见反馈（userId 从登录上下文取） */
    void submit(FeedbackRequest request);
}
