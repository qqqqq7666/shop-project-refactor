package com.example.shop_project.product.controller;

import com.example.shop_project.product.dto.ProductRequestDto;
import com.example.shop_project.product.dto.ProductResponseDto;
import com.example.shop_project.product.entity.ProductImage;
import com.example.shop_project.product.repository.productImageRepository;
import com.example.shop_project.product.service.ImageService;
import com.example.shop_project.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ImageService imageService;
    private final productImageRepository imageRepository;


    @PostMapping
    public ResponseEntity<?> createProduct(
            @Valid @RequestPart("productRequestDto") ProductRequestDto productRequestDto,
            @RequestPart("images") List<MultipartFile> images,
            BindingResult bindingResult
    ) {
        log.debug("### productRequestDto : {}", productRequestDto);
        log.debug("### images : {}", images);
        log.debug("### bindingResult : {}", bindingResult);


        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        productService.createProduct(productRequestDto, images);

        // JSON 형식의 응답 반환
        Map<String, String> response = new HashMap<>();
        response.put("message", "Product created successfully!");
        return ResponseEntity.ok(response);
    }

    // 부분 수정 메서드
    @PatchMapping("/{productId}")
    public ResponseEntity<?> updateProductPartially(
            @PathVariable Long productId,
            @RequestPart(value = "updates") String updatesJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        // JSON 문자열을 Map으로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> updates = null;
        try {
            updates = objectMapper.readValue(updatesJson, Map.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON format for updates");
        }

        ProductResponseDto updatedProduct = productService.partialUpdateProduct(productId, updates, images);
        return ResponseEntity.ok(updatedProduct);
    }


    // 상품 삭제 API
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok().body("Product deleted successfully");
    }



}
