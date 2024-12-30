package com.example.shop_project.admin.controller;

import com.example.shop_project.member.service.MemberService;
import com.example.shop_project.order.service.OrderService;
import com.example.shop_project.order.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderViewController {
    @Autowired
    OrderService orderService;
    @Autowired
    MemberService memberService;
    @Autowired
    PaymentService paymentService;

    @GetMapping
    public String orderList(Model model, @RequestParam(defaultValue = "date") String sort){
        model.addAttribute("orderList", orderService.getOrderList());

        return "admin/admin_order_list";
    }

    @GetMapping("/{orderNo}")
    public String orderDetail(@PathVariable @ModelAttribute Long orderNo, Model model){
        model.addAttribute("detailList", orderService.getOrderDetailList(orderNo));
        model.addAttribute("order", orderService.getOrderByOrderNo(orderNo));
        model.addAttribute("payment", paymentService.getPaymentByOrderNo(orderNo));
        return "admin/admin_order_detail";
    }
}
