package com.example.shop_project.admin.controller;

import com.example.shop_project.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderViewController {
    @Autowired
    OrderService orderService;

    @GetMapping
    public String orderList(Model model){
        model.addAttribute("orderList", orderService.getOrderList());

        return "order/admin/order_list";
    }
}
