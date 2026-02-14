package com.credit.miniapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("user_favorites")
public class UserFavorite implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String openid;

    private Long productId;

    private LocalDateTime createdAt;

    // 关联查询字段，数据库中不存在
    @TableField(exist = false)
    private String productName;

    @TableField(exist = false)
    private String bankName;

    @TableField(exist = false)
    private String bankLogoUrl;

    @TableField(exist = false)
    private String amountRange;

    @TableField(exist = false)
    private String rateRange;
}
