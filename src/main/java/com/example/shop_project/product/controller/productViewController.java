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
                                  @RequestParam(value = "categoryId", required = false) Long categoryId,
                                  Model model) {

        Page<ProductResponseDto> productPage = productService.getProductList(categoryId, search, sort, page, size);

        String subCategoryName = categoryId != null ? categoryService.getSubCategoryName(categoryId) : "";
        String mainCategoryName = categoryId != null ? categoryService.getMainCategoryName(categoryId) : "전체";


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
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("subCategoryName", subCategoryName);
        model.addAttribute("mainCategoryName", mainCategoryName);
        return "products/productList";
    }

    @GetMapping("/detail/{productId}")
    public String getProductDetail(@PathVariable("productId") Long productId, Model model, HttpSession session) {
        // 상품 정보 로드
        ProductResponseDto product = productService.getProductDetail(productId);
        model.addAttribute("product", product);
        model.addAttribute("productId", productId);

        // Redis에 상품 ID 저장 (TTL: 10초)
        // 조회수가 없으면 초기값 저장
        String key = "view:" + productId;
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.opsForValue().set(key, "1", Duration.ofSeconds(10));
        } else {
            // 조회수 증가
            redisTemplate.opsForValue().increment(key);
        }
        return "products/productDetail";
    }

    @PostMapping("/detail/{productId}/confirm-view")
    public ResponseEntity<String> confirmView(@PathVariable("productId") Long productId) {
        log.debug("### API 요청 들어오는지 확인");

        // 유효하지 않은 productId 처리
        if (productId == null || productId <= 0) {
            log.error("유효하지 않은 productId: {}", productId);
            return ResponseEntity.badRequest().body("유효하지 않은 상품 ID");
        }

        // Redis 키 확인
        String key = "view:" + productId;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            String viewCountStr = redisTemplate.opsForValue().get(key);
            if (viewCountStr == null) {
                log.error("Redis에서 조회수를 가져오지 못했습니다. 키: {}", key);
                return ResponseEntity.badRequest().body("조회수 정보가 없습니다.");
            }

            int viewCount;
            try {
                viewCount = Integer.parseInt(viewCountStr);
            } catch (NumberFormatException e) {
                log.error("조회수 변환 실패. 키: {}, 값: {}", key, viewCountStr, e);
                return ResponseEntity.badRequest().body("조회수 데이터 오류");
            }

            // 조회수 증가
            productService.incrementViewCount(productId, viewCount);
            log.info("조회수 증가: productId={}, 증가량={}", productId, viewCount);

            // Redis 키 삭제
            if (Boolean.TRUE.equals(redisTemplate.delete(key))) {
                log.debug("Redis 키 삭제 성공: {}", key);
            } else {
                log.warn("Redis 키 삭제 실패: {}", key);
            }

            return ResponseEntity.ok("조회수 증가");
        }

        log.warn("조회수 증가 요청, Redis 키 없음: {}", key);
        return ResponseEntity.badRequest().body("조회수 증가 X");
    }

    /*
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
*/



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
