package com.credit.admin.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.credit.admin.entity.Consultant;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConsultantRepository extends BaseMapper<Consultant> {
}
