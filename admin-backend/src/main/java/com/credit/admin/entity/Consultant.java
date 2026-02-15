package com.credit.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("consultant")
public class Consultant implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String phoneEncrypted;
    private Long bankId;
    private Integer status;
    private LocalDateTime createdAt;
}
