package com.credit.miniapp.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.credit.miniapp.entity.Bank;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BankRepository extends BaseMapper<Bank> {
}
