package com.example.campbooking.g.controller;

import com.example.campbooking.common.Result;
import com.example.campbooking.g.dto.RedeemRequest;
import com.example.campbooking.g.service.RedeemService;
import com.example.campbooking.g.vo.RedeemResultVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/redeem")
public class RedeemController {

    @Autowired
    private RedeemService redeemService;

    /** 兑换码兑换：coupon / free_room / points */
    @PostMapping
    public Result<RedeemResultVO> redeem(@Valid @RequestBody RedeemRequest request) {
        return Result.success(redeemService.redeem(request));
    }
}
