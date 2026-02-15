package com.credit.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.credit.admin.entity.Bank;
import com.credit.admin.entity.Consultant;
import com.credit.admin.repository.BankRepository;
import com.credit.admin.repository.ConsultantRepository;
import com.credit.admin.util.CryptoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConsultantService {

    @Autowired
    private ConsultantRepository consultantRepository;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private CryptoUtil cryptoUtil;

    public List<Map<String, Object>> getAllConsultants() {
        LambdaQueryWrapper<Consultant> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Consultant::getCreatedAt);
        List<Consultant> consultants = consultantRepository.selectList(wrapper);
        return consultants.stream().map(this::toMap).toList();
    }

    public Map<String, Object> getConsultantById(Long id) {
        Consultant consultant = consultantRepository.selectById(id);
        return consultant != null ? toMap(consultant) : null;
    }

    public Consultant createConsultant(Consultant consultant) {
        if (consultant.getPhoneEncrypted() != null && !consultant.getPhoneEncrypted().isEmpty()) {
            consultant.setPhoneEncrypted(cryptoUtil.encrypt(consultant.getPhoneEncrypted()));
        } else {
            consultant.setPhoneEncrypted("");
        }
        if (consultant.getStatus() == null) {
            consultant.setStatus(1);
        }
        consultantRepository.insert(consultant);
        return consultant;
    }

    public Consultant updateConsultant(Long id, Consultant consultant) {
        Consultant existing = consultantRepository.selectById(id);
        if (existing == null) {
            throw new RuntimeException("顾问不存在");
        }
        if (consultant.getName() != null) {
            existing.setName(consultant.getName());
        }
        if (consultant.getPhoneEncrypted() != null) {
            existing.setPhoneEncrypted(cryptoUtil.encrypt(consultant.getPhoneEncrypted()));
        }
        if (consultant.getBankId() != null) {
            existing.setBankId(consultant.getBankId());
        }
        consultantRepository.updateById(existing);
        return existing;
    }

    public void deleteConsultant(Long id) {
        consultantRepository.deleteById(id);
    }

    private Map<String, Object> toMap(Consultant consultant) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", consultant.getId());
        map.put("name", consultant.getName());
        map.put("bankId", consultant.getBankId());
        map.put("status", consultant.getStatus());
        map.put("createdAt", consultant.getCreatedAt());

        try {
            String phone = cryptoUtil.decrypt(consultant.getPhoneEncrypted());
            map.put("phone", phone);
            map.put("phoneMasked", cryptoUtil.maskPhone(phone));
        } catch (Exception e) {
            map.put("phone", "");
            map.put("phoneMasked", "暂无");
        }

        if (consultant.getBankId() != null) {
            Bank bank = bankRepository.selectById(consultant.getBankId());
            if (bank != null) {
                map.put("bankName", bank.getName());
            }
        }

        return map;
    }
}
