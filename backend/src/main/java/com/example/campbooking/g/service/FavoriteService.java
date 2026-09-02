package com.example.campbooking.g.service;

import com.example.campbooking.g.dto.FavoriteRequest;
import com.example.campbooking.g.entity.Favorite;

import java.util.List;

public interface FavoriteService {

    /** 收藏（同一用户同一 target 去重） */
    void add(FavoriteRequest request);

    /** 取消收藏（只能取消本人的） */
    void delete(Long id);

    /** 当前登录用户的收藏列表（可按 targetType 筛选） */
    List<Favorite> list(String targetType);
}
