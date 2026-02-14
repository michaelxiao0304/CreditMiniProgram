package com.credit.miniapp.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.credit.miniapp.entity.Consultant;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConsultantRepository extends BaseMapper<Consultant> {
}
