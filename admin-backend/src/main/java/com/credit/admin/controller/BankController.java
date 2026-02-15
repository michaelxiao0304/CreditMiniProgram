package com.credit.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.credit.admin.dto.ApiResponse;
import com.credit.admin.dto.PageRequest;
import com.credit.admin.entity.Bank;
import com.credit.admin.repository.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/banks")
public class BankController {

    @Autowired
    private BankRepository bankRepository;

    @GetMapping
    public ApiResponse<Map<String, Object>> getBanks(PageRequest pageRequest) {
        Page<Bank> page = new Page<>(pageRequest.getPage(), pageRequest.getSize());
        LambdaQueryWrapper<Bank> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Bank::getCreatedAt);

        Page<Bank> result = bankRepository.selectPage(page, wrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("pages", result.getPages());
        data.put("current", result.getCurrent());
        data.put("size", result.getSize());

        return ApiResponse.success(data);
    }

    @GetMapping("/{id}")
    public ApiResponse<Bank> getBankById(@PathVariable Long id) {
        Bank bank = bankRepository.selectById(id);
        if (bank == null) {
            return ApiResponse.error("银行不存在");
        }
        return ApiResponse.success(bank);
    }

    @PostMapping
    public ApiResponse<Bank> createBank(@RequestBody Bank bank) {
        if (bank.getStatus() == null) {
            bank.setStatus(1);
        }
        bankRepository.insert(bank);
        return ApiResponse.success(bank);
    }

    @PutMapping("/{id}")
    public ApiResponse<Bank> updateBank(@PathVariable Long id, @RequestBody Bank bank) {
        Bank existing = bankRepository.selectById(id);
        if (existing == null) {
            return ApiResponse.error("银行不存在");
        }
        existing.setName(bank.getName());
        existing.setLogoUrl(bank.getLogoUrl());
        existing.setStatus(bank.getStatus());
        bankRepository.updateById(existing);
        return ApiResponse.success(existing);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteBank(@PathVariable Long id) {
        bankRepository.deleteById(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/all")
    public ApiResponse<List<Bank>> getAllBanks() {
        LambdaQueryWrapper<Bank> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bank::getStatus, 1);
        wrapper.orderByAsc(Bank::getName);
        List<Bank> banks = bankRepository.selectList(wrapper);
        return ApiResponse.success(banks);
    }
}
