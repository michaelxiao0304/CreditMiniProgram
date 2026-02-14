package com.credit.miniapp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.credit.miniapp.repository")
public class CreditMiniAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(CreditMiniAppApplication.class, args);
    }
}
