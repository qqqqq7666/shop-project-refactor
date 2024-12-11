package com.example.shop_project.order.controller;

import com.example.shop_project.order.dto.OrderDetailDto;
import com.example.shop_project.order.dto.OrderRequestDto;
import com.example.shop_project.order.dto.OrderResponseDto;
import com.example.shop_project.order.entity.Order;
import com.example.shop_project.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/order")
@Slf4j
public class OrderAPIController {
    @Autowired
    OrderService orderService;

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
    public ResponseEntity<List<OrderDetailDto>> getOrderDetails(@PathVariable Long orderNo){
        return ResponseEntity.ok(orderService.getOrderDetailList(orderNo));
    }

    @PostMapping("/create")
    public ResponseEntity<OrderResponseDto> createOrder(@Validated OrderRequestDto orderRequestDto){
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
}
