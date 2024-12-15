package com.example.shop_project.category.controller;

import com.example.shop_project.category.dto.CategoryReqDto;
import com.example.shop_project.category.service.CategoryService;
import com.example.shop_project.category.dto.CategoryResDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/categories")
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
    public ResponseEntity<CategoryResDto> createCategory(@ModelAttribute CategoryReqDto categoryReqDto) {
        CategoryResDto categoryResDto = categoryService.createCategory(categoryReqDto);
        return ResponseEntity.ok(categoryResDto);
    }

}
