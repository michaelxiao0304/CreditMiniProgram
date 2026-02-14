package com.credit.miniapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long bankId;

    private String name;

    private BigDecimal amountMin;

    private BigDecimal amountMax;

    private BigDecimal rateMin;

    private BigDecimal rateMax;

    private String tags;

    private String description;

    private String requirements;

    private Integer status;

    private LocalDateTime createdAt;

    // 关联查询字段，数据库中不存在
    @TableField(exist = false)
    private String bankName;

    @TableField(exist = false)
    private String bankLogoUrl;
}
