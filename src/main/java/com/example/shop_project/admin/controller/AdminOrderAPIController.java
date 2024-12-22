package com.example.shop_project.admin.controller;

import com.example.shop_project.order.dto.OrderDetailDto;
import com.example.shop_project.order.dto.OrderResponseDto;
import com.example.shop_project.order.entity.OrderDetail;
import com.example.shop_project.order.entity.OrderStatus;
import com.example.shop_project.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/admin/order")
public class AdminOrderAPIController {
    @Autowired
    OrderService orderService;

    @PatchMapping("/{orderNo}/update-status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(@PathVariable Long orderNo, @RequestBody OrderStatus orderStatus){
        OrderResponseDto response = orderService.updateOrderStatus(orderNo, orderStatus);

        return ResponseEntity.created(URI.create("/" + response.getOrderNo())).body(response);
    }

    @DeleteMapping("/{orderNo}/delete")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderNo){
        orderService.deleteOrder(orderNo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{orderNo}")
    public ResponseEntity<List<OrderDetail>> orderDetail(@PathVariable Long orderNo){
        return ResponseEntity.ok(orderService.getOrderDetailList(orderNo));
    }
}
