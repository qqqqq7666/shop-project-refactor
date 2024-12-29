package com.example.shop_project.product.controller;

import com.example.shop_project.category.dto.CategoryResDto;
import com.example.shop_project.category.service.CategoryService;
import com.example.shop_project.product.dto.ProductResponseDto;
import com.example.shop_project.product.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/products")
public class productViewController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final RedisTemplate<String, String> redisTemplate;

    public productViewController(ProductService productService, CategoryService categoryService, RedisTemplate<String, String> redisTemplate) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/create")
    public String showCreateProductPage() {
        return "products/create";
    }

    @GetMapping("/productList")
    public String productListPage(@RequestParam(value = "search", required = false, defaultValue = "") String search,
                                  @RequestParam(value = "sort", required = false, defaultValue = "createdAt") String sort,
                                  @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                  @RequestParam(value = "size", required = false, defaultValue = "8") int size,
                                  Model model) {
        Page<ProductResponseDto> productPage = productService.getProductList(search, sort, page, size);

        // 페이지네이션 블록 설정
        int blockSize = 5; // 페이지 블록 크기 설정
        int totalPages = productPage.getTotalPages();

        int currentBlock = page / blockSize; // 현재 블록 계산
        int startPage = currentBlock * blockSize + 1; // 시작 페이지 계산
        int endPage = Math.min(startPage + blockSize - 1, totalPages); // 끝 페이지 계산

        model.addAttribute("productPage", productPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        return "products/productList";
    }

    @GetMapping("/detail/{productId}")
    public String getProductDetail(@PathVariable("productId") Long productId, Model model, HttpSession session) {
        // 상품 정보 로드
        ProductResponseDto product = productService.getProductDetail(productId);
        model.addAttribute("product", product);

        // Redis에 상품 ID 저장 (TTL: 5초)
        redisTemplate.opsForValue().set("view:" + productId, "0", Duration.ofSeconds(70));


        return "products/productDetail";
    }

    // 조회수 증가 API
    @PostMapping("/detail/{productId}/confirm-view")
    public ResponseEntity<String> confirmView(@PathVariable("productId") Long productId) {
        log.debug("### API 요청 들어오는지 확인");
        // Redis에서 키 확인
        String key = "view:" + productId;
        if (redisTemplate.hasKey(key)) {
            // 조회수 증가
            // productService.incrementViewCount(productId);

            // Redis 키 삭제
            redisTemplate.delete(key);

            return ResponseEntity.ok("조회수 증가");
        }

        return ResponseEntity.badRequest().body("조회수 증가 X");
    }

    @Transactional
    @Scheduled(fixedRate = 6000) // 1분마다 실행
    public void syncViewCountsToDB() {
        log.debug("### syncViewCountsToDB 진입");
        Set<String> keys = redisTemplate.keys("view:*");
        if (keys != null) {
            for (String key : keys) {
                try {
                    Long productId = Long.valueOf(key.split(":")[1]);
                    String viewCountStr = redisTemplate.opsForValue().get(key);

                    // 값 검증
                    if (viewCountStr == null || !viewCountStr.matches("\\d+")) {
                        log.error("Redis key '{}' has invalid or missing value: {}", key, viewCountStr);
                        continue;
                    }

                    int viewCount = Integer.parseInt(viewCountStr);

                    // DB 업데이트
                    productService.incrementViewCount(productId, viewCount);
                    log.debug("Product {} view count incremented by {}", productId, viewCount);

                    // Redis 키 삭제
                    boolean deleted = Boolean.TRUE.equals(redisTemplate.delete(key));
                    log.debug("Redis key '{}' deleted: {}", key, deleted);

                } catch (Exception e) {
                    log.error("Failed to sync view count for key '{}'", key, e);
                }
            }
        } else {
            log.debug("No keys found in Redis for view counts.");
        }
    }




    // 수정 페이지 요청 처리
    @GetMapping("/edit/{productId}")
    public String editProductPage(@PathVariable Long productId, Model model) {
        // 상품 정보를 가져오기
        ProductResponseDto product = productService.getProductDetail(productId);

        // categoryId를 사용해 메인/서브 카테고리 정보 가져오기
        Map<String, CategoryResDto> categories = categoryService.getMainAndSubCategoryById(product.getCategoryId());

        // 메인 카테고리 목록 가져오기
        List<CategoryResDto> mainCategories = categoryService.getMainCategories();

        // 모델에 데이터 추가
        model.addAttribute("product", product);
        model.addAttribute("mainCategories", mainCategories);
        model.addAttribute("selectedMainCategory", categories.get("mainCategory"));
        model.addAttribute("selectedSubCategory", categories.get("subCategory"));

        return "products/editProduct";
    }

    // 이미지 url을 불러오기 위한 함수
    @GetMapping("/edit/{productId}/images")
    public ResponseEntity<List<String>> getProductImages(@PathVariable Long productId) {
        List<String> imageUrls = productService.getProductImageUrls(productId); // 이미지 URL 리스트 반환
        return ResponseEntity.ok(imageUrls); // List<String> 반환
    }

}
