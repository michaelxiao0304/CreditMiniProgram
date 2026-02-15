package com.credit.admin.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.credit.admin.entity.Bank;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BankRepository extends BaseMapper<Bank> {
}
