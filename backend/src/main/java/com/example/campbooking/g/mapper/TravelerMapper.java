package com.example.campbooking.g.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.campbooking.g.entity.Traveler;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 出行人 Mapper。基础 CRUD 由 MyBatis-Plus BaseMapper 提供，
 * 按用户查询为用户侧列表接口所需的自定义方法（SQL 见 resources/mapper/TravelerMapper.xml）。
 */
public interface TravelerMapper extends BaseMapper<Traveler> {

    List<Traveler> selectByUserId(@Param("userId") Long userId);
}
