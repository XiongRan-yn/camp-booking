package com.example.campbooking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campbooking.common.PageResult;
import com.example.campbooking.entity.PointsRecord;
import com.example.campbooking.entity.User;
import com.example.campbooking.mapper.PointsRecordMapper;
import com.example.campbooking.mapper.UserMapper;
import com.example.campbooking.service.PointsService;
import com.example.campbooking.vo.PointsBalanceVO;
import com.example.campbooking.vo.PointsRecordVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PointsServiceImpl implements PointsService {

    private final UserMapper userMapper;
    private final PointsRecordMapper pointsRecordMapper;

    public PointsServiceImpl(UserMapper userMapper, PointsRecordMapper pointsRecordMapper) {
        this.userMapper = userMapper;
        this.pointsRecordMapper = pointsRecordMapper;
    }

    @Override
    public PointsBalanceVO balance(Long userId) {
        LambdaQueryWrapper<PointsRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointsRecord::getUserId, userId);
        List<PointsRecord> records = pointsRecordMapper.selectList(wrapper);

        int earned = 0;
        int spent = 0;
        for (PointsRecord record : records) {
            if (record.getAmount() == null) {
                continue;
            }
            if (record.getAmount() >= 0) {
                earned += record.getAmount();
            } else {
                spent += Math.abs(record.getAmount());
            }
        }

        User user = userMapper.selectById(userId);
        PointsBalanceVO vo = new PointsBalanceVO();
        vo.setBalance(user != null && user.getPointsBalance() != null ? user.getPointsBalance() : 0);
        vo.setTotalEarned(earned);
        vo.setTotalSpent(spent);
        return vo;
    }

    @Override
    public PageResult<PointsRecordVO> records(Long userId, Integer page, Integer pageSize) {
        LambdaQueryWrapper<PointsRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointsRecord::getUserId, userId);
        wrapper.orderByDesc(PointsRecord::getCreatedAt);

        Page<PointsRecord> mpPage = new Page<>(page, pageSize);
        Page<PointsRecord> result = pointsRecordMapper.selectPage(mpPage, wrapper);

        List<PointsRecordVO> list = result.getRecords().stream().map(record -> {
            PointsRecordVO vo = new PointsRecordVO();
            vo.setId(record.getId());
            vo.setAmount(record.getAmount());
            vo.setType(record.getType());
            vo.setSource(record.getSource());
            vo.setRemark(record.getRemark());
            vo.setCreatedAt(record.getCreatedAt());
            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(list, result.getTotal(), page, pageSize);
    }
}