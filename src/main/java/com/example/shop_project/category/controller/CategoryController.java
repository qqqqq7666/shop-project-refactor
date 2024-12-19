package com.example.shop_project.category.controller;

import com.example.shop_project.category.dto.CategoryCreateReqDto;
import com.example.shop_project.category.dto.CategoryUpdateReqDto;
import com.example.shop_project.category.service.CategoryService;
import com.example.shop_project.category.dto.CategoryResDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/category")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // 카테고리 목록 페이지
    @GetMapping("/categoryList")
    public ModelAndView getCategoryList() {
        List<CategoryResDto> categories = categoryService.getAllCategories();
        ModelAndView mav = new ModelAndView("category/categoryList");
        mav.addObject("categories", categories);
        return mav;
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
    public ResponseEntity<Void> deleteCategory(@RequestBody Map<String, Object> request) {
        log.debug("카테고리 삭제(controller) - 요청 ID: {}", request.get("categoryId"));
        Long categoryId = Long.valueOf(request.get("categoryId").toString());
        boolean isDeleted = categoryService.deleteCategory(categoryId);
        if(isDeleted) {
            return ResponseEntity.status(200).build();
        }
        return ResponseEntity.status(404).build();
    }
}
