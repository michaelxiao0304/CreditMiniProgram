package com.credit.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.credit.admin.dto.ApiResponse;
import com.credit.admin.dto.PageRequest;
import com.credit.admin.dto.ProductRequest;
import com.credit.admin.entity.Bank;
import com.credit.admin.entity.Product;
import com.credit.admin.repository.BankRepository;
import com.credit.admin.repository.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BankRepository bankRepository;

    @GetMapping
    public ApiResponse<Map<String, Object>> getProducts(PageRequest pageRequest) {
        Page<Product> page = new Page<>(pageRequest.getPage(), pageRequest.getSize());
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Product::getCreatedAt);

        Page<Product> result = productRepository.selectPage(page, wrapper);

        List<Map<String, Object>> list = result.getRecords().stream().map(this::toMap).toList();

        Map<String, Object> data = new HashMap<>();
        data.put("records", list);
        data.put("total", result.getTotal());
        data.put("pages", result.getPages());
        data.put("current", result.getCurrent());
        data.put("size", result.getSize());

        return ApiResponse.success(data);
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getProductById(@PathVariable Long id) {
        Product product = productRepository.selectById(id);
        if (product == null) {
            return ApiResponse.error("产品不存在");
        }
        return ApiResponse.success(toMap(product));
    }

    @PostMapping
    public ApiResponse<Product> createProduct(@RequestBody ProductRequest request) {
        Product product = new Product();
        BeanUtils.copyProperties(request, product);
        productRepository.insert(product);
        return ApiResponse.success(product);
    }

    @PutMapping("/{id}")
    public ApiResponse<Product> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        Product existing = productRepository.selectById(id);
        if (existing == null) {
            return ApiResponse.error("产品不存在");
        }
        BeanUtils.copyProperties(request, existing, "id", "createdAt");
        productRepository.updateById(existing);
        return ApiResponse.success(existing);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/all")
    public ApiResponse<List<Map<String, Object>>> getAllProducts() {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Product::getName);
        List<Product> products = productRepository.selectList(wrapper);
        return ApiResponse.success(products.stream().map(this::toMap).toList());
    }

    private Map<String, Object> toMap(Product product) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", product.getId());
        map.put("bankId", product.getBankId());
        map.put("name", product.getName());
        map.put("amountMin", product.getAmountMin());
        map.put("amountMax", product.getAmountMax());
        map.put("rateMin", product.getRateMin());
        map.put("rateMax", product.getRateMax());
        map.put("tags", product.getTags());
        map.put("description", product.getDescription());
        map.put("requirements", product.getRequirements());
        map.put("status", product.getStatus());
        map.put("createdAt", product.getCreatedAt());

        if (product.getBankId() != null) {
            Bank bank = bankRepository.selectById(product.getBankId());
            if (bank != null) {
                map.put("bankName", bank.getName());
            }
        }

        return map;
    }
}
