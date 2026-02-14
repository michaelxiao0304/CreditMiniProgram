package com.credit.miniapp.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequest {
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
}
