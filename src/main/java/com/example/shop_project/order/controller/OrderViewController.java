package com.example.shop_project.order.controller;

import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.service.MemberService;
import com.example.shop_project.order.dto.OrderResponseDto;
import com.example.shop_project.order.entity.OrderStatus;
import com.example.shop_project.order.service.OrderService;
import com.example.shop_project.order.service.PaymentService;
import com.example.shop_project.point.dto.PointDto;
import com.example.shop_project.point.service.PointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Controller
@RequestMapping("/order")
@Slf4j
public class OrderViewController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PointService pointService;

    @GetMapping("/create")
    public String createOrder(){
        return "order/order_create";
    }

    // TODO order의 멤버와 principal로 찾은 맴버를 비교해서 다르면 exception 발생
    // TODO 결제 실패 시 우선 비공개 처리, 개선할 방법 고려
    @GetMapping("/{orderNo}")
    public String orderDetail(@PathVariable @ModelAttribute Long orderNo, Model model, Principal principal){
        OrderResponseDto orderResponseDto = orderService.getOrderByOrderNo(orderNo);
        if(!orderResponseDto.getMember().equals(memberService.findByEmail(principal.getName())))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "잘못된 접근입니다.");
        model.addAttribute("detailList", orderService.getOrderDetailList(orderNo));
        model.addAttribute("order", orderResponseDto);
        model.addAttribute("payment", paymentService.getPaymentByOrderNo(orderNo));
        model.addAttribute("point", pointService.getUsedPointByOrderNo(orderNo));
        model.addAttribute("orderStatusArray", OrderStatus.values());
        return "order/order_detail";
    }

    @GetMapping
    public String orderList(Model model, Principal principal, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "") String search){
        Page<OrderResponseDto> orderPage = orderService.getOrderPageList(principal, PageRequest.of(page, 10), search);
        if(orderPage.isEmpty())
            return "order/order_empty";
        model.addAttribute("orderPage", orderPage);
        model.addAttribute("currentPage", page);
        return "order/order_list";
    }

    @GetMapping("/{orderNo}/update")
    public String orderUpdate(@ModelAttribute @PathVariable Long orderNo){
        return "order/order_update";
    }

    @GetMapping("checkout")
    public String checkout(Principal principal, Model model){
        PointDto pointDto = pointService.getPointByMember(principal.getName());
        model.addAttribute("point", pointDto);
        return "order/checkout";
    }

    @GetMapping("cart")
    public String cart(){
        return "/order/cart/cart";
    }

    @GetMapping("modify-address/{orderNo}")
    public String modifyAddress(Model model, @PathVariable Long orderNo){
        model.addAttribute("orderNo", orderNo);
        return "/order/modify_address";
    }
}
