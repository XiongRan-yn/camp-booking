package com.example.campbooking.g.service;

import com.example.campbooking.g.dto.TravelerRequest;
import com.example.campbooking.g.entity.Traveler;

import java.util.List;

public interface TravelerService {

    /** 新增出行人，返回新记录 id */
    Long add(TravelerRequest request);

    /** 修改出行人（id 必填，且只能改本人的） */
    void update(TravelerRequest request);

    /** 删除出行人（只能删本人的） */
    void delete(Long id);

    /** 当前登录用户的出行人列表 */
    List<Traveler> list();
}
