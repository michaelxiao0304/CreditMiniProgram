package com.credit.miniapp.controller;

import com.credit.miniapp.dto.ApiResponse;
import com.credit.miniapp.entity.Bank;
import com.credit.miniapp.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/banks")
public class AdminBankController {

    @Autowired
    private BankService bankService;

    @GetMapping
    public ApiResponse<List<Bank>> getBanks() {
        List<Bank> banks = bankService.getAllBanks();
        return ApiResponse.success(banks);
    }

    @GetMapping("/{id}")
    public ApiResponse<Bank> getBankById(@PathVariable Long id) {
        Bank bank = bankService.getBankById(id);
        return ApiResponse.success(bank);
    }

    @PostMapping
    public ApiResponse<Bank> createBank(@RequestBody Bank bank) {
        Bank created = bankService.createBank(bank);
        return ApiResponse.success(created);
    }

    @PutMapping("/{id}")
    public ApiResponse<Bank> updateBank(@PathVariable Long id, @RequestBody Bank bank) {
        Bank updated = bankService.updateBank(id, bank);
        return ApiResponse.success(updated);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteBank(@PathVariable Long id) {
        bankService.deleteBank(id);
        return ApiResponse.success();
    }
}
