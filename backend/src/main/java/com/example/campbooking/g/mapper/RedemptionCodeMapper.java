package com.example.campbooking.g.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.campbooking.g.entity.RedemptionCode;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 兑换码 Mapper。基础 CRUD 由 BaseMapper 提供；
 * 兑换时按 code 查码、核销标记已使用为自定义方法（SQL 见 resources/mapper/RedemptionCodeMapper.xml）。
 */
public interface RedemptionCodeMapper extends BaseMapper<RedemptionCode> {

    RedemptionCode selectByCode(@Param("code") String code);

    int markUsed(@Param("id") Long id,
                 @Param("usedBy") Long usedBy,
                 @Param("usedAt") LocalDateTime usedAt);
}
