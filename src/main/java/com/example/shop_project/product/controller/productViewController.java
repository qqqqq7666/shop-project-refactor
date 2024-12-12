package com.example.shop_project.product.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/products")
public class productViewController {
    @GetMapping("/create")
    public String showCreateProductPage() {
        return "product/create"; // templates/create.html 파일을 렌더링
    }
}
