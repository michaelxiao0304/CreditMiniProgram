package com.credit.miniapp.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDTO {
    private Long id;
    private Long bankId;
    private String bankName;
    private String bankLogoUrl;
    private String name;
    private String amountRange;
    private String rateRange;
    private String tags;
    private String description;
    private String requirements;
}
