package com.example.shop_project.category.controller;

import com.example.shop_project.category.dto.CategoryCreateReqDto;
import com.example.shop_project.category.dto.CategoryDeleteResDto;
import com.example.shop_project.category.dto.CategoryUpdateReqDto;
import com.example.shop_project.category.service.CategoryService;
import com.example.shop_project.category.dto.CategoryResDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryAPIController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryAPIController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // 카테고리 목록(헤더)
    @GetMapping("/categoryList")
    public ResponseEntity<List<CategoryResDto>> getCategoryList() {
        List<CategoryResDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // 카테고리 추가
    @PostMapping("/create")
    public ResponseEntity<CategoryResDto> createCategory(@Valid @ModelAttribute CategoryCreateReqDto categoryCreateReqDto) {
        log.debug("카테고리 추가(controller) - {}", categoryCreateReqDto);

        CategoryResDto categoryResDto;
        if(categoryCreateReqDto.isCreatingMainCategory()) {
            categoryResDto = categoryService.createMainCategory(categoryCreateReqDto);
        } else {
            categoryResDto = categoryService.createSubCategory(categoryCreateReqDto);
        }
        return ResponseEntity.ok(categoryResDto);
    }

    // 카테고리 수정
    @PatchMapping("/update")
    public ResponseEntity<CategoryResDto> updateCategory(@Valid @RequestBody CategoryUpdateReqDto categoryUpdateReqDto) {
        CategoryResDto categoryResDto = categoryService.updateCategory(categoryUpdateReqDto);
        return ResponseEntity.ok(categoryResDto);
    }

    // 카테고리 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<CategoryDeleteResDto> deleteCategory(@RequestBody Map<String, Object> request) {
        log.debug("카테고리 삭제(controller) - 요청 ID: {}", request.get("categoryId"));
        Long categoryId = Long.valueOf(request.get("categoryId").toString());
        CategoryDeleteResDto categoryDeleteResDto = categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(categoryDeleteResDto);
    }
}
