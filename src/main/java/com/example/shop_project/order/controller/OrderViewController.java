package com.example.shop_project.order.controller;

import com.example.shop_project.member.service.MemberService;
import com.example.shop_project.order.service.OrderService;
import com.example.shop_project.order.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/order")
@Slf4j
public class OrderViewController {
    @Autowired
    OrderService orderService;
    @Autowired
    MemberService memberService;
    @Autowired
    PaymentService paymentService;

    @GetMapping("/create")
    public String createOrder(){
        return "order/order_create";
    }

    // TODO order의 멤버와 principal로 찾은 맴버를 비교해서 다르면 exception 발생
    // TODO 결제 실패 시 우선 비공개 처리, 개선할 방법 고려
    // TODO 페이지네이션 필요
    @GetMapping("/{orderNo}")
    public String orderDetail(@PathVariable @ModelAttribute Long orderNo, Model model, Principal principal){
        model.addAttribute("detailList", orderService.getOrderDetailList(orderNo));
        model.addAttribute("order", orderService.getOrderByOrderNo(orderNo));
        model.addAttribute("member", memberService.findByEmail(principal.getName()));
        model.addAttribute("payment", paymentService.getPaymentByOrderNo(orderNo));
        return "order/order_detail";
    }

    @GetMapping
    public String orderList(Model model, Principal principal){
        model.addAttribute("orderMap", orderService.getOrderAndDetailMap(principal));
        return "order/order_list";
    }

    @GetMapping("/{orderNo}/update")
    public String orderUpdate(@ModelAttribute @PathVariable Long orderNo){
        return "order/order_update";
    }

    @GetMapping("checkout")
    public String checkout(@ModelAttribute Principal principal){
//        log.info("cookie: {}", jSession);
        return "order/checkout";
    }

    @GetMapping("cart")
    public String cart(){
        return "order/cart/cart";
    }
}
