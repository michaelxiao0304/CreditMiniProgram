package com.credit.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("feedback")
public class Feedback implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String openid;
    private String content;
    private String contact;
    private Integer status;
    private LocalDateTime createdAt;
}
