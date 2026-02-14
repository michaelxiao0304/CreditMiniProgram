package com.credit.miniapp.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.credit.miniapp.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProductRepository extends BaseMapper<Product> {

    @Select("SELECT p.*, b.name as bankName, b.logo_url as bankLogoUrl " +
            "FROM product p " +
            "LEFT JOIN bank b ON p.bank_id = b.id " +
            "WHERE p.status = 1 " +
            "ORDER BY p.created_at DESC")
    Product findAllProducts();
}
