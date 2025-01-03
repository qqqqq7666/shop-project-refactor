package com.example.shop_project.order.controller;

import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.service.MemberService;
import com.example.shop_project.order.dto.OrderResponseDto;
import com.example.shop_project.order.entity.OrderStatus;
import com.example.shop_project.order.service.OrderService;
import com.example.shop_project.order.service.PaymentService;
import com.example.shop_project.point.dto.PointDto;
import com.example.shop_project.point.service.PointService;
import com.example.shop_project.review.service.ReviewService;
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
import java.util.Map;

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
    @Autowired
    private ReviewService reviewService;

    @GetMapping("/{orderNo}")
    public String orderDetail(@PathVariable("orderNo") @ModelAttribute Long orderNo, Model model, Principal principal){
        OrderResponseDto orderResponseDto = orderService.getOrderByOrderNo(orderNo);
        if(!orderResponseDto.getMember().equals(memberService.findByEmail(principal.getName())))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "잘못된 접근입니다.");
        model.addAttribute("detailList", orderService.getOrderDetailList(orderNo));
        model.addAttribute("order", orderResponseDto);
        model.addAttribute("payment", paymentService.getPaymentByOrderNo(orderNo));
        model.addAttribute("point", pointService.getUsedPointByOrderNo(orderNo));
        model.addAttribute("orderStatusArray", OrderStatus.values());
        model.addAttribute("memberEmail", principal.getName());
        return "order/order_detail";
    }

    @GetMapping
    public String orderList(Model model, Principal principal, @RequestParam(value = "page",defaultValue = "0") int page, @RequestParam(value = "search", defaultValue = "") String search){
        Page<OrderResponseDto> orderPage = orderService.getOrderPageList(principal, PageRequest.of(page, 10), search);

        // 리뷰 작성 여부
        Map<Long, Boolean> reviewStatus = reviewService.getReviewStatusMap(orderPage);
        model.addAttribute("reviewStatus", reviewStatus);

        model.addAttribute("orderPage", orderPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", search);
        return "order/order_list";
    }

    @GetMapping("checkout")
    public String checkout(Principal principal, Model model){
        PointDto pointDto = pointService.getPointByMember(principal.getName());
        model.addAttribute("point", pointDto);
        model.addAttribute("recentOrderNo", orderService.getRecentOrder().getOrderNo());
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
