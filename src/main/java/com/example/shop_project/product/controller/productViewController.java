package com.example.shop_project.product.controller;

import com.example.shop_project.category.dto.CategoryResDto;
import com.example.shop_project.category.service.CategoryService;
import com.example.shop_project.product.dto.ProductResponseDto;
import com.example.shop_project.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/products")
public class productViewController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public productViewController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
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
    public String getProductDetail(@PathVariable Long productId, Model model) {
        // 상품 정보를 조회
        ProductResponseDto product = productService.getProductDetails(productId);

        // 모델에 상품 데이터 추가
        model.addAttribute("product", product);
        return "products/productDetail";
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

        return "products/editProduct"; // 수정 페이지 템플릿
    }

    // 이미지 url을 불러오기 위한 함수
    @GetMapping("/edit/{productId}/images")
    public ResponseEntity<List<String>> getProductImages(@PathVariable Long productId) {
        List<String> imageUrls = productService.getProductImageUrls(productId); // 이미지 URL 리스트 반환
        return ResponseEntity.ok(imageUrls); // List<String> 반환
    }

}
