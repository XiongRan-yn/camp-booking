package com.example.campbooking.g.controller;

import com.example.campbooking.common.Result;
import com.example.campbooking.g.dto.TravelerRequest;
import com.example.campbooking.g.entity.Traveler;
import com.example.campbooking.g.service.TravelerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/travelers")
public class TravelerController {

    @Autowired
    private TravelerService travelerService;

    /** 新增出行人 */
    @PostMapping
    public Result<Long> add(@Valid @RequestBody TravelerRequest request) {
        return Result.success(travelerService.add(request));
    }

    /** 修改出行人（id 必填） */
    @PutMapping
    public Result<Void> update(@Valid @RequestBody TravelerRequest request) {
        travelerService.update(request);
        return Result.success();
    }

    /** 删除出行人 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        travelerService.delete(id);
        return Result.success();
    }

    /** 当前用户出行人列表 */
    @GetMapping
    public Result<List<Traveler>> list() {
        return Result.success(travelerService.list());
    }
}
