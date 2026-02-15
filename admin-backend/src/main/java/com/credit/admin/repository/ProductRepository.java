package com.credit.admin.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.credit.admin.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductRepository extends BaseMapper<Product> {
}
