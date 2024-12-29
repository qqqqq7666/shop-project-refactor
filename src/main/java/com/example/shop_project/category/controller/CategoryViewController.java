package com.example.shop_project.category.controller;

import com.example.shop_project.category.dto.CategoryResDto;
import com.example.shop_project.category.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CategoryViewController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryViewController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/admin/categories")
    public String categoryList(Model model) {
        List<CategoryResDto> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "category/categoryList";
    }
}
