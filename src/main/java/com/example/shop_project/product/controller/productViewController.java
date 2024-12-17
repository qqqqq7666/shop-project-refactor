package com.example.shop_project.product.controller;

import com.example.shop_project.product.dto.ProductResponseDto;
import com.example.shop_project.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/products")
public class productViewController {

    private final ProductService productService;

    public productViewController(ProductService productService) {
        this.productService = productService;
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

        model.addAttribute("productPage", productPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        return "products/productList";
    }

    @GetMapping("/detail/{productId}")
    public String getProductDetail(@PathVariable Long productId, Model model) {
        // 상품 정보를 조회
        ProductResponseDto product = productService.getProductDetail(productId);

        // 모델에 상품 데이터 추가
        model.addAttribute("product", product);
        return "products/productDetail";
    }

    // 상품 관리 페이지(관리자)
    @GetMapping("/manage")
    public String productList(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword, Model model) {
        List<ProductResponseDto> products = productService.getProductList(keyword != null ? keyword : "");
        if (products == null) {
            products = Collections.emptyList();
        }
        model.addAttribute("products", products);
        return "admin/productManage";
    }

    // 상품 수정 페이지
    @GetMapping("/edit/{productId}")
    public String editProductForm(@PathVariable Long productId, Model model) {
        ProductResponseDto product = productService.getProductById(productId);
        model.addAttribute("product", product);
        return "admin/editProductForm";
    }
}
