package com.example.shop_project.product.service;

import com.example.shop_project.order.dto.OrderDetailDto;
import com.example.shop_project.product.dto.ProductImageDto;
import com.example.shop_project.product.dto.ProductOptionDto;
import com.example.shop_project.product.dto.ProductRequestDto;
import com.example.shop_project.product.dto.ProductResponseDto;
import com.example.shop_project.product.entity.*;
import com.example.shop_project.product.repository.ProductOptionRepository;
import com.example.shop_project.product.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.bridge.MessageUtil;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ImageService imageService;
    private final ProductOptionRepository productOptionRepository;

    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto productRequestDto, List<MultipartFile> images) {
        validateProductRequest(productRequestDto);

        Product product = Product.builder()
                .categoryId(productRequestDto.getCategoryId())
                .productName(productRequestDto.getProductName())
                .price(productRequestDto.getPrice())
                .description(productRequestDto.getDescription())
                .nickname(productRequestDto.getNickname())
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

    public ProductResponseDto mapToResponseDto(Product product) {
        return ProductResponseDto.builder()
                .productId(product.getProductId())
                .categoryId(product.getCategoryId())
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

    public ProductResponseDto mapToResponseDto(Product product, boolean isOutOfStock) {
        return ProductResponseDto.builder()
                .productId(product.getProductId())
                .categoryId(product.getCategoryId())
                .productName(product.getProductName())
                .price(product.getPrice())
                .description(product.getDescription())
                .viewCount(product.getViewCount())
                .salesCount(product.getSalesCount())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .nickname(product.getNickname())
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
                .isOutOfStock(isOutOfStock)
                .build();
    }

    @Transactional
    public void validateProductRequest(ProductRequestDto productRequestDto) {
        // 제품 이름 검증
        if (productRequestDto.getProductName() == null || productRequestDto.getProductName().isBlank()) {
            throw new InvalidProductException("제품 이름은 필수 입력 항목입니다.");
        }
        if (productRequestDto.getProductName().length() > 50) {
            throw new InvalidProductException("제품 이름은 최대 50자까지 가능합니다.");
        }

        // 카테고리 검증
        if (productRequestDto.getCategoryId() == null) {
            throw new InvalidProductException("카테고리는 필수 입력 항목입니다.");
        }

        // 상세 설명 검증
        if (productRequestDto.getDescription() == null || productRequestDto.getDescription().isBlank()) {
            throw new InvalidProductException("상세 설명은 필수 입력 항목입니다.");
        }
        if (productRequestDto.getDescription().length() > 500) {
            throw new InvalidProductException("상세 설명은 최대 500자까지 가능합니다.");
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
                throw new InvalidProductException("색상은 선택 필수 항목입니다.");
            }
            if (option.getStockQuantity() < 1) {
                throw new InvalidProductException("재고는 1 이상이어야 합니다.");
            }
        }
    }


    @Transactional
    public Page<ProductResponseDto> getProductList(Long categoryId, String search, String sort, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sort));

        Page<Product> products;
        if (categoryId != null && !search.isEmpty()) {
            // 카테고리와 검색 조건이 모두 있는 경우
            products = productRepository.findByCategoryIdAndProductNameContaining(categoryId, search, pageable);
        } else if (categoryId != null) {
            // 카테고리만 있는 경우
            products = productRepository.findByCategoryId(categoryId, pageable);
        } else if (!search.isEmpty()) {
            // 검색 조건만 있는 경우
            products = productRepository.findByProductNameContaining(search, pageable);
        } else {
            // 조건이 없는 경우
            products = productRepository.findAll(pageable);
        }

        // Product -> ProductResponseDto로 변환
        return products.map(product -> {
            boolean isOutOfStock = product.getOptions().stream()
                    .allMatch(option -> option.getStockQuantity() == 0); // 모든 옵션의 재고가 0인지 확인
            return mapToResponseDto(product, isOutOfStock);
        });
    }

    @Transactional
    public Page<ProductResponseDto> getProductsByCategory(Long categoryId, String sort, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sort));

        Page<Product> products = productRepository.findByCategoryId(categoryId, pageable);

        return products.map(product -> {
            boolean isOutOfStock = product.getOptions().stream()
                    .allMatch(option -> option.getStockQuantity() == 0); // 모든 옵션의 재고가 0인지 확인
            return mapToResponseDto(product, isOutOfStock);
        });
    }


    @Transactional
    public ProductResponseDto getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다. ID: " + productId));

        return mapToResponseDto(product);
    }

    // 상세페이지에서 호출할 때 (조회수 증가)
    @Transactional
    public ProductResponseDto getProductDetails(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다. ID: " + productId));

        // 조회수 증가
        product.setViewCount(product.getViewCount() + 1);

        // 변경 사항 저장
        productRepository.save(product);

        return mapToResponseDto(product);
    }


    // 상품 삭제
    @Transactional
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    // 부분 업데이트
    @Transactional
    public ProductResponseDto partialUpdateProduct(Long productId, Map<String, Object> updates, List<MultipartFile> images/* List<String> existingImageUrls */) {
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
                case "categoryId":
                    if (value instanceof Number) {
                        product.setCategoryId((long) ((Number) value).intValue());
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

        /*
        // 이미지 업데이트 로직
        if ((existingImageUrls != null && !existingImageUrls.isEmpty()) || (images != null && !images.isEmpty())) {
            // 기존 이미지 순서 업데이트
            if (existingImageUrls != null) {
                for (int i = 0; i < existingImageUrls.size(); i++) {
                    String imageUrl = existingImageUrls.get(i);
                    ProductImage existingImage = product.getImages().stream()
                            .filter(img -> img.getImageUrl().equals(imageUrl))
                            .findFirst()
                            .orElse(null);

                    if (existingImage != null) {
                        existingImage.setSortOrder(i + 1);
                    }
                }
            }


            // 불필요한 기존 이미지 삭제
            List<String> updatedUrls = new ArrayList<>();
            if (existingImageUrls != null) updatedUrls.addAll(existingImageUrls);
        }

         */


        // 새로운 이미지 추가
        if (images != null) {
            product.getImages().clear(); // 기존 이미지 제거
            for (int i = 0; i < images.size(); i++) {
                MultipartFile image = images.get(i);
                String imagePath = imageService.uploadImage("products", image);

                ProductImage newImage = new ProductImage();
                newImage.setImageUrl(imagePath);
                newImage.setSortOrder(i + 1);
                newImage.setProduct(product);
                product.getImages().add(newImage);
            }
        }

            product.setUpdatedAt(LocalDateTime.now());

            // 변경된 상품 저장
            Product updatedProduct = productRepository.save(product);

            // 수정된 상품 정보 반환
            return mapToResponseDto(updatedProduct);
        }


    @Transactional
    public List<String> getProductImageUrls(Long productId) {
        // 상품을 데이터베이스에서 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        // 상품 이미지 경로 리스트를 반환
        return product.getImages().stream() // 상품의 이미지 리스트를 스트림으로 변환
                .map(image -> "http://localhost:8080" + image.getImageUrl()) // 각 파일 이름에 URL 경로 추가
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponseDto getProductById(Long productId){
        return mapToResponseDto(productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다.")));
    }

    @Transactional
    public void incrementViewCount(Long productId, int viewCount) {
        productRepository.incrementViewCount(productId, viewCount);
    }

    @Transactional
    public List<ProductOptionDto> getAvailableSizes(Long productId, String color) {
        // Repository를 통해 데이터베이스에서 조건에 맞는 옵션들을 조회
        List<ProductOption> productOptions = productOptionRepository.findByProductIdAndColor(productId, color);

        // Entity -> DTO 변환
        return productOptions.stream()
                .map(option -> new ProductOptionDto(option.getSize(), option.getColor(), option.getStockQuantity()))
                .collect(Collectors.toList());
    }

    // 클라이언트 IP 가져오기
    @Transactional
    public String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    @Transactional
    public void decreaseProductStock(List<OrderDetailDto> orderDetailDtoList){
        orderDetailDtoList.forEach(orderDetailDto -> {
            Product product = productRepository.findById(orderDetailDto.getProductId()).orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

            for(ProductOption option : product.getOptions()){
                if(option.getSize() == orderDetailDto.getSize() && Objects.equals(option.getColor(), orderDetailDto.getColor())) {
                    int updatedStock = (int) (option.getStockQuantity() - orderDetailDto.getQuantity());
                    if(updatedStock < 0)
                        throw new IllegalStateException("상품 재고가 부족합니다.");
                    option.setStockQuantity(updatedStock);
                    break;
                }
            }
            productRepository.save(product);
        });
    }

    @Transactional
    public void productStockRollback(List<OrderDetailDto> orderDetailDtoList){
        orderDetailDtoList.forEach(orderDetailDto -> {
            Product product = productRepository.findById(orderDetailDto.getProductId()).orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

            for(ProductOption option : product.getOptions()){
                if(option.getSize() == orderDetailDto.getSize() && Objects.equals(option.getColor(), orderDetailDto.getColor())) {
                    int updatedStock = (int) (option.getStockQuantity() + orderDetailDto.getQuantity());
                    option.setStockQuantity(updatedStock);
                    break;
                }
            }
            productRepository.save(product);
        });
    }
}
