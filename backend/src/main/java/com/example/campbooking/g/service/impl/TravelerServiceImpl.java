package com.example.campbooking.g.service.impl;

import com.example.campbooking.common.BusinessException;
import com.example.campbooking.g.dto.TravelerRequest;
import com.example.campbooking.g.entity.Traveler;
import com.example.campbooking.g.mapper.TravelerMapper;
import com.example.campbooking.g.security.SecurityUtils;
import com.example.campbooking.g.service.TravelerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TravelerServiceImpl implements TravelerService {

    @Autowired
    private TravelerMapper travelerMapper;

    @Override
    public Long add(TravelerRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        Traveler traveler = new Traveler();
        traveler.setUserId(userId);
        traveler.setName(request.getName());
        traveler.setIdCard(request.getIdCard());
        traveler.setPhone(request.getPhone());
        traveler.setAge(request.getAge());
        traveler.setGender(request.getGender());
        LocalDateTime now = LocalDateTime.now();
        traveler.setCreatedAt(now);
        traveler.setUpdatedAt(now);
        travelerMapper.insert(traveler);
        return traveler.getId();
    }

    @Override
    public void update(TravelerRequest request) {
        if (request.getId() == null) {
            throw new BusinessException(400, "修改出行人时 id 不能为空");
        }
        Long userId = SecurityUtils.getCurrentUserId();
        Traveler existing = travelerMapper.selectById(request.getId());
        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new BusinessException(403, "只能修改本人的出行人");
        }
        existing.setName(request.getName());
        existing.setIdCard(request.getIdCard());
        existing.setPhone(request.getPhone());
        existing.setAge(request.getAge());
        existing.setGender(request.getGender());
        existing.setUpdatedAt(LocalDateTime.now());
        travelerMapper.updateById(existing);
    }

    @Override
    public void delete(Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        Traveler existing = travelerMapper.selectById(id);
        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new BusinessException(403, "只能删除本人的出行人");
        }
        travelerMapper.deleteById(id);
    }

    @Override
    public List<Traveler> list() {
        return travelerMapper.selectByUserId(SecurityUtils.getCurrentUserId());
    }
}
