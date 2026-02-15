package com.credit.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.credit.admin.dto.PageRequest;
import com.credit.admin.dto.ProductRequest;
import com.credit.admin.entity.Bank;
import com.credit.admin.entity.Product;
import com.credit.admin.repository.BankRepository;
import com.credit.admin.repository.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BankRepository bankRepository;

    public IPage<Product> getProducts(PageRequest request) {
        Page<Product> page = new Page<>(request.getPage(), request.getSize());
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        if (request.getBankId() != null) {
            wrapper.eq(Product::getBankId, request.getBankId());
        }

        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.like(Product::getName, request.getKeyword());
        }

        if (request.getStatus() != null) {
            wrapper.eq(Product::getStatus, request.getStatus());
        }

        wrapper.orderByDesc(Product::getCreatedAt);

        IPage<Product> result = productRepository.selectPage(page, wrapper);

        // 填充银行名称
        result.getRecords().forEach(product -> {
            Bank bank = bankRepository.selectById(product.getBankId());
            if (bank != null) {
                product.setBankName(bank.getName());
            }
        });

        return result;
    }

    public Product getProductById(Long id) {
        Product product = productRepository.selectById(id);
        if (product != null && product.getBankId() != null) {
            Bank bank = bankRepository.selectById(product.getBankId());
            if (bank != null) {
                product.setBankName(bank.getName());
            }
        }
        return product;
    }

    public Product createProduct(ProductRequest request) {
        Product product = new Product();
        BeanUtils.copyProperties(request, product);
        if (product.getStatus() == null) {
            product.setStatus(1);
        }
        productRepository.insert(product);
        return product;
    }

    public Product updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.selectById(id);
        if (product == null) {
            throw new RuntimeException("产品不存在");
        }
        BeanUtils.copyProperties(request, product);
        productRepository.updateById(product);
        return product;
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public void updateStatus(Long id, Integer status) {
        Product product = productRepository.selectById(id);
        if (product != null) {
            product.setStatus(status);
            productRepository.updateById(product);
        }
    }
}
