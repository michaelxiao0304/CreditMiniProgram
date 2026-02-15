package com.credit.admin.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.credit.admin.entity.AdminUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminUserRepository extends BaseMapper<AdminUser> {
    default AdminUser findByUsername(String username) {
        return selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AdminUser>()
            .eq(AdminUser::getUsername, username));
    }
}
