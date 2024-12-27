package com.example.shop_project.product;

import com.example.shop_project.product.dto.ProductOptionDto;
import com.example.shop_project.product.dto.ProductRequestDto;
import com.example.shop_project.product.dto.ProductResponseDto;
import com.example.shop_project.product.entity.Size;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public class ProductTestEntity {
    public ProductRequestDto prepareProductRequestDto() {
        ProductRequestDto dto = new ProductRequestDto();
        dto.setCategoryId(1L);
        dto.setProductName("Test Product");
        dto.setPrice(10000);
        dto.setDescription("Test Description");
        dto.setNickname("Test Nickname");
        dto.setOptions(List.of(new ProductOptionDto(Size.S, "Red", 10)));
        return dto;
    }

    public List<MultipartFile> prepareMockImages() {
        MockMultipartFile image1 = new MockMultipartFile("image1", "image1.jpg", "image/jpeg", "test-image-1".getBytes());
        MockMultipartFile image2 = new MockMultipartFile("image2", "image2.jpg", "image/jpeg", "test-image-2".getBytes());
        return List.of(image1, image2);
    }

    public ProductResponseDto prepareProductResponseDto() {
        ProductResponseDto responseDto = new ProductResponseDto();
        responseDto.setProductId(1L);
        responseDto.setCategoryId(1L);
        responseDto.setProductName("Test Product");
        responseDto.setDescription("Test Description");
        responseDto.setPrice(10000);
        responseDto.setSalesCount(50);
        responseDto.setViewCount(200);
        responseDto.setImageUrls(List.of("http://example.com/image1.jpg", "http://example.com/image2.jpg"));
        responseDto.setOptions(List.of(
                new ProductOptionDto(Size.S, "Red", 10),
                new ProductOptionDto(Size.S, "Blue", 5)
        )); // 옵션 설정
        responseDto.setCreatedAt(LocalDateTime.now().minusDays(1));
        responseDto.setUpdatedAt(LocalDateTime.now());
        responseDto.setNickname("TestNickname");
        return responseDto;
    }
}
