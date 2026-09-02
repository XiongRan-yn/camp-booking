package com.example.campbooking.service;

import com.example.campbooking.common.PageResult;
import com.example.campbooking.vo.PointsBalanceVO;
import com.example.campbooking.vo.PointsRecordVO;

public interface PointsService {
    PointsBalanceVO balance(Long userId);
    PageResult<PointsRecordVO> records(Long userId, Integer page, Integer pageSize);
}