package com.example.shop_project.order.controller;

import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.service.MemberService;
import com.example.shop_project.order.dto.OrderRequestDto;
import com.example.shop_project.order.dto.OrderResponseDto;
import com.example.shop_project.order.entity.OrderDetail;
import com.example.shop_project.order.entity.OrderStatus;
import com.example.shop_project.order.service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/order")
@Slf4j
public class OrderAPIController {
    @Autowired
    OrderService orderService;
    @Autowired
    MemberService memberService;

    public ResponseEntity<List<OrderResponseDto>> orderList(){
        return ResponseEntity.ok(orderService.getOrderList());
    }

    @GetMapping("/{orderNo}")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable(name = "orderNo") Long orderNo){
        OrderResponseDto response = orderService.getOrderByOrderNo(orderNo);
        log.warn("postNo : {}", response.getPostNo());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderNo}/order-detail")
    public ResponseEntity<List<OrderDetail>> getOrderDetails(@PathVariable Long orderNo){
        return ResponseEntity.ok(orderService.getOrderDetailList(orderNo));
    }

    @PostMapping(value = "/create")
    public ResponseEntity<OrderResponseDto> createOrder(@Validated @RequestBody OrderRequestDto orderRequestDto, HttpSession session){
        log.info("member : {}", orderRequestDto.getMember());
        OrderResponseDto response = orderService.createOrder(orderRequestDto);

        return ResponseEntity.created(URI.create("/" + response.getOrderNo())).body(response);
    }

    @PatchMapping("/{orderNo}/update")
    public ResponseEntity<OrderResponseDto> updateOrder(@PathVariable Long orderNo, @Validated OrderRequestDto orderRequestDto){
        OrderResponseDto response = orderService.updateOrder(orderNo, orderRequestDto);

        return ResponseEntity.created(URI.create("/" + response.getOrderNo())).body(response);
    }

    @DeleteMapping("{orderNo}/delete")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderNo){
        orderService.deleteOrder(orderNo);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{orderNo}/update-status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(@PathVariable Long orderNo, OrderStatus orderStatus){
        OrderResponseDto response = orderService.updateOrderStatus(orderNo, orderStatus);

        return ResponseEntity.created(URI.create("/" + response.getOrderNo())).body(response);
    }

    @GetMapping("/member-info")
    public ResponseEntity<Member> getMemberAddress(Principal principal){
        Member member = memberService.findByEmail(principal.getName());
        return ResponseEntity.ok(member);
    }
}
