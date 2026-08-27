package com.example.campbooking.g.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.campbooking.g.entity.Feedback;

/**
 * 意见反馈 Mapper。仅做插入，无自定义方法；
 * XML 仅声明 namespace 与 resultMap（见 resources/mapper/FeedbackMapper.xml）。
 */
public interface FeedbackMapper extends BaseMapper<Feedback> {
}
