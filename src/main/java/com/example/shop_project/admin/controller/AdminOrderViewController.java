package com.example.shop_project.admin.controller;

import com.example.shop_project.member.service.MemberService;
import com.example.shop_project.order.service.OrderService;
import com.example.shop_project.order.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderViewController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public String totalOrderPage(Model model, @RequestParam(defaultValue = "") String email, @RequestParam(defaultValue = "all") String orderStatus, @RequestParam(defaultValue = "0") Integer page){
        model.addAttribute("orderPage", orderService.getTotalOrderPage(email, orderStatus, PageRequest.of(page, 10)));
        model.addAttribute("currentPage", page);
        model.addAttribute("selectedStatus", orderStatus);
        model.addAttribute("keyword", email);

        return "admin/admin_order_list";
    }

    @GetMapping("/{orderNo}")
    public String orderDetail(@PathVariable @ModelAttribute Long orderNo, Model model){
        model.addAttribute("detailList", orderService.getOrderDetailList(orderNo));
        model.addAttribute("order", orderService.getOrderByOrderNo(orderNo));
        model.addAttribute("payment", paymentService.getPaymentByOrderNo(orderNo));
        model.addAttribute("canceledOrder", orderService.getCanceledOrder(orderNo));
        return "admin/admin_order_detail";
    }
}
