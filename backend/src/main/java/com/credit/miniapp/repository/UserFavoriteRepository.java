package com.credit.miniapp.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.credit.miniapp.entity.UserFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserFavoriteRepository extends BaseMapper<UserFavorite> {

    @Select("SELECT uf.*, p.name as productName, b.name as bankName, b.logo_url as bankLogoUrl, " +
            "CONCAT(p.amount_min, '-', p.amount_max) as amountRange, " +
            "CONCAT(p.rate_min, '-', p.rate_max) as rateRange " +
            "FROM user_favorites uf " +
            "LEFT JOIN product p ON uf.product_id = p.id " +
            "LEFT JOIN bank b ON p.bank_id = b.id " +
            "WHERE uf.openid = #{openid} " +
            "ORDER BY uf.created_at DESC")
    List<UserFavorite> findByOpenid(String openid);
}
