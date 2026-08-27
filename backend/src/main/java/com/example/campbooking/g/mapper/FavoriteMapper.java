package com.example.campbooking.g.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.campbooking.g.entity.Favorite;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 收藏 Mapper。基础 CRUD 由 MyBatis-Plus BaseMapper 提供，
 * 按用户查询为收藏列表接口所需的自定义方法（SQL 见 resources/mapper/FavoriteMapper.xml）。
 */
public interface FavoriteMapper extends BaseMapper<Favorite> {

    List<Favorite> selectByUserId(@Param("userId") Long userId);
}
