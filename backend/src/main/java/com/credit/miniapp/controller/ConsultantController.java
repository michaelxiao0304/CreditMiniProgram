package com.credit.miniapp.controller;

import com.credit.miniapp.dto.ApiResponse;
import com.credit.miniapp.dto.ConsultantDTO;
import com.credit.miniapp.service.ConsultantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consultant")
public class ConsultantController {

    @Autowired
    private ConsultantService consultantService;

    @GetMapping("/{productId}")
    public ApiResponse<ConsultantDTO> getConsultantByProductId(@PathVariable Long productId) {
        ConsultantDTO consultant = consultantService.getConsultantByProductId(productId);
        if (consultant == null) {
            return ApiResponse.error("暂无顾问信息");
        }
        return ApiResponse.success(consultant);
    }
}
