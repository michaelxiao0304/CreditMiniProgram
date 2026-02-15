package com.credit.admin.dto;

import lombok.Data;

@Data
public class PageRequest {
    private Integer page = 1;
    private Integer size = 20;
    private Long bankId;
    private String keyword;
    private Integer status;
}
