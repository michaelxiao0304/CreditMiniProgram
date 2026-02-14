package com.credit.miniapp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.credit.miniapp.dto.ApiResponse;
import com.credit.miniapp.dto.PageRequest;
import com.credit.miniapp.dto.ProductDTO;
import com.credit.miniapp.dto.ProductRequest;
import com.credit.miniapp.entity.Product;
import com.credit.miniapp.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

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
        return ApiResponse.success(product);
    }

    @PostMapping
    public ApiResponse<Product> createProduct(@RequestBody ProductRequest request) {
        Product product = productService.createProduct(request);
        return ApiResponse.success(product);
    }

    @PutMapping("/{id}")
    public ApiResponse<Product> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        Product product = productService.updateProduct(id, request);
        return ApiResponse.success(product);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResponse.success();
    }
}
