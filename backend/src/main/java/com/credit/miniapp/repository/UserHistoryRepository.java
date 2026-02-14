package com.credit.miniapp.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.credit.miniapp.entity.UserHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserHistoryRepository extends BaseMapper<UserHistory> {

    @Select("SELECT uh.*, p.name as productName, b.name as bankName, b.logo_url as bankLogoUrl, " +
            "CONCAT(p.amount_min, '-', p.amount_max) as amountRange, " +
            "CONCAT(p.rate_min, '-', p.rate_max) as rateRange " +
            "FROM user_history uh " +
            "LEFT JOIN product p ON uh.product_id = p.id " +
            "LEFT JOIN bank b ON p.bank_id = b.id " +
            "WHERE uh.openid = #{openid} " +
            "ORDER BY uh.created_at DESC " +
            "LIMIT 50")
    List<UserHistory> findByOpenid(String openid);
}
