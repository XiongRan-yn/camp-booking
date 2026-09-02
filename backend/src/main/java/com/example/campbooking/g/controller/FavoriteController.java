package com.example.campbooking.g.controller;

import com.example.campbooking.common.Result;
import com.example.campbooking.g.dto.FavoriteRequest;
import com.example.campbooking.g.entity.Favorite;
import com.example.campbooking.g.service.FavoriteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /** 收藏 */
    @PostMapping
    public Result<Void> add(@Valid @RequestBody FavoriteRequest request) {
        favoriteService.add(request);
        return Result.success();
    }

    /** 取消收藏 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        favoriteService.delete(id);
        return Result.success();
    }

    /** 当前用户收藏列表：type 可选 hotel | camp，不传返回全部 */
    @GetMapping
    public Result<List<Favorite>> list(@RequestParam(required = false) String type) {
        return Result.success(favoriteService.list(type));
    }
}
