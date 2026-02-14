package com.credit.miniapp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.credit.miniapp.dto.ConsultantDTO;
import com.credit.miniapp.entity.Bank;
import com.credit.miniapp.entity.Consultant;
import com.credit.miniapp.repository.BankRepository;
import com.credit.miniapp.repository.ConsultantRepository;
import com.credit.miniapp.config.SpringContextHolder;
import com.credit.miniapp.util.CryptoUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultantService {

    @Autowired
    private ConsultantRepository consultantRepository;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private CryptoUtil cryptoUtil;

    public ConsultantDTO getConsultantByProductId(Long productId) {
        // 根据产品获取顾问
        ProductService productService = SpringContextHolder.getBean(ProductService.class);
        var product = productService.getProductById(productId);
        if (product == null || product.getBankId() == null) {
            return null;
        }

        LambdaQueryWrapper<Consultant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Consultant::getBankId, product.getBankId());
        wrapper.eq(Consultant::getStatus, 1);
        wrapper.last("LIMIT 1");

        Consultant consultant = consultantRepository.selectOne(wrapper);
        if (consultant == null) {
            return null;
        }

        return buildConsultantDTO(consultant);
    }

    public List<ConsultantDTO> getAllConsultants() {
        LambdaQueryWrapper<Consultant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Consultant::getStatus, 1);
        List<Consultant> consultants = consultantRepository.selectList(wrapper);

        return consultants.stream()
                .map(this::buildConsultantDTO)
                .collect(Collectors.toList());
    }

    public Consultant createConsultant(Consultant consultant) {
        // 加密手机号
        if (consultant.getPhoneEncrypted() != null && !consultant.getPhoneEncrypted().isEmpty()) {
            consultant.setPhoneEncrypted(cryptoUtil.encrypt(consultant.getPhoneEncrypted()));
        }
        consultant.setStatus(1);
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
        Consultant consultant = consultantRepository.selectById(id);
        if (consultant != null) {
            consultant.setStatus(0);
            consultantRepository.updateById(consultant);
        }
    }

    private ConsultantDTO buildConsultantDTO(Consultant consultant) {
        ConsultantDTO dto = new ConsultantDTO();
        dto.setId(consultant.getId());
        dto.setName(consultant.getName());

        // 解密手机号并脱敏
        try {
            String phone = cryptoUtil.decrypt(consultant.getPhoneEncrypted());
            dto.setPhone(cryptoUtil.maskPhone(phone));
        } catch (Exception e) {
            dto.setPhone("暂无");
        }

        if (consultant.getBankId() != null) {
            Bank bank = bankRepository.selectById(consultant.getBankId());
            if (bank != null) {
                dto.setBankName(bank.getName());
            }
        }

        return dto;
    }
}
