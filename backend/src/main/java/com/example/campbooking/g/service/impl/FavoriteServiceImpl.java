package com.example.campbooking.g.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.campbooking.common.BusinessException;
import com.example.campbooking.g.dto.FavoriteRequest;
import com.example.campbooking.g.entity.Favorite;
import com.example.campbooking.g.mapper.FavoriteMapper;
import com.example.campbooking.g.security.SecurityUtils;
import com.example.campbooking.g.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Override
    public void add(FavoriteRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();

        QueryWrapper<Favorite> qw = new QueryWrapper<>();
        qw.eq("user_id", userId)
                .eq("target_type", request.getTargetType())
                .eq("target_id", request.getTargetId());
        if (favoriteMapper.selectCount(qw) > 0) {
            throw new BusinessException(409, "已经收藏过了");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setTargetType(request.getTargetType());
        favorite.setTargetId(request.getTargetId());
        favorite.setCreatedAt(LocalDateTime.now());
        favoriteMapper.insert(favorite);
    }

    @Override
    public void delete(Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        Favorite existing = favoriteMapper.selectById(id);
        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new BusinessException(403, "只能取消本人的收藏");
        }
        favoriteMapper.deleteById(id);
    }

    @Override
    public List<Favorite> list() {
        return favoriteMapper.selectByUserId(SecurityUtils.getCurrentUserId());
    }
}
