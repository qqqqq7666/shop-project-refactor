package com.example.shop_project.product.service;

import com.example.shop_project.product.dto.ProductImageDto;
import com.example.shop_project.product.dto.ProductOptionDto;
import com.example.shop_project.product.dto.ProductRequestDto;
import com.example.shop_project.product.dto.ProductResponseDto;
import com.example.shop_project.product.entity.*;
import com.example.shop_project.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ImageService imageService;

    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto productRequestDto, List<MultipartFile> images) {
        validateProductRequest(productRequestDto);

        Product product = Product.builder()
                .categoryName(productRequestDto.getCategoryName())
                .productName(productRequestDto.getProductName())
                .description(productRequestDto.getDescription())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<ProductOption> productOptions = productRequestDto.getOptions().stream().map(optionDto -> {
            ProductOption option = new ProductOption();
            option.setSize(optionDto.getSize());
            option.setColor(optionDto.getColor());
            option.setStockQuantity(optionDto.getStockQuantity());
            option.setProduct(product);
            return option;
        }).collect(Collectors.toList());

        List<ProductImage> productImages = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            MultipartFile image = images.get(i);
            String imagePath = imageService.uploadImage("products", image);
            ProductImage productImage = new ProductImage();
            productImage.setImageUrl(imagePath);
            productImage.setSortOrder(i + 1);
            productImage.setProduct(product);
            productImages.add(productImage);
        }

        product.setOptions(productOptions);
        product.setImages(productImages);

        Product savedProduct = productRepository.save(product);

        return mapToResponseDto(savedProduct);
    }

    private ProductResponseDto mapToResponseDto(Product product) {
        return ProductResponseDto.builder()
                .productId(product.getProductId())
                .categoryName(product.getCategoryName())
                .productName(product.getProductName())
                .description(product.getDescription())
                .imageUrls(product.getImages().stream()
                        .map(ProductImage::getImageUrl)
                        .collect(Collectors.toList()))
                .options(product.getOptions().stream()
                        .map(option -> {
                            ProductOptionDto optionDto = new ProductOptionDto();
                            optionDto.setSize(option.getSize());
                            optionDto.setColor(option.getColor());
                            optionDto.setStockQuantity(option.getStockQuantity());
                            return optionDto;
                        }).collect(Collectors.toList()))
                .build();
    }

    private void validateProductRequest(ProductRequestDto productRequestDto) {
        // 제품 이름 검증
        if (productRequestDto.getProductName() == null || productRequestDto.getProductName().isBlank()) {
            throw new InvalidProductException("제품 이름은 필수 입력 항목입니다.");
        }
        if (productRequestDto.getProductName().length() > 20) {
            throw new InvalidProductException("제품 이름은 최대 20자까지 가능합니다.");
        }

        // 카테고리 검증
        if (productRequestDto.getCategoryName() == null || productRequestDto.getCategoryName().isBlank()) {
            throw new InvalidProductException("카테고리는 필수 입력 항목입니다.");
        }

        // 상세 설명 검증
        if (productRequestDto.getDescription() == null || productRequestDto.getDescription().isBlank()) {
            throw new InvalidProductException("상세 설명은 필수 입력 항목입니다.");
        }
        if (productRequestDto.getDescription().length() > 100) {
            throw new InvalidProductException("상세 설명은 최대 100자까지 가능합니다.");
        }

        // 옵션 검증
        if (productRequestDto.getOptions() == null || productRequestDto.getOptions().isEmpty()) {
            throw new InvalidProductException("옵션은 최소 1개 이상 입력해야 합니다.");
        }
        for (ProductOptionDto option : productRequestDto.getOptions()) {
            if (option.getSize() == null) {
                throw new InvalidProductException("사이즈는 선택 필수 항목입니다.");
            }
            if (option.getColor() == null || option.getColor().isBlank()) {
                throw new InvalidProductException("색상은 필수 입력 항목입니다.");
            }
            if (option.getStockQuantity() < 1) {
                throw new InvalidProductException("재고는 1 이상이어야 합니다.");
            }
        }
    }

}
