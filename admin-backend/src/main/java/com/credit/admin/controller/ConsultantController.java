package com.credit.admin.controller;

import com.credit.admin.dto.ApiResponse;
import com.credit.admin.entity.Consultant;
import com.credit.admin.service.ConsultantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/consultants")
public class ConsultantController {

    @Autowired
    private ConsultantService consultantService;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> getConsultants() {
        return ApiResponse.success(consultantService.getAllConsultants());
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getConsultantById(@PathVariable Long id) {
        Map<String, Object> consultant = consultantService.getConsultantById(id);
        if (consultant == null) {
            return ApiResponse.error("顾问不存在");
        }
        return ApiResponse.success(consultant);
    }

    @PostMapping
    public ApiResponse<Consultant> createConsultant(@RequestBody Consultant consultant) {
        return ApiResponse.success(consultantService.createConsultant(consultant));
    }

    @PutMapping("/{id}")
    public ApiResponse<Consultant> updateConsultant(@PathVariable Long id, @RequestBody Consultant consultant) {
        return ApiResponse.success(consultantService.updateConsultant(id, consultant));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteConsultant(@PathVariable Long id) {
        consultantService.deleteConsultant(id);
        return ApiResponse.success(null);
    }
}
