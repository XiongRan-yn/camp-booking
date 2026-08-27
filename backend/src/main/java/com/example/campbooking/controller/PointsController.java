package com.example.campbooking.controller;

import com.example.campbooking.common.PageResult;
import com.example.campbooking.common.Result;
import com.example.campbooking.service.PointsService;
import com.example.campbooking.vo.PointsBalanceVO;
import com.example.campbooking.vo.PointsRecordVO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/points")
public class PointsController {

    private final PointsService pointsService;

    public PointsController(PointsService pointsService) {
        this.pointsService = pointsService;
    }

    @GetMapping("/balance")
    public Result<PointsBalanceVO> balance() {
        return Result.success(pointsService.balance(currentUserId()));
    }

    @GetMapping("/records")
    public Result<PageResult<PointsRecordVO>> records(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(pointsService.records(currentUserId(), page, pageSize));
    }

    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Long) auth.getPrincipal();
    }
}