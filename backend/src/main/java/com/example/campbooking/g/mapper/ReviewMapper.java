package com.example.campbooking.g.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.campbooking.g.entity.Review;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评价 Mapper。基础 CRUD 由 MyBatis-Plus BaseMapper 提供，
 * 按对象查询（如某酒店/某动态下的评价列表）为自定义方法（SQL 见 resources/mapper/ReviewMapper.xml）。
 */
public interface ReviewMapper extends BaseMapper<Review> {

    List<Review> selectByTarget(@Param("targetType") String targetType,
                                @Param("targetId") Long targetId);

    List<Review> selectByUserId(@Param("userId") Long userId);
}
