package com.credit.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("bank")
public class Bank implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String logoUrl;
    private Integer status;
    private LocalDateTime createdAt;
}
