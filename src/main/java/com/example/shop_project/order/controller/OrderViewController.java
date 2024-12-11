package com.example.shop_project.order.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/order")
public class OrderViewController {
    @GetMapping("/create")
    public String createOrder(){
        return "order/order_create";
    }
}
