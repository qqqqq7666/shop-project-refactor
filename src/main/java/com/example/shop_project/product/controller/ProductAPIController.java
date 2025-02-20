package com.example.shop_project.product.controller;

import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.service.MemberService;
import com.example.shop_project.product.dto.ProductOptionDto;
import com.example.shop_project.product.dto.ProductRequestDto;
import com.example.shop_project.product.dto.ProductResponseDto;
import com.example.shop_project.product.entity.Size;
import com.example.shop_project.product.repository.productImageRepository;
import com.example.shop_project.product.service.ImageService;
import com.example.shop_project.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductAPIController {

    private final ProductService productService;
    private final MemberService memberService;
    private final ImageService imageService;
    private final productImageRepository imageRepository;


    @PostMapping
    public ResponseEntity<?> createProduct(
            @Valid @RequestPart("productRequestDto") ProductRequestDto productRequestDto,
            @RequestPart("images") List<MultipartFile> images,
            BindingResult bindingResult,
            Principal principal)
    {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            log.debug("### erros : {}", errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        // 이메일을 통해 닉네임을 가져옴
        String email = principal.getName(); // 로그인된 사용자의 이메일
        Member member = memberService.findByEmail(email);
        String nickname = member.getNickname(); // 닉네임 조회

        // 닉네임을 ProductRequestDto에 설정
        productRequestDto.setNickname(nickname);

        productService.createProduct(productRequestDto, images);

        // JSON 형식의 응답 반환
        Map<String, String> response = new HashMap<>();
        response.put("message", "상품 등록 성공");
        return ResponseEntity.ok(response);
    }

    // 부분 수정 메서드
    @PatchMapping("/{productId}")
    public ResponseEntity<?> updateProductPartially(
            @PathVariable Long productId,
            @RequestPart(value = "updates") String updatesJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
            /* @RequestParam("existingImageUrls") List<String> existingImageUrls */
            ) {
        // JSON 문자열을 Map으로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> updates = null;
        try {
            updates = objectMapper.readValue(updatesJson, Map.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON format for updates");
        }

        ProductResponseDto updatedProduct = productService.partialUpdateProduct(productId, updates, images /* existingImageUrls */);
        return ResponseEntity.ok(updatedProduct);
    }


    // 상품 삭제 API
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok().body("Product deleted successfully");
    }

    // 재고 검사 API
    @GetMapping("/available-sizes")
    public ResponseEntity<?> getAvailableSizes(@RequestParam("productId") Long productId,
                                               @RequestParam("color") String color) {
        List<ProductOptionDto> availableSizes = productService.getAvailableSizes(productId, color);
        return ResponseEntity.ok(availableSizes);
    }



}
