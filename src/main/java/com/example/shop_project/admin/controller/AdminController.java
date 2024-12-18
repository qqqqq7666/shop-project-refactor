package com.example.shop_project.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
	@GetMapping()
    public String adminDashboard() {
        return "admin/adminMain"; // 관리자 메인 페이지
    }

    @GetMapping("/categories/categoryList")
    public String categoryList() { return "category/categoryList"; }
}
