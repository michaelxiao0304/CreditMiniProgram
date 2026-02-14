package com.credit.miniapp.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String code;
    private String userInfo;
}
