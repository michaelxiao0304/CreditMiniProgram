package com.credit.miniapp.controller;

import com.credit.miniapp.dto.ApiResponse;
import com.credit.miniapp.entity.Bank;
import com.credit.miniapp.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banks")
public class BankController {

    @Autowired
    private BankService bankService;

    @GetMapping
    public ApiResponse<List<Bank>> getAllBanks() {
        List<Bank> banks = bankService.getAllBanks();
        return ApiResponse.success(banks);
    }

    @GetMapping("/{id}")
    public ApiResponse<Bank> getBankById(@PathVariable Long id) {
        Bank bank = bankService.getBankById(id);
        return ApiResponse.success(bank);
    }
}
