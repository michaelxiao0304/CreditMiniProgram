package com.credit.miniapp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.credit.miniapp.dto.ApiResponse;
import com.credit.miniapp.dto.PageRequest;
import com.credit.miniapp.dto.ProductDTO;
import com.credit.miniapp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ApiResponse<IPage<ProductDTO>> getProducts(PageRequest request) {
        IPage<ProductDTO> page = productService.getProducts(request);
        return ApiResponse.success(page);
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO product = productService.getProductById(id);
        if (product == null) {
            return ApiResponse.error("产品不存在");
        }
        return ApiResponse.success(product);
    }
}
