package com.example.shop_project.product.service;

import com.example.shop_project.product.dto.ProductImageDto;
import com.example.shop_project.product.dto.ProductOptionDto;
import com.example.shop_project.product.dto.ProductRequestDto;
import com.example.shop_project.product.dto.ProductResponseDto;
import com.example.shop_project.product.entity.*;
import com.example.shop_project.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
                .price(productRequestDto.getPrice())
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
                .price(product.getPrice())
                .description(product.getDescription())
                .viewCount(product.getViewCount())
                .salesCount(product.getSalesCount())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
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
            if (option.getStockQuantity() < 1) {
                throw new InvalidProductException("재고는 1 이상이어야 합니다.");
            }
        }
    }


    public List<ProductResponseDto> getProductListSortedBy(String sortBy) {
        List<Product> products;
        if ("viewCount".equalsIgnoreCase(sortBy)) {
            products = productRepository.findAll(Sort.by(Sort.Direction.DESC, "viewCount"));
        } else {
            products = productRepository.findAll(Sort.by(Sort.Direction.DESC, "salesCount"));
        }

        // Product -> ProductResponseDto 변환
        return products.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public Page<ProductResponseDto> getProductList(String search, String sort, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sort));

        // 검색 기능 적용
        Page<Product> products = productRepository.findByProductNameContaining(search, pageable);

        // Product -> ProductResponseDto로 변환
        return products.map(product -> ProductResponseDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .price(product.getPrice())
                .viewCount(product.getViewCount())
                .salesCount(product.getSalesCount())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .imageUrls(product.getImages().stream()
                        .map(ProductImage::getImageUrl)
                        .toList())
                .build());
    }

    // 상품 목록 불러오기(키워드로 불러오기)
    public Page<ProductResponseDto> getProductList(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // 검색 기능 적용
        Page<Product> products = productRepository.findByProductNameContaining(keyword, pageable);

        // Product -> ProductResponseDto로 변환
        return products.map(product -> ProductResponseDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .price(product.getPrice())
                .viewCount(product.getViewCount())
                .salesCount(product.getSalesCount())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .imageUrls(product.getImages().stream()
                        .map(ProductImage::getImageUrl)
                        .toList())
                .build());
    }

    public ProductResponseDto getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다. ID: " + productId));

        return mapToResponseDto(product);
    }

    // 상품 상세 정보 불러오기
    public ProductResponseDto getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        return mapToResponseDto(product);
    }
    

    // 상품 삭제
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    // 부분 업데이트
    public ProductResponseDto partialUpdateProduct(Long productId, Map<String, Object> updates, List<MultipartFile> images) {
        // 상품 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        // 업데이트할 필드 적용
        updates.forEach((key, value) -> {
            switch (key) {
                case "productName":
                    if (value instanceof String) {
                        product.setProductName((String) value);
                    }
                    break;
                case "categoryName":
                    if (value instanceof String) {
                        product.setCategoryName((String) value);
                    }
                    break;
                case "description":
                    if (value instanceof String) {
                        product.setDescription((String) value);
                    }
                    break;
                case "price":
                    if (value instanceof Number) {
                        product.setPrice(((Number) value).intValue());
                    }
                    break;
                case "options":
                    if (value instanceof List<?>) {
                        List<Map<String, Object>> optionList = (List<Map<String, Object>>) value;
                        product.getOptions().clear();
                        optionList.forEach(option -> {
                            String size = (String) option.get("size");
                            String color = (String) option.get("color");
                            Integer stockQuantity = (Integer) option.get("stockQuantity");

                            if (size != null && color != null && stockQuantity != null) {
                                ProductOption newOption = new ProductOption();
                                newOption.setSize(Size.valueOf(size));
                                newOption.setColor(color);
                                newOption.setStockQuantity(stockQuantity);
                                newOption.setProduct(product);
                                product.getOptions().add(newOption);
                            }
                        });
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Invalid field: " + key);
            }
        });

        // 이미지 업데이트 로직
        if (images != null && !images.isEmpty()) {
            product.getImages().clear(); // 기존 이미지 제거
            for (int i = 0; i < images.size(); i++) {
                String imagePath = imageService.uploadImage("products", images.get(i));
                ProductImage productImage = new ProductImage();
                productImage.setImageUrl(imagePath);
                productImage.setSortOrder(i + 1);
                productImage.setProduct(product);
                product.getImages().add(productImage);
            }
        }

        product.setUpdatedAt(LocalDateTime.now());

        // 변경된 상품 저장
        Product updatedProduct = productRepository.save(product);

        // 수정된 상품 정보 반환
        return mapToResponseDto(updatedProduct);
    }

}
