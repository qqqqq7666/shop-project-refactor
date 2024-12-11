package com.example.shop_project.order.controller;

import com.example.shop_project.order.dto.OrderRequestDto;
import com.example.shop_project.order.dto.OrderResponseDto;
import com.example.shop_project.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/order")
@Slf4j
public class OrderAPIController {
    @Autowired
    OrderService orderService;

    @GetMapping("/{order-no}")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable(name = "order-no") Long orderNo){
        OrderResponseDto response = orderService.getOrderByOrderNo(orderNo);
        log.warn("postNo : {}", response.getPostNo());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<OrderResponseDto> createOrder(@Validated OrderRequestDto orderRequestDto){
        OrderResponseDto response = orderService.createOrder(orderRequestDto);

        return ResponseEntity.created(URI.create("/" + response.getOrderNo())).body(response);
    }
}
