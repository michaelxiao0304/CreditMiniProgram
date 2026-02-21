package com.credit.miniapp.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.credit.miniapp.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRepository extends BaseMapper<User> {
}
