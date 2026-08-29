package com.example.campbooking.g.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 意见反馈请求。email 为选填（邮箱，便于回访）。
 */
public class FeedbackRequest {

    @NotBlank(message = "反馈内容不能为空")
    private String content;

    /** 选填：邮箱 */
    private String email;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
