package com.credit.miniapp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.credit.miniapp.entity.Bank;
import com.credit.miniapp.repository.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankService {

    @Autowired
    private BankRepository bankRepository;

    public List<Bank> getAllBanks() {
        LambdaQueryWrapper<Bank> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bank::getStatus, 1);
        wrapper.orderByAsc(Bank::getName);
        return bankRepository.selectList(wrapper);
    }

    public Bank getBankById(Long id) {
        return bankRepository.selectById(id);
    }

    public Bank createBank(Bank bank) {
        bank.setStatus(1);
        bankRepository.insert(bank);
        return bank;
    }

    public Bank updateBank(Long id, Bank bank) {
        Bank existing = bankRepository.selectById(id);
        if (existing == null) {
            throw new RuntimeException("银行不存在");
        }
        existing.setName(bank.getName());
        existing.setLogoUrl(bank.getLogoUrl());
        bankRepository.updateById(existing);
        return existing;
    }

    public void deleteBank(Long id) {
        Bank bank = bankRepository.selectById(id);
        if (bank != null) {
            bank.setStatus(0);
            bankRepository.updateById(bank);
        }
    }
}
