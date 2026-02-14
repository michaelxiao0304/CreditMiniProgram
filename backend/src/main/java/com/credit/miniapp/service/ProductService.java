package com.credit.miniapp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.credit.miniapp.dto.PageRequest;
import com.credit.miniapp.dto.ProductDTO;
import com.credit.miniapp.dto.ProductRequest;
import com.credit.miniapp.entity.Bank;
import com.credit.miniapp.entity.Product;
import com.credit.miniapp.repository.BankRepository;
import com.credit.miniapp.repository.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BankRepository bankRepository;

    public IPage<ProductDTO> getProducts(PageRequest request) {
        Page<Product> page = new Page<>(request.getPage(), request.getSize());
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);

        if (request.getBankId() != null) {
            wrapper.eq(Product::getBankId, request.getBankId());
        }

        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.like(Product::getName, request.getKeyword());
        }

        wrapper.orderByDesc(Product::getCreatedAt);

        IPage<Product> result = productRepository.selectPage(page, wrapper);

        // 转换DTO
        return result.convert(product -> {
            ProductDTO dto = new ProductDTO();
            BeanUtils.copyProperties(product, dto);

            // 获取银行信息
            Bank bank = bankRepository.selectById(product.getBankId());
            if (bank != null) {
                dto.setBankName(bank.getName());
                dto.setBankLogoUrl(bank.getLogoUrl());
            }

            // 格式化额度范围
            if (product.getAmountMin() != null && product.getAmountMax() != null) {
                dto.setAmountRange(formatAmount(product.getAmountMin()) + "-" + formatAmount(product.getAmountMax()));
            }

            // 格式化利率范围
            if (product.getRateMin() != null && product.getRateMax() != null) {
                dto.setRateRange(product.getRateMin().toString() + "%-" + product.getRateMax().toString() + "%");
            }

            return dto;
        });
    }

    public ProductDTO getProductById(Long id) {
        Product product = productRepository.selectById(id);
        if (product == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();
        BeanUtils.copyProperties(product, dto);

        Bank bank = bankRepository.selectById(product.getBankId());
        if (bank != null) {
            dto.setBankName(bank.getName());
            dto.setBankLogoUrl(bank.getLogoUrl());
        }

        if (product.getAmountMin() != null && product.getAmountMax() != null) {
            dto.setAmountRange(formatAmount(product.getAmountMin()) + "-" + formatAmount(product.getAmountMax()));
        }

        if (product.getRateMin() != null && product.getRateMax() != null) {
            dto.setRateRange(product.getRateMin().toString() + "%-" + product.getRateMax().toString() + "%");
        }

        return dto;
    }

    public Product createProduct(ProductRequest request) {
        Product product = new Product();
        BeanUtils.copyProperties(request, product);
        product.setStatus(1);
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
        Product product = productRepository.selectById(id);
        if (product != null) {
            product.setStatus(0);
            productRepository.updateById(product);
        }
    }

    private String formatAmount(BigDecimal amount) {
        if (amount.compareTo(new BigDecimal("10000")) >= 0) {
            return amount.divide(new BigDecimal("10000")).stripTrailingZeros().toPlainString() + "万";
        }
        return amount.toPlainString() + "元";
    }
}
