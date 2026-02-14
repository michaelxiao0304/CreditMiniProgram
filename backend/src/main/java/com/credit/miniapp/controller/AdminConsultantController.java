package com.credit.miniapp.controller;

import com.credit.miniapp.dto.ApiResponse;
import com.credit.miniapp.dto.ConsultantDTO;
import com.credit.miniapp.entity.Consultant;
import com.credit.miniapp.service.ConsultantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/consultants")
public class AdminConsultantController {

    @Autowired
    private ConsultantService consultantService;

    @GetMapping
    public ApiResponse<List<ConsultantDTO>> getConsultants() {
        List<ConsultantDTO> consultants = consultantService.getAllConsultants();
        return ApiResponse.success(consultants);
    }

    @PostMapping
    public ApiResponse<Consultant> createConsultant(@RequestBody Consultant consultant) {
        Consultant created = consultantService.createConsultant(consultant);
        return ApiResponse.success(created);
    }

    @PutMapping("/{id}")
    public ApiResponse<Consultant> updateConsultant(@PathVariable Long id, @RequestBody Consultant consultant) {
        Consultant updated = consultantService.updateConsultant(id, consultant);
        return ApiResponse.success(updated);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteConsultant(@PathVariable Long id) {
        consultantService.deleteConsultant(id);
        return ApiResponse.success();
    }
}
