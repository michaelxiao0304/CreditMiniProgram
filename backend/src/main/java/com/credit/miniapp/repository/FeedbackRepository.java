package com.credit.miniapp.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.credit.miniapp.entity.Feedback;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FeedbackRepository extends BaseMapper<Feedback> {
}
