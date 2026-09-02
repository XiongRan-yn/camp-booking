package com.example.campbooking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campbooking.common.BusinessException;
import com.example.campbooking.common.PageResult;
import com.example.campbooking.entity.Product;
import com.example.campbooking.entity.ProductSpec;
import com.example.campbooking.g.entity.Favorite;
import com.example.campbooking.g.mapper.FavoriteMapper;
import com.example.campbooking.mapper.ProductMapper;
import com.example.campbooking.mapper.ProductSpecMapper;
import com.example.campbooking.service.ProductService;
import com.example.campbooking.vo.ProductDetailVO;
import com.example.campbooking.vo.ProductVO;
import com.example.campbooking.vo.SpecVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductSpecMapper specMapper;
    private final FavoriteMapper favoriteMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProductServiceImpl(ProductMapper productMapper, ProductSpecMapper specMapper, FavoriteMapper favoriteMapper) {
        this.productMapper = productMapper;
        this.specMapper = specMapper;
        this.favoriteMapper = favoriteMapper;
    }

    @Override
    public PageResult<ProductVO> listProducts(String category, String keyword, Integer page, Integer pageSize) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, "on");
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Product::getCategory, category);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Product::getTitle, keyword);
        }
        wrapper.orderByDesc(Product::getCreatedAt);

        Page<Product> mpPage = new Page<>(page, pageSize);
        Page<Product> result = productMapper.selectPage(mpPage, wrapper);

        List<ProductVO> list = result.getRecords().stream().map(p -> {
            ProductVO vo = new ProductVO();
            vo.setId(p.getId());
            vo.setTitle(p.getTitle());
            vo.setCategory(p.getCategory());
            vo.setSubCategory(p.getSubCategory());
            vo.setCoverImage(p.getCoverImage());
            vo.setTags(parseJsonList(p.getTags()));
            vo.setMinPrice(p.getMinPrice());
            vo.setMaxPrice(p.getMaxPrice());
            vo.setIsFull(p.getIsFull() != null && p.getIsFull() == 1);
            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(list, result.getTotal(), page, pageSize);
    }

    @Override
    public ProductDetailVO getDetail(Long productId, Long userId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }

        ProductDetailVO vo = new ProductDetailVO();
        vo.setId(product.getId());
        vo.setTitle(product.getTitle());
        vo.setCategory(product.getCategory());
        vo.setSubCategory(product.getSubCategory());
        vo.setCoverImage(product.getCoverImage());
        vo.setImages(parseJsonList(product.getImages()));
        vo.setDescription(product.getDescription());
        vo.setTags(parseJsonList(product.getTags()));
        vo.setMinPrice(product.getMinPrice());
        vo.setMaxPrice(product.getMaxPrice());
        vo.setIsFull(product.getIsFull() != null && product.getIsFull() == 1);
        vo.setIsFavorited(isFavorited(userId, productId));

        LambdaQueryWrapper<ProductSpec> specWrapper = new LambdaQueryWrapper<>();
        specWrapper.eq(ProductSpec::getProductId, productId);
        List<ProductSpec> specs = specMapper.selectList(specWrapper);
        List<SpecVO> specVOList = specs.stream().map(s -> {
            SpecVO sv = new SpecVO();
            sv.setId(s.getId());
            sv.setName(s.getName());
            sv.setPrice(s.getPrice());
            sv.setOriginalPrice(s.getOriginalPrice());
            sv.setStock(s.getStock());
            return sv;
        }).collect(Collectors.toList());
        vo.setSpecs(specVOList);

        return vo;
    }

    private List<String> parseJsonList(String json) {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private boolean isFavorited(Long userId, Long productId) {
        if (userId == null) {
            return false;
        }
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId);
        wrapper.eq(Favorite::getTargetId, productId);
        return favoriteMapper.selectCount(wrapper) > 0;
    }
}
