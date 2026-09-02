package com.example.campbooking.g.service.impl;

import com.example.campbooking.g.dto.FeedbackRequest;
import com.example.campbooking.g.entity.Feedback;
import com.example.campbooking.g.mapper.FeedbackMapper;
import com.example.campbooking.g.security.SecurityUtils;
import com.example.campbooking.g.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Override
    public void submit(FeedbackRequest request) {
        Feedback feedback = new Feedback();
        feedback.setUserId(SecurityUtils.getCurrentUserId());
        feedback.setContent(request.getContent());
        feedback.setEmail(request.getEmail());
        feedback.setCreatedAt(LocalDateTime.now());
        feedbackMapper.insert(feedback);
    }
}
